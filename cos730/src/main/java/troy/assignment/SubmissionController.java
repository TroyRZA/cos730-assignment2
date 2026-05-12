package troy.assignment;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class SubmissionController {
    private final Consumer<String> onStatus;

    public SubmissionController(Consumer<String> onStatus) {
        this.onStatus = onStatus;
    }

    public void submit(File selectedFile) throws IOException, SQLException {
        Database db = new Database();
        int submissionId = db.saveSubmission(selectedFile);
        ReviewerManager rm = new ReviewerManager(db);
        List<Reviewer> filteredReviewers = rm.getAvailableReviewers(submissionId);
        onStatus.accept("Valid - " + filteredReviewers.size() + " reviewers available");
        NotificationService ns = new NotificationService(message -> javafx.application.Platform.runLater(() -> onStatus.accept(message)));
        EvaluationManager em = new EvaluationManager(db, ns);
        System.out.println("loop [assign reviewers]");
        for (Reviewer reviewer : filteredReviewers) {
            reviewer.assignReview(submissionId);
        }
        em.startEvaluation(submissionId, filteredReviewers);
    }
}