package troy.assignment;

import java.util.function.Consumer;

public class NotificationService {

    private final Consumer<String> uiCallback;

    public NotificationService(Consumer<String> uiCallback) {
        this.uiCallback = uiCallback;
    }

    public void notifyAcceptance() {
        uiCallback.accept("Submission accepted.");
    }

    public void notifyRejection() {
        uiCallback.accept("Submission rejected.");
    }

    public void notifyRevision() {
        uiCallback.accept("Submission requires revision.");
    }
}