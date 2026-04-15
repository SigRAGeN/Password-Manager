module com.example.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.passwordmanager to javafx.fxml;
    exports com.passwordmanager;
}