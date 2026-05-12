package troy.assignment;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SubmissionController {
    private final Consumer<String> onStatus;

    public SubmissionController(Consumer<String> onStatus) {
        this.onStatus = onStatus;
    }

    public void submit(File data) throws IOException, SQLException {
        long start = System.nanoTime();
        try {
            Database db = new Database();
            System.out.println("SubmissionController calling saveSubmission(data) on database");
            int researchId = db.saveSubmission(data);
            System.out.println("SubmissionController calling assignReviewers(researchId) on ReviewerManager");
            new ReviewerManager(db, new EvaluationManager(db, new NotificationService(onStatus)))
                    .assignReviewers(researchId);
        } finally {
            MetricsCollector.getInstance().recordRun(System.nanoTime() - start);
        }
    }
}