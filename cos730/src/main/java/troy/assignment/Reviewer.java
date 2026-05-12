package troy.assignment;

import java.sql.SQLException;

public class Reviewer {
    private final int id;
    private final String name;
    private final int assignedStudyId;
    private final int studyCount;
    private final Database database;

    public Reviewer(int id, String name, int assignedStudyId, int studyCount, Database database) {
        this.id = id;
        this.name = name;
        this.assignedStudyId = assignedStudyId;
        this.studyCount = studyCount;
        this.database = database;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAssignedStudyId() {
        return assignedStudyId;
    }

    public int getStudyCount() {
        return studyCount;
    }

    public void assignReview(int submissionId) throws SQLException {
        System.out.println("SubmissionController: called assignReview() on Reviewer");
        database.assignReview(this.id, submissionId);
    }

    public double submitScore() {
        System.out.println("EvaluationManager: called submitScore() on Reviewer");
        return Math.round(Math.random() * 100);
    }
}