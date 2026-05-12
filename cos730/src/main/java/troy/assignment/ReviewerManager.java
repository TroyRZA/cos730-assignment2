package troy.assignment;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewerManager {

    private final Database database;

    public ReviewerManager(Database database) {
        this.database = database;
    }

    public void assignReviewers(int researchId) throws SQLException {
        System.out.println("ReviewerManager: called assignReviewers(researchId)");
        List<Reviewer> reviewers = database.fetchReviewers();
        reviewers = filterConflicts(reviewers, researchId);
        reviewers = checkWorkload(reviewers);
        for (Reviewer reviewer : reviewers) {
            database.assignReview(reviewer.getId(), researchId);
        }
    }

    private List<Reviewer> filterConflicts(List<Reviewer> reviewers, int researchId) {
        System.out.println("ReviewerManager: self-called filterConflicts(reviewerList)");
        return reviewers.stream()
                .filter(r -> r.getAssignedStudyId() != researchId)
                .collect(Collectors.toList());
    }

    private List<Reviewer> checkWorkload(List<Reviewer> reviewers) {
        System.out.println("ReviewerManager: self-called checkWorkload(reviewerList)");
        return reviewers.stream()
                .filter(r -> r.getStudyCount() <= 5)
                .collect(Collectors.toList());
    }

}