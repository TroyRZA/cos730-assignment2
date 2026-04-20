module troy.assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens troy.assignment to javafx.fxml;

    exports troy.assignment;
}
