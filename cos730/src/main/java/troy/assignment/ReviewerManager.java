package troy.assignment;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewerManager {

    private final Database database;
    private final EvaluationManager evaluationManager;

    public ReviewerManager(Database database, EvaluationManager evaluationManager) {
        this.database = database;
        this.evaluationManager = evaluationManager;
    }

    public void assignReviewers(int researchId) throws SQLException {
        System.out.println("ReviewerManager: called assignReviewers(researchId)");
        List<Reviewer> reviewers = database.fetchReviewers();
        List<Reviewer> filteredReviewers = selectReviewers(reviewers, researchId);
        for (Reviewer reviewer : filteredReviewers) {
            reviewer.assignReview(researchId);
        }
        evaluationManager.startEvaluation(researchId, filteredReviewers);
    }

    private List<Reviewer> selectReviewers(List<Reviewer> reviewerList, int researchId) {
        System.out.println("ReviewerManager: self-called selectReviewers(reviewerList)");
        return reviewerList.stream()
                .filter(r -> r.getAssignedStudyId() != researchId)
                .filter(r -> r.getStudyCount() <= 5)
                .collect(Collectors.toList());
    }

}