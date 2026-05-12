package troy.assignment;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SubmissionController {
    @FXML
    private Label statusLabel;

    @FXML
    private void submitResearchOutput() {
        System.out.println("Researcher clicked: submitResearchOutput");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            long start = System.nanoTime();
            submit(selectedFile);
            System.out.printf("[Metrics] Execution time: %.3f ms%n", (System.nanoTime() - start) / 1_000_000.0);
        }
    }

    private void submit(File selectedFile) {
        System.out.println("UI: submit(data)");
        try {
            new Validator(selectedFile);
            Database db = new Database();
            int submissionId = db.saveSubmission(selectedFile);
            ReviewerManager rm = new ReviewerManager(db);
            List<Reviewer> filteredReviewers = rm.getAvailableReviewers(submissionId);
            statusLabel.setText("Valid - " + filteredReviewers.size() + " reviewers available");
            statusLabel.setStyle("-fx-text-fill: green;");
            NotificationService ns = new NotificationService(message -> javafx.application.Platform.runLater(() -> {
                statusLabel.setText(message);
                statusLabel.setStyle("-fx-text-fill: blue;");
                statusLabel.setVisible(true);
                statusLabel.setManaged(true);
            }));
            EvaluationManager em = new EvaluationManager(db, ns);
            System.out.println("loop [assign reviewers]");
            for (Reviewer reviewer : filteredReviewers) {
                reviewer.assignReview(submissionId);
            }
            em.startEvaluation(submissionId, filteredReviewers);
        } catch (IllegalArgumentException e) {
            System.out.println("SubmissionController: returnError()");
            statusLabel.setText("Invalid");
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (IOException e) {
            System.out.println("SubmissionController: returnError()");
            statusLabel.setText("Error reading file");
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (SQLException e) {
            System.out.println("SubmissionController: returnError()");
            statusLabel.setText("Database error");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}
