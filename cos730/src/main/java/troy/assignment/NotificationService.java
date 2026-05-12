package troy.assignment;

import java.util.function.Consumer;

public class NotificationService {

    private final Consumer<String> uiCallback;

    public NotificationService(Consumer<String> uiCallback) {
        this.uiCallback = uiCallback;
    }

    public void notify(String result) {
        System.out.println("EvaluationManager called: notify(result) on NotificationService");
        switch (result) {
            case "accepted":
                uiCallback.accept("Submission accepted.");
                break;
            case "revision":
                uiCallback.accept("Submission requires revision.");
                break;
            case "rejected":
                uiCallback.accept("Submission rejected.");
                break;
        }
    }
}