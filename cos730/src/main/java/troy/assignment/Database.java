package troy.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:research.db";

    public Database() throws SQLException {
        try (Connection conn = connect()) {
            createTable(conn);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createTable(Connection conn) throws SQLException {
        String submissions = "CREATE TABLE IF NOT EXISTS submissions (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "value REAL NOT NULL" +
                ");";
        String assignments = "CREATE TABLE IF NOT EXISTS assignments (" +
                "reviewer_id INTEGER NOT NULL," +
                "submission_id INTEGER NOT NULL," +
                "PRIMARY KEY (reviewer_id, submission_id)," +
                "FOREIGN KEY (reviewer_id) REFERENCES reviewers(id)," +
                "FOREIGN KEY (submission_id) REFERENCES submissions(id)" +
                ");";
        String scores = "CREATE TABLE IF NOT EXISTS scores (" +
                "submission_id INTEGER NOT NULL," +
                "reviewer_id INTEGER NOT NULL," +
                "score REAL NOT NULL," +
                "PRIMARY KEY (submission_id, reviewer_id)," +
                "FOREIGN KEY (submission_id) REFERENCES submissions(id)," +
                "FOREIGN KEY (reviewer_id) REFERENCES reviewers(id)" +
                ");";
        conn.createStatement().execute(scores);
        conn.createStatement().execute(submissions);
        conn.createStatement().execute(assignments);
    }

    private List<String[]> parseCSV(File file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                rows.add(line.trim().split(",", 5));
            }
        }
        return rows;
    }

    public int saveSubmission(File file) throws IOException, SQLException {
        System.out.println("SubmissionController:  called saveSubmission(data) on Database");
        List<String[]> rows = parseCSV(file);
        String sql = "INSERT OR REPLACE INTO submissions (id, title, author, date, value) VALUES (?, ?, ?, ?, ?)";
        int firstId = -1;
        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String[] row : rows) {
                int id = Integer.parseInt(row[0]);
                if (firstId == -1)
                    firstId = id;
                stmt.setInt(1, id);
                stmt.setString(2, row[1]);
                stmt.setString(3, row[2]);
                stmt.setString(4, row[3]);
                stmt.setDouble(5, Double.parseDouble(row[4]));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
        return firstId;
    }

    public List<Reviewer> fetchReviewers() throws SQLException {
        System.out.println("ReviewerManager: called fetchReviewers() on Database");
        String sql = "SELECT r.id, r.name, r.assigned_study_id, COUNT(r2.id) as study_count " +
                "FROM reviewers r " +
                "LEFT JOIN reviewers r2 ON r2.assigned_study_id = r.assigned_study_id " +
                "GROUP BY r.id";
        List<Reviewer> reviewers = new ArrayList<>();
        try (Connection conn = connect();
                var rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                reviewers.add(new Reviewer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("assigned_study_id"),
                        rs.getInt("study_count"),
                        this));
            }
        }
        return reviewers;
    }

    public void assignReview(int reviewerId, int submissionId) throws SQLException {
        System.out.println("SubmissionController: assignReview()");
        String sql = "INSERT OR IGNORE INTO assignments (reviewer_id, submission_id) VALUES (?, ?)";
        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewerId);
            stmt.setInt(2, submissionId);
            stmt.executeUpdate();
        }
    }

    public void saveScore(int submissionId, int reviewerId, double score) throws SQLException {
        System.out.println("EvaluationManager called: saveScore(score) on Database");
        String sql = "INSERT OR REPLACE INTO scores (submission_id, reviewer_id, score) VALUES (?, ?, ?)";
        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submissionId);
            stmt.setInt(2, reviewerId);
            stmt.setDouble(3, score);
            stmt.executeUpdate();
        }
    }

}