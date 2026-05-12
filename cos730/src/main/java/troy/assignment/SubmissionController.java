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
        System.out.println("UI called: submit(selectedFile) on SubmissionController");
        Database db = new Database();
        int researchId = db.saveSubmission(data);
        System.out.println("SubmissionController called: displayMessage(result) on UI");
        onStatus.accept("Submission saved with ID: " + researchId);
    }
}