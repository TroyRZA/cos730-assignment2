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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                new Validator(selectedFile);
                Database db = new Database();
                int submissionId = db.saveSubmission(selectedFile);
                ReviewerManager rm = new ReviewerManager(db);
                List<Reviewer> filteredReviewers = rm.getAvailableReviewers(submissionId);
                for (Reviewer reviewer : filteredReviewers) {
                    reviewer.assignReview(submissionId);
                }
                statusLabel.setText("Valid - " + filteredReviewers.size() + " reviewers available");
                statusLabel.setStyle("-fx-text-fill: green;");
            } catch (IllegalArgumentException e) {
                statusLabel.setText("Invalid");
                statusLabel.setStyle("-fx-text-fill: red;");
            } catch (IOException e) {
                statusLabel.setText("Error reading file");
                statusLabel.setStyle("-fx-text-fill: red;");
            } catch (SQLException e) {
                statusLabel.setText("Database error");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
        }
    }
}