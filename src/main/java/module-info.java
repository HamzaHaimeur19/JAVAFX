module com.example.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi.ooxml;
    requires java.sql;
    requires poi;


    opens com.example.test to javafx.fxml;
    exports com.example.test;
}