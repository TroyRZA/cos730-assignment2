package troy.assignment;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UI {
    @FXML
    private Label statusLabel;

    @FXML
    private void submit() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            System.out.println("UI: calling Validator.validateFormat()");
            Validator validator = new Validator(this::displayMessage);
            boolean isValid = validator.validateFormat(selectedFile);

            if (!isValid) {
                displayError();
            }
        }
    }

    public void displayMessage(String message) {
        System.out.println("UI: displayMessage(" + message + ")");
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: blue;");
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
        });
    }

    private void displayError() {
        System.out.println("UI: displayError()");
        statusLabel.setText("Invalid");
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}