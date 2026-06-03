package controller;

import dao.FamilyDAO;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import util.BaseController;

public class CoordinatorDashboardController extends BaseController {

    @FXML private Label totalFamiliesLabel;
    @FXML private Label familiesServedLabel;
    @FXML private Label familiesNotServedLabel;
    @FXML private Label welcomeLabel;

    private User currentUser;
    private FamilyDAO familyDAO = new FamilyDAO();

    public void initData(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + " 👋");
        loadStats();
    }

    private void loadStats() {
        int total     = familyDAO.getTotalFamilies();
        int served    = familyDAO.getTotalFamiliesServedByOrg(currentUser.getOrgId());
        // "Not yet served" = أسر ما خدمتها أي منظمة أبداً (مش بس منظمتنا)
        int notServed = total - familyDAO.getTotalFamiliesServed();

        totalFamiliesLabel.setText(String.valueOf(total));
        familiesServedLabel.setText(String.valueOf(served));
        familiesNotServedLabel.setText(String.valueOf(notServed));
    }

    @FXML
    public void goToDashboard() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CoordinatorDashboard.fxml"));
        Parent root = loader.load();
        CoordinatorDashboardController controller = loader.getController();
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
    public void goToFamilies() { loadView("/view/FamilyView.fxml"); }

    @FXML
    public void goToAidDistribution() { loadView("/view/AidDistributionView.fxml"); }

    @FXML
    public void goToProfile() { loadView("/view/ProfileView.fxml"); }

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

            Object controller = loader.getController();
            if (controller instanceof CoordinatorDashboardController c) c.initData(currentUser);
            else if (controller instanceof FamilyController c) c.initData(currentUser);
            else if (controller instanceof AidDistributionController c) c.initData(currentUser);
            else if (controller instanceof ProfileController c) c.initData(currentUser);
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
