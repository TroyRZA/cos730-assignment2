package troy.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class Validator {

    private static final String[] EXPECTED_HEADERS = { "id", "title", "author", "date", "value" };
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DELIMITER = ",";

    public Validator(File file) throws IOException {
        System.out.println("SubmissionController called Validator: validateFormat(data)");
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            throw new IOException("File is not a CSV: " + file.getName());
        }
        validate(file);
    }

    private void validate(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("File is empty: " + file.getName());
            }

            headerLine = headerLine.replace("\uFEFF", "").trim();

            String[] headers = headerLine.split(DELIMITER);
            if (headers.length != EXPECTED_HEADERS.length) {
                throw new IllegalArgumentException("Invalid headers in: " + file.getName());
            }

            for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
                if (!headers[i].equalsIgnoreCase(EXPECTED_HEADERS[i])) {
                    throw new IllegalArgumentException("Invalid headers in: " + file.getName());
                }
            }

            String line;
            int rowCount = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                validateRow(line.trim(), file.getName());
                rowCount++;
            }

            if (rowCount == 0) {
                throw new IllegalArgumentException("No data rows in: " + file.getName());
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read file: " + file.getName());
        }
    }

    private void validateRow(String line, String fileName) {
        String[] columns = line.split(DELIMITER, 5);

        if (columns.length != 5 || Arrays.stream(columns).anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("Invalid row in: " + fileName);
        }

        try {
            int id = Integer.parseInt(columns[0]);
            if (id <= 0)
                throw new IllegalArgumentException("Invalid row in: " + fileName);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in: " + fileName);
        }

        try {
            LocalDate.parse(columns[3], DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid row in: " + fileName);
        }

        try {
            double value = Double.parseDouble(columns[4]);
            if (value <= 0)
                throw new IllegalArgumentException("Invalid row in: " + fileName);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in: " + fileName);
        }
    }
}
