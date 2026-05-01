package troy.assignment;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewerManager {

    private final Database database;

    public ReviewerManager(Database database) {
        this.database = database;
    }

    public List<Reviewer> getAvailableReviewers(int submissionId) throws SQLException {
        List<Reviewer> reviewers = database.fetchReviewers();
        reviewers = filterConflicts(reviewers, submissionId);
        reviewers = checkWorkload(reviewers);
        // filteredReviewers()
        return reviewers;
    }

    private List<Reviewer> filterConflicts(List<Reviewer> reviewers, int submissionId) {
        return reviewers.stream()
                .filter(r -> r.getAssignedStudyId() != submissionId)
                .collect(Collectors.toList());
    }

    private List<Reviewer> checkWorkload(List<Reviewer> reviewers) {
        return reviewers.stream()
                .filter(r -> r.getStudyCount() <= 5)
                .collect(Collectors.toList());
    }

}