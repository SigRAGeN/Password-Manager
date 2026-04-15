package com.passwordmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

import static com.passwordmanager.EncryptionService.createHash;

public class FXMLPasswordDocumentController {

    @FXML
    private Button btnOk;

    @FXML
    private PasswordField txtfldPassword;

    private final String SALT_PATH = "secret.salt";
    private final String HASH_PATH = "master.hash";

    @FXML
    void btnOkOnActionHandler(ActionEvent event) {
        String inputPassword = txtfldPassword.getText();
        if (inputPassword.isEmpty()) return;

        try {
            ensureSaltExists();
            byte[] salt = loadSalt();

            if (!Files.exists(Paths.get(HASH_PATH))) {
                String newHash = createHash(inputPassword, salt);
                Files.write(Paths.get(HASH_PATH), newHash.getBytes());
                System.out.println("New Master password has been registered.");

                proceedToMainApp(inputPassword, salt, event);
            } else {
                String storedHash = new String(Files.readAllBytes(Paths.get(HASH_PATH))).trim();
                String currentInputHash = createHash(inputPassword, salt);

                if (currentInputHash.equals(storedHash)) {
                    System.out.println("Login successfull.");
                    proceedToMainApp(inputPassword, salt, event);
                } else {
                    showErrorAlert("Wrong Master password!");
                    txtfldPassword.clear();
                }
            }
        } catch (Exception e) {
            showErrorAlert("Unexpected error while logging in.");
            e.printStackTrace();
        }
    }

    private void proceedToMainApp(String password, byte[] salt, ActionEvent event) throws Exception {
        EncryptionService service = new EncryptionService(password, salt);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();

        FXMLDocumentController mainController = loader.getController();
        mainController.initService(service);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void ensureSaltExists() {
        java.nio.file.Path saltPath = Paths.get(SALT_PATH);
        try {
            if (!Files.exists(saltPath) || Files.size(saltPath) == 0) {
                byte[] salt = new byte[16];
                SecureRandom random = new SecureRandom();
                random.nextBytes(salt);
                Files.write(saltPath, salt);
                System.out.println("New salt generated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] loadSalt() {
        try {
            return Files.readAllBytes(Paths.get(SALT_PATH));
        } catch (Exception e) {
            return null;
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void initialize() {
    }
}