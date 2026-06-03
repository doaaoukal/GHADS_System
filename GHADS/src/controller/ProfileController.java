/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.UserDAO;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import util.BaseController;

public class ProfileController extends BaseController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private Label roleLabel;
    @FXML private Label orgLabel;
    @FXML private ImageView photoView;
    @FXML private Label statusLabel;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;
    private byte[] selectedPhoto;

    public void initData(User user) {
        this.currentUser = user;
        loadProfile();
    }

    private void loadProfile() {
        fullNameField.setText(currentUser.getFullName());
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
        roleLabel.setText(currentUser.getRole());

        if (currentUser.getPhoto() != null) {
            photoView.setImage(new Image(new ByteArrayInputStream(currentUser.getPhoto())));
            selectedPhoto = currentUser.getPhoto();
        }
    }

    @FXML
    public void handleUploadPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Profile Photo");
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
    public void handleUpdate() {
        if (fullNameField.getText().trim().isEmpty() ||
            usernameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty()) {
            showStatus("All fields are required!", false);
            return;
        }

        currentUser.setFullName(fullNameField.getText().trim());
        currentUser.setUsername(usernameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setPhoto(selectedPhoto);

        if (userDAO.updateUser(currentUser)) {
            showStatus("Profile updated successfully! ✅", true);
        } else {
            showStatus("Failed to update profile!", false);
        }
    }

    @FXML
    public void handleReset() {
        loadProfile(); 
        selectedPhoto = currentUser.getPhoto();
        statusLabel.setText("");
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CoordinatorDashboard.fxml"));
            Parent root = loader.load();
            ((CoordinatorDashboardController) loader.getController()).initData(currentUser);
            Stage stage = (Stage) fullNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
    }
}
