package troy.assignment;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UI {
    @FXML
    private Label statusLabel;

    @FXML
    private void submit() {
        System.out.println("Researcher clicked: submitResearchOutput");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            System.out.println("UI: submit(data)");
            Validator validator = new Validator();
            boolean isValid = validator.validateFormat(selectedFile);

            if (!isValid) {
                displayError();
            } else {
                try {
                    SubmissionController sc = new SubmissionController(message -> javafx.application.Platform.runLater(() -> {
                        statusLabel.setText(message);
                        statusLabel.setStyle("-fx-text-fill: blue;");
                        statusLabel.setVisible(true);
                        statusLabel.setManaged(true);
                    }));
                    sc.submit(selectedFile);
                    statusLabel.setVisible(true);
                    statusLabel.setManaged(true);
                } catch (IOException e) {
                    statusLabel.setText("Error reading file");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setVisible(true);
                    statusLabel.setManaged(true);
                } catch (SQLException e) {
                    statusLabel.setText("Database error");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setVisible(true);
                    statusLabel.setManaged(true);
                }
            }
        }
    }

    private void displayError() {
        System.out.println("UI: displayError()");
        statusLabel.setText("Invalid");
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}