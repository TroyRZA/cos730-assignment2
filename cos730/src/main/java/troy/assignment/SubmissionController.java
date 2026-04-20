package troy.assignment;

import java.io.File;
import java.io.IOException;
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
                statusLabel.setText("Valid");
                statusLabel.setStyle("-fx-text-fill: green;");
            } catch (IllegalArgumentException e) {
                statusLabel.setText("Invalid");
                statusLabel.setStyle("-fx-text-fill: red;");
            } catch (IOException e) {
                statusLabel.setText("Error reading file");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
        }
    }
}
