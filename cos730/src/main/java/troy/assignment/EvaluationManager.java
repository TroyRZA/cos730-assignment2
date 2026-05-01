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
        System.out.println("SubmissionController: called startEvaluation() on EvaluationManager");
        scores = new java.util.ArrayList<>();
        System.out.println("loop [each reviewer]");
        for (Reviewer reviewer : reviewers) {
            double score = submitScore(submissionId);
            database.saveScore(submissionId, reviewer.getId(), score);
            scores.add(score);
        }
        System.out.println("EvaluationManager self-called: calculateAverage()");
        double average = calculateAverage();
        System.out.println("EvaluationManager self-called: checkConsensus()");
        boolean consensus = checkConsensus(average);
        System.out.println("EvaluationManager self-called: applyRules()");
        applyRules(average, consensus);
    }

    private double calculateAverage() {
        return scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private boolean checkConsensus(double average) {
        double standardDeviation = Math.sqrt(
                scores.stream()
                        .mapToDouble(s -> Math.pow(s - average, 2))
                        .average()
                        .orElse(0.0));
        return standardDeviation <= CONSENSUS_MAX_DEVIATION;
    }

    public double submitScore(int submissionId) {
        System.out.println("Reviewer: called submitScore on EvaluationManager");
        return Math.round(Math.random() * 100);
    }

    private void applyRules(double average, boolean consensus) {
        System.out.println("alt [accepted]");
        if (average >= ACCEPT_THRESHOLD && consensus) {
            System.out.println("EvaluationManager called: notifyAcceptance() on notificationService");
            notificationService.notifyAcceptance();
        } else if (average >= REVISION_THRESHOLD) {
            System.out.println("EvaluationManager called: notifyRevision() on notificationService");
            notificationService.notifyRevision();
        } else {
            System.out.println("EvaluationManager called: notifyRejection() on notificationService");
            notificationService.notifyRejection();
        }
    }
}
