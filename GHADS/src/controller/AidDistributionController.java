/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.AidDistributionDAO;
import dao.AidDistributionDAO.DuplicateCheckResult;
import dao.FamilyDAO;
import dao.OrganizationDAO;
import model.AidDistribution;
import model.Family;
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
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;
import util.BaseController;

public class AidDistributionController extends BaseController {

    @FXML private TableView<AidDistribution> distTable;
    @FXML private TableColumn<AidDistribution, Integer> colId;
    @FXML private TableColumn<AidDistribution, String> colFamily;
    @FXML private TableColumn<AidDistribution, String> colOrg;
    @FXML private TableColumn<AidDistribution, String> colCoordinator;
    @FXML private TableColumn<AidDistribution, String> colAidType;
    @FXML private TableColumn<AidDistribution, LocalDate> colDate;

    @FXML private ComboBox<Family> familyCombo;
    @FXML private ComboBox<String> aidTypeCombo;
    @FXML private DatePicker distributionDatePicker;
    @FXML private ComboBox<Organization> orgFilterCombo; // للـ Admin فقط
    @FXML private Label statusLabel;

    private AidDistributionDAO aidDAO = new AidDistributionDAO();
    private FamilyDAO familyDAO = new FamilyDAO();
    private OrganizationDAO orgDAO = new OrganizationDAO();
    private ObservableList<AidDistribution> distList = FXCollections.observableArrayList();
    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
        setupTable();
        setupCombos();
        loadDistributions();

        if (user.getRole().equals("COORDINATOR")) {
            // إخفاء فلتر المنظمة للـ Coordinator
            if (orgFilterCombo != null) {
                orgFilterCombo.setVisible(false);
                orgFilterCombo.setManaged(false);
            }
        } else {
            // الـ Admin يشوف بس، ما يسجل توزيعات
            familyCombo.setDisable(true);
            aidTypeCombo.setDisable(true);
            distributionDatePicker.setDisable(true);
        }
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("distributionId"));
        colFamily.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        colOrg.setCellValueFactory(new PropertyValueFactory<>("orgName"));
        colCoordinator.setCellValueFactory(new PropertyValueFactory<>("coordinatorName"));
        colAidType.setCellValueFactory(new PropertyValueFactory<>("aidType"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("distributionDate"));
    }

    private void setupCombos() {
        // Aid Types (Bonus)
        aidTypeCombo.setItems(FXCollections.observableArrayList(
                "Food", "Medicine", "Clothing", "Shelter", "Water", "Hygiene Kit", "Cash"
        ));

        // Families مرتبة حسب الـ vulnerability
        List<Family> families = familyDAO.getFamiliesByVulnerability();
        familyCombo.setItems(FXCollections.observableArrayList(families));

        // Org filter للـ Admin
        if (orgFilterCombo != null) {
            List<Organization> orgs = orgDAO.getAllOrganizations();
            orgFilterCombo.setItems(FXCollections.observableArrayList(orgs));
        }
    }

    private void loadDistributions() {
        if (currentUser.getRole().equals("ADMIN")) {
            distList.setAll(aidDAO.getAllDistributions());
        } else {
            distList.setAll(aidDAO.getDistributionsByOrg(currentUser.getOrgId()));
        }
        distTable.setItems(distList);
    }

    @FXML
    public void handleAddDistribution() {
        if (!validateInputs()) return;

        Family selectedFamily = familyCombo.getValue();
        String aidType = aidTypeCombo.getValue();

        // ── Duplicate Check ──────────────────────────────
        DuplicateCheckResult result = aidDAO.checkDuplicate(selectedFamily.getFamilyId(), aidType);

        if (result.isDuplicate()) {
            AidDistribution existing = result.getExistingRecord();
            // عرض Alert واضح بكل التفاصيل
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Duplicate Aid Detected ⚠️");
            alert.setHeaderText("This family already received this aid recently!");
            alert.setContentText(
                "Family: " + selectedFamily.getHouseholdName() + "\n" +
                "Vulnerability Level: " + selectedFamily.getVulnerabilityLevel() + "\n" +
                "Aid Type: " + existing.getAidType() + "\n" +
                "Given by: " + existing.getOrgName() + "\n" +
                "Date: " + existing.getDistributionDate()
            );
            alert.showAndWait();
            return;
        }

        // ── Save Distribution ─────────────────────────────
        AidDistribution aid = new AidDistribution(0,
                selectedFamily.getFamilyId(),
                currentUser.getOrgId(),
                currentUser.getUserId(),
                distributionDatePicker.getValue(),
                aidType
        );

        if (aidDAO.addDistribution(aid)) {
            showStatus("Aid distribution recorded successfully! ✅", true);
            handleReset();
            loadDistributions();
        } else {
            showStatus("Failed to record distribution!", false);
        }
    }

    // فلترة حسب المنظمة (للـ Admin)
    @FXML
    public void handleFilterByOrg() {
        if (orgFilterCombo == null || orgFilterCombo.getValue() == null) {
            loadDistributions();
            return;
        }
        distList.setAll(aidDAO.getDistributionsByOrg(orgFilterCombo.getValue().getOrgId()));
        distTable.setItems(distList);
    }

    // عرض الأسر الأكثر هشاشة (HIGH أولاً)
    @FXML
    public void showMostVulnerable() {
        List<Family> vulnerable = familyDAO.getFamiliesByVulnerability();
        familyCombo.setItems(FXCollections.observableArrayList(vulnerable));
        showStatus("Showing families by vulnerability (HIGH first)", true);
    }

    // عرض الأسر اللي ما خدمت
    @FXML
    public void showNotServed() {
        List<Family> notServed = familyDAO.getFamiliesNotServed();
        familyCombo.setItems(FXCollections.observableArrayList(notServed));
        showStatus("Showing families not yet served", true);
    }

    @FXML
    public void handleReset() {
        familyCombo.setValue(null);
        aidTypeCombo.setValue(null);
        distributionDatePicker.setValue(null);
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
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (familyCombo.getValue() == null ||
            aidTypeCombo.getValue() == null ||
            distributionDatePicker.getValue() == null) {
            showStatus("All fields are required!", false);
            return false;
        }
        return true;
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
    }
}
