package com.passwordmanager;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Comparator;

public class FXMLDocumentController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnSort;

    @FXML
    private Button btnSearch;

    @FXML
    private ListView<PasswordEntry> listViewMain;
    private ObservableList<PasswordEntry> data = FXCollections.observableArrayList();

    @FXML
    private ToggleButton toggleBtnShow;

    @FXML
    private TextField txtfldMail;

    @FXML
    private TextField txtfldNick;

    @FXML
    private TextField txtfldPassword;

    @FXML
    private TextField txtfldSearch;

    @FXML
    private TextField txtfldUrl;

    @FXML
    void btnAddOnActionHandler(ActionEvent event) {
        showAddPasswordDialog();
    }

    @FXML
    void btnSortOnActionHandler(ActionEvent event){
        data.sort(Comparator.comparing(e -> e.getNick().toLowerCase()));
    }

    @FXML
    void btnSearchOnActionHandler(ActionEvent event) {
        String query = txtfldSearch.getText().toLowerCase();

        if (query.isEmpty()) return;

        List<PasswordEntry> matches = data.stream()
                .filter(e -> e.getNick().toLowerCase().contains(query))
                .collect(Collectors.toList());

        if (!matches.isEmpty()) {
            data.removeAll(matches);
            data.addAll(0, matches);
            int i = 0;
            for(PasswordEntry e : matches) {
                listViewMain.getSelectionModel().select(i);
                i++;
            }
            listViewMain.scrollTo(0);

            System.out.println("Found: " + matches.size());
        } else {
            System.out.println("Nothing found");
        }
    }

    @FXML
    void setListViewMainOnMouseClickedHandler(MouseEvent event) {
    }

    @FXML
    void toggleBtnShowOnActionHandler(ActionEvent event) {
        PasswordEntry selected = listViewMain.getSelectionModel().getSelectedItem();

        if (selected == null) {
            toggleBtnShow.setSelected(false);
            return;
        }

        if (toggleBtnShow.isSelected()) {
            String zadaneHeslo = showPasswordPrompt();
            try {
                byte[] salt = Files.readAllBytes(Paths.get("secret.salt"));
                String storedHash = new String(Files.readAllBytes(Paths.get("master.hash"))).trim();
                String inputHash = EncryptionService.createHash(zadaneHeslo, salt);

                if (inputHash.equals(storedHash)) {
                    String decrypted = service.decrypt(selected.getPassword());
                    txtfldPassword.setText(decrypted);
                    toggleBtnShow.setText("hide");
                } else {
                    toggleBtnShow.setSelected(false);
                    showErrorAlert("Wrong Master password!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            txtfldPassword.setText("********");
            toggleBtnShow.setText("show");
        }
    }

    @FXML
    void txtfldSearchOnKeyPressed(KeyEvent event) {

    }

    public void initialize() {
        listViewMain.setItems(data);

        listViewMain.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            PasswordEntry selected = newValue;
            if (selected != null) {
                try {
                    txtfldNick.setText(selected.getNick());
                    txtfldUrl.setText(selected.getUrl());
                    txtfldMail.setText(selected.getMail());
                    toggleBtnShow.setSelected(false);
                    toggleBtnShow.setText("show");
                    updatePasswordField(selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("First pick an item from the list!");
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Record");

        deleteItem.setOnAction(event -> {
            PasswordEntry selected = listViewMain.getSelectionModel().getSelectedItem();

            if (selected != null) {
                try {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Delete record?");
                    alert.setHeaderText("Want to remove this record?");
                    alert.setContentText("Do you wish to delete: " + selected.getNick());

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        listViewMain.getSelectionModel().clearSelection();
                        data.remove(selected);
                        savePasswordsToFile();
                        txtfldNick.clear();
                        txtfldUrl.clear();
                        txtfldMail.clear();
                        txtfldPassword.clear();
                        toggleBtnShow.setSelected(false);
                    } else {
                        System.out.println("deletion canceled");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        contextMenu.getItems().add(deleteItem);
        listViewMain.setContextMenu(contextMenu);
    }

    public String showPasswordPrompt() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Master Password");
        dialog.setHeaderText("Put in your master password:");

        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("password");

        dialog.getDialogPane().setContent(passwordField);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return passwordField.getText().strip();
            }else{
                toggleBtnShow.setSelected(false);
                return null;
            }
        });

        return dialog.showAndWait().orElse("");
    }

    private EncryptionService service;

    public void initService(EncryptionService service) {
        this.service = service;
        System.out.println("Vault unlocked service passed on!");
        loadPasswordsFromFile();
    }

    private void updatePasswordField(PasswordEntry entry) {
        if (entry == null) return;

        if (toggleBtnShow.isSelected()) {
            try {
                String decrypted = service.decrypt(entry.getPassword());
                txtfldPassword.setText(decrypted);
            } catch (Exception e) {
                txtfldPassword.setText("Error!");
                e.printStackTrace();
            }
        } else {
            txtfldPassword.setText("********");
        }
    }

    public void showAddPasswordDialog() {
        Dialog<PasswordEntry> dialog = new Dialog<>();
        dialog.setTitle("Add New Password");
        dialog.setHeaderText("Enter your data");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nick = new TextField();
        nick.setPromptText("Web");
        TextField url = new TextField();
        url.setPromptText("URL");
        TextField email = new TextField();
        email.setPromptText("E-mail");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Web:"), 0, 0);
        grid.add(nick, 1, 0);
        grid.add(new Label("URL:"), 0, 1);
        grid.add(url, 1, 1);
        grid.add(new Label("E-mail:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(password, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String encrypted = service.encrypt(password.getText());
                    return new PasswordEntry(nick.getText(), url.getText(), email.getText(), encrypted);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newEntry -> {
            data.add(newEntry);
            savePasswordsToFile();
            System.out.println("Saved: " + newEntry.getNick());
        });
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private final String DB_FILE = "passwords.dat";
    private void loadPasswordsFromFile() {
        Path path = Paths.get(DB_FILE);
        if (!Files.exists(path)) return;

        try {
            List<String> lines = Files.readAllLines(path);
            data.clear();
            for (String line : lines) {
                String[] p = line.split(";", -1);
                if (p.length == 4) {
                    data.add(new PasswordEntry(p[0], p[1], p[2], p[3]));
                }
            }
        } catch (Exception e) {
            System.err.println("Error while loading: " + e.getMessage());
        }
    }

    private void savePasswordsToFile() {
        try {
            List<String> lines = new ArrayList<>();
            for (PasswordEntry e : data) {
                lines.add(e.getNick() + ";" + e.getUrl() + ";" + e.getMail() + ";" + e.getPassword());
            }
            Files.write(Paths.get(DB_FILE), lines);
        } catch (Exception e) {
            System.err.println("Error while saving: " + e.getMessage());
        }
    }
}
