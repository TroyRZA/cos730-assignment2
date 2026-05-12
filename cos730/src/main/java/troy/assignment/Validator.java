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

    public boolean validateFormat(File file) {
        System.out.println("Validator: validateFormat(data)");
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            return false;
        }
        return validate(file);
    }

    private boolean validate(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return false;
            }

            headerLine = headerLine.replace("\uFEFF", "").trim();

            String[] headers = headerLine.split(DELIMITER);
            if (headers.length != EXPECTED_HEADERS.length) {
                return false;
            }

            for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
                if (!headers[i].equalsIgnoreCase(EXPECTED_HEADERS[i])) {
                    return false;
                }
            }

            String line;
            int rowCount = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                if (!validateRow(line.trim())) {
                    return false;
                }
                rowCount++;
            }

            return rowCount > 0;

        } catch (IOException e) {
            return false;
        }
    }

    private boolean validateRow(String line) {
        String[] columns = line.split(DELIMITER, 5);

        if (columns.length != 5 || Arrays.stream(columns).anyMatch(String::isBlank)) {
            return false;
        }

        try {
            int id = Integer.parseInt(columns[0]);
            if (id <= 0)
                return false;
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            LocalDate.parse(columns[3], DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return false;
        }

        try {
            double value = Double.parseDouble(columns[4]);
            if (value <= 0)
                return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}