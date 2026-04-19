module troy.assignment {
    requires javafx.controls;
    requires javafx.fxml;

    opens troy.assignment to javafx.fxml;
    exports troy.assignment;
}
