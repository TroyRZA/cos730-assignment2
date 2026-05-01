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
        String sql = "CREATE TABLE IF NOT EXISTS submissions (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "value REAL NOT NULL" +
                ");";
        conn.createStatement().execute(sql);
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
                        rs.getInt("study_count")));
            }
        }
        return reviewers;
    }
}