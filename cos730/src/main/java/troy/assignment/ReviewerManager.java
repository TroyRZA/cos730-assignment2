package troy.assignment;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewerManager {

    private final Database database;

    public ReviewerManager(Database database) {
        this.database = database;
    }

    public List<String[]> getAvailableReviewers(int submissionId) throws SQLException {
        List<String[]> reviewers = database.fetchReviewers();
        reviewers = filterConflicts(reviewers, submissionId);
        reviewers = checkWorkload(reviewers);
        return reviewers;
    }

    private List<String[]> filterConflicts(List<String[]> reviewers, int submissionId) {
        return reviewers.stream()
                .filter(r -> !String.valueOf(submissionId).equals(r[2]))
                .collect(Collectors.toList());
    }

    private List<String[]> checkWorkload(List<String[]> reviewers) {
        return reviewers.stream()
                .filter(r -> Integer.parseInt(r[2]) <= 5)
                .collect(Collectors.toList());
    }
}