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

public class LoginController extends BaseController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        User user = userDAO.getUserByCredentials(username, password);

        if (user == null) {
            showError("Invalid username or password.");
            return;
        }

        // Navigate based on role
        try {
            if (user.getRole().equals("ADMIN")) {
                loadDashboard("/view/AdminDashboard.fxml", user, "Admin Dashboard");
            } else if (user.getRole().equals("COORDINATOR")) {
                loadDashboard("/view/CoordinatorDashboard.fxml", user, "Coordinator Dashboard");
            }
        } catch (Exception e) {
            showError("Error loading dashboard: " + e.getMessage());
        }
    }

    private void loadDashboard(String fxmlPath, User user, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Pass user to next controller
        if (user.getRole().equals("ADMIN")) {
            AdminDashboardController controller = loader.getController();
            controller.initData(user);
        } else {
            CoordinatorDashboardController controller = loader.getController();
            controller.initData(user);
        }

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }
}