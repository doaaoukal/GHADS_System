/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.*;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import util.BaseController;

public class AdminDashboardController extends BaseController {

    @FXML private Label totalOrgsLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalFamiliesLabel;
    @FXML private Label familiesServedLabel;
    @FXML private Label familiesNotServedLabel;
    @FXML private Label welcomeLabel;
    

    private User currentUser;
    private OrganizationDAO orgDAO = new OrganizationDAO();
    private UserDAO userDAO = new UserDAO();
    private FamilyDAO familyDAO = new FamilyDAO();

    public void initData(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + " 👋");
        loadStats();
    }

    private void loadStats() {
        int totalOrgs     = orgDAO.getTotalOrganizations();
        int totalUsers    = userDAO.getTotalCoordinators();
        int totalFamilies = familyDAO.getTotalFamilies();
        int served        = familyDAO.getTotalFamiliesServed();
        int notServed     = totalFamilies - served;

        totalOrgsLabel.setText(String.valueOf(totalOrgs));
        totalUsersLabel.setText(String.valueOf(totalUsers));
        totalFamiliesLabel.setText(String.valueOf(totalFamilies));
        familiesServedLabel.setText(String.valueOf(served));
        familiesNotServedLabel.setText(String.valueOf(notServed));
    }
    @FXML
public void goToDashboard() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboard.fxml"));
        Parent root = loader.load();
        AdminDashboardController controller = loader.getController();
        controller.initData(currentUser);
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    } catch (Exception e) {
        System.err.println("Dashboard error: " + e.getMessage());
    }
}

    // ── Navigation ──────────────────────────────────────────

    @FXML
    public void goToOrganizations() { loadView("/view/OrganizationView.fxml"); }

    @FXML
    public void goToUsers() { loadView("/view/UserView.fxml"); }

    @FXML
    public void goToFamilies() { loadView("/view/FamilyView.fxml"); }

    @FXML
    public void goToAidDistribution() { loadView("/view/AidDistributionView.fxml"); }

    @FXML
    public void goToChangePassword() { loadView("/view/ChangePasswordView.fxml"); }

    @FXML
    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("GHADS - Login");
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Logout error: " + e.getMessage());
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Pass currentUser to next controller
            Object controller = loader.getController();
            if (controller instanceof AdminDashboardController c) c.initData(currentUser);
            else if (controller instanceof OrganizationController c) c.initData(currentUser);
            else if (controller instanceof UserController c) c.initData(currentUser);
            else if (controller instanceof FamilyController c) c.initData(currentUser);
            else if (controller instanceof AidDistributionController c) c.initData(currentUser);
            else if (controller instanceof ChangePasswordController c) c.initData(currentUser);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }

    public User getCurrentUser() { return currentUser; }
}