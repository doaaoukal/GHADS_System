/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.OrganizationDAO;
import dao.UserDAO;
import model.Organization;
import model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import util.BaseController;

public class UserController extends BaseController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colOrg;

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private ComboBox<Organization> orgCombo;
    @FXML private ImageView photoView;
    @FXML private Label statusLabel;

    private UserDAO userDAO = new UserDAO();
    private OrganizationDAO orgDAO = new OrganizationDAO();
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private User currentUser;
    private User selectedUser;
    private byte[] selectedPhoto;

    public void initData(User user) {
        this.currentUser = user;
        setupTable();
        setupCombos();
        loadUsers();
        setupTableSelection();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colOrg.setCellValueFactory(new PropertyValueFactory<>("orgName")); 
    }

    private void setupCombos() {
        roleCombo.setItems(FXCollections.observableArrayList("COORDINATOR"));
        List<Organization> orgs = orgDAO.getAllOrganizations();
        orgCombo.setItems(FXCollections.observableArrayList(orgs));
    }

    private void loadUsers() {
        userList.setAll(userDAO.getAllUsers());
        userTable.setItems(userList);
    }

    private void setupTableSelection() {
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedUser = newVal;
                fullNameField.setText(newVal.getFullName());
                usernameField.setText(newVal.getUsername());
                passwordField.setText(newVal.getPassword());
                emailField.setText(newVal.getEmail());
                roleCombo.setValue(newVal.getRole());

                // Set org in combo
                orgCombo.getItems().stream()
                        .filter(o -> o.getOrgId() == newVal.getOrgId())
                        .findFirst()
                        .ifPresent(orgCombo::setValue);

                // Show photo (Bonus)
                if (newVal.getPhoto() != null) {
                    photoView.setImage(new Image(new ByteArrayInputStream(newVal.getPhoto())));
                    selectedPhoto = newVal.getPhoto();
                }
            }
        });
    }

    // Bonus: Upload photo
    @FXML
    public void handleUploadPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Photo");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(photoView.getScene().getWindow());
        if (file != null) {
            try {
                selectedPhoto = Files.readAllBytes(file.toPath());
                photoView.setImage(new Image(new ByteArrayInputStream(selectedPhoto)));
            } catch (Exception e) {
                showStatus("Failed to load photo!", false);
            }
        }
    }

    @FXML
    public void handleAdd() {
        if (!validateInputs(true)) return;

        if (userDAO.isUsernameExists(usernameField.getText().trim())) {
            showStatus("Username already exists!", false);
            return;
        }
        if (userDAO.isEmailExists(emailField.getText().trim())) {
            showStatus("Email already exists!", false);
            return;
        }

        User user = new User(0,
                usernameField.getText().trim(),
                passwordField.getText().trim(),
                fullNameField.getText().trim(),
                emailField.getText().trim(),
                roleCombo.getValue(),
                orgCombo.getValue().getOrgId(),
                selectedPhoto
        );

        if (userDAO.addUser(user)) {
            showStatus("User added successfully!", true);
            handleReset();
            loadUsers();
        } else {
            showStatus("Failed to add user!", false);
        }
    }

    @FXML
    public void handleUpdate() {
        if (selectedUser == null) {
            showStatus("Please select a user to update.", false);
            return;
        }
        if (!validateInputs(false)) return;

        selectedUser.setFullName(fullNameField.getText().trim());
        selectedUser.setUsername(usernameField.getText().trim());
        selectedUser.setPassword(passwordField.getText().trim());
        selectedUser.setEmail(emailField.getText().trim());
        selectedUser.setRole(roleCombo.getValue());
        selectedUser.setOrgId(orgCombo.getValue().getOrgId());
        selectedUser.setPhoto(selectedPhoto);

        if (userDAO.updateUser(selectedUser)) {
            showStatus("User updated successfully!", true);
            handleReset();
            loadUsers();
        } else {
            showStatus("Failed to update user!", false);
        }
    }

    @FXML
    public void handleDelete() {
        if (selectedUser == null) {
            showStatus("Please select a user to delete.", false);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete \"" + selectedUser.getFullName() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                if (userDAO.deleteUser(selectedUser.getUserId())) {
                    showStatus("User deleted successfully!", true);
                    handleReset();
                    loadUsers();
                } else {
                    showStatus("Failed to delete user!", false);
                }
            }
        });
    }

    @FXML
    public void handleReset() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        roleCombo.setValue(null);
        orgCombo.setValue(null);
        photoView.setImage(null);
        selectedPhoto = null;
        selectedUser = null;
        userTable.getSelectionModel().clearSelection();
        statusLabel.setText("");
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboard.fxml"));
            Parent root = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) fullNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }

    private boolean validateInputs(boolean checkPassword) {
        if (fullNameField.getText().trim().isEmpty() ||
            usernameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            roleCombo.getValue() == null ||
            orgCombo.getValue() == null) {
            showStatus("All fields are required!", false);
            return false;
        }
        if (checkPassword && passwordField.getText().trim().length() < 8) {
            showStatus("Password must be at least 8 characters!", false);
            return false;
        }
        if (!emailField.getText().contains("@")) {
            showStatus("Invalid email format!", false);
            return false;
        }
        return true;
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
    }
}
