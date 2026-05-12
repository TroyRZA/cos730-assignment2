package troy.assignment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

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
        System.out.println("ReviewerManager: called startEvaluation() on EvaluationManager");
        scores = new ArrayList<>();
        Map<Integer, Double> reviewerScores = new LinkedHashMap<>();
        System.out.println("loop [reviewers]");
        for (Reviewer reviewer : reviewers) {
            double score = reviewer.submitScore();
            reviewerScores.put(reviewer.getId(), score);
            scores.add(score);
        }
        database.saveScores(submissionId, reviewerScores);
        System.out.println("EvaluationManager self-called: evaluate()");
        String result = evaluate();
        System.out.println("EvaluationManager evaluate() result: " + result);
    }

    private String evaluate() {
        double average = scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double standardDeviation = Math.sqrt(
                scores.stream()
                        .mapToDouble(s -> Math.pow(s - average, 2))
                        .average()
                        .orElse(0.0));
        boolean consensus = standardDeviation <= CONSENSUS_MAX_DEVIATION;

        if (average >= ACCEPT_THRESHOLD && consensus) {
            System.out.println("EvaluationManager called: notifyAcceptance() on notificationService");
            notificationService.notifyAcceptance();
            return "accepted";
        } else if (average >= REVISION_THRESHOLD) {
            System.out.println("EvaluationManager called: notifyRevision() on notificationService");
            notificationService.notifyRevision();
            return "revision";
        } else {
            System.out.println("EvaluationManager called: notifyRejection() on notificationService");
            notificationService.notifyRejection();
            return "rejected";
        }
    }
}