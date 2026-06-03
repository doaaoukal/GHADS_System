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
import javafx.stage.Stage;
import util.BaseController;

public class ChangePasswordController extends BaseController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
    }

    @FXML
    public void handleChangePassword() {
        String current = currentPasswordField.getText().trim();
        String newPass = newPasswordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showStatus("All fields are required!", false);
            return;
        }
        if (!current.equals(currentUser.getPassword())) {
            showStatus("Current password is incorrect!", false);
            return;
        }
        if (newPass.length() < 8) {
            showStatus("New password must be at least 8 characters!", false);
            return;
        }
        if (!newPass.equals(confirm)) {
            showStatus("Passwords do not match!", false);
            return;
        }

        if (userDAO.changePassword(currentUser.getUserId(), newPass)) {
            currentUser.setPassword(newPass);
            showStatus("Password changed successfully! ✅", true);
            handleReset();
        } else {
            showStatus("Failed to change password!", false);
        }
    }

    @FXML
    public void handleReset() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        statusLabel.setText("");
    }

    @FXML
    public void goBack() {
        try {
            String fxmlPath = currentUser.getRole().equals("ADMIN")
                    ? "/view/AdminDashboard.fxml"
                    : "/view/CoordinatorDashboard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            if (currentUser.getRole().equals("ADMIN")) {
                ((AdminDashboardController) loader.getController()).initData(currentUser);
            } else {
                ((CoordinatorDashboardController) loader.getController()).initData(currentUser);
            }
            Stage stage = (Stage) currentPasswordField.getScene().getWindow();
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