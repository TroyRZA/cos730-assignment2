package troy.assignment;

public class Reviewer {
    private final int id;
    private final String name;
    private final int assignedStudyId;
    private final int studyCount;

    public Reviewer(int id, String name, int assignedStudyId, int studyCount) {
        this.id = id;
        this.name = name;
        this.assignedStudyId = assignedStudyId;
        this.studyCount = studyCount;
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

    public void assignReview(int submissionId) {
        // stub
    }
}