package troy.assignment;

import java.sql.SQLException;
import java.util.List;

public class EvaluationManager {

    private static final double ACCEPT_THRESHOLD = 70.0;
    private static final double REVISION_THRESHOLD = 50.0;
    private static final double CONSENSUS_MAX_DEVIATION = 15.0;

    private final Database database;
    private final NotificationService notificationService;
    private List<Double> scores;

    public EvaluationManager(Database database, NotificationService notificationService) {
        this.database = database;
        this.notificationService = notificationService;
    }

    public void startEvaluation(int submissionId, List<Reviewer> reviewers) throws SQLException {
        scores = new java.util.ArrayList<>();
        for (Reviewer reviewer : reviewers) {
            double score = reviewer.submitScore(submissionId);
            database.saveScore(submissionId, reviewer.getId(), score);
            scores.add(score);
        }
        double average = calculateAverage();
        boolean consensus = checkConsensus();
        applyRules(average, consensus);
    }

    private double calculateAverage() {
        return scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private boolean checkConsensus() {
        double average = calculateAverage();
        double standardDeviation = Math.sqrt(
                scores.stream()
                        .mapToDouble(s -> Math.pow(s - average, 2))
                        .average()
                        .orElse(0.0));
        return standardDeviation <= CONSENSUS_MAX_DEVIATION;
    }

    private void applyRules(double average, boolean consensus) {
        if (average >= ACCEPT_THRESHOLD && consensus) {
            notificationService.notifyAcceptance();
        } else if (average >= REVISION_THRESHOLD) {
            notificationService.notifyRevision();
        } else {
            notificationService.notifyRejection();
        }
    }
}