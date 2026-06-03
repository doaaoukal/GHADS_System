/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.FamilyDAO;
import model.Family;
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
import util.BaseController;

public class FamilyController  extends BaseController {

    @FXML private TableView<Family> familyTable;
    @FXML private TableColumn<Family, Integer> colId;
    @FXML private TableColumn<Family, String> colName;
    @FXML private TableColumn<Family, String> colPhone;
    @FXML private TableColumn<Family, String> colLocation;
    @FXML private TableColumn<Family, Integer> colSize;
    @FXML private TableColumn<Family, String> colNationalId;
    @FXML private TableColumn<Family, String> colVulnerability;
    @FXML private TableColumn<Family, LocalDate> colLastAid;

    @FXML private TextField householdNameField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextField familySizeField;
    @FXML private TextField nationalIdField;
    @FXML private ComboBox<String> vulnerabilityCombo;
    @FXML private DatePicker registrationDatePicker;
    @FXML private Label statusLabel;

    private FamilyDAO familyDAO = new FamilyDAO();
    private ObservableList<Family> familyList = FXCollections.observableArrayList();
    private User currentUser;
    private Family selectedFamily;

    public void initData(User user) {
        this.currentUser = user;
        setupTable();
        setupCombos();
        loadFamilies();
        setupTableSelection();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("familyId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("familySize"));
        colNationalId.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        colVulnerability.setCellValueFactory(new PropertyValueFactory<>("vulnerabilityLevel"));
        colLastAid.setCellValueFactory(new PropertyValueFactory<>("lastAidDate"));
    }

    private void setupCombos() {
        vulnerabilityCombo.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));
    }

    private void loadFamilies() {
        familyList.setAll(familyDAO.getAllFamilies());
        familyTable.setItems(familyList);
    }

    private void setupTableSelection() {
        familyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedFamily = newVal;
                householdNameField.setText(newVal.getHouseholdName());
                phoneField.setText(newVal.getPhone());
                locationField.setText(newVal.getLocation());
                familySizeField.setText(String.valueOf(newVal.getFamilySize()));
                nationalIdField.setText(newVal.getNationalId());
                vulnerabilityCombo.setValue(newVal.getVulnerabilityLevel());
                registrationDatePicker.setValue(newVal.getRegistrationDate());
            }
        });
    }

    @FXML
    public void handleAdd() {
        if (!validateInputs()) return;

        // Check national ID duplicate
        if (familyDAO.getFamilyByNationalId(nationalIdField.getText().trim()) != null) {
            showStatus("Family with this National ID already exists!", false);
            return;
        }

        Family family = new Family(0,
                householdNameField.getText().trim(),
                phoneField.getText().trim(),
                locationField.getText().trim(),
                Integer.parseInt(familySizeField.getText().trim()),
                nationalIdField.getText().trim(),
                vulnerabilityCombo.getValue(),
                registrationDatePicker.getValue(),
                null
        );

        if (familyDAO.addFamily(family)) {
            showStatus("Family registered successfully!", true);
            handleReset();
            loadFamilies();
        } else {
            showStatus("Failed to register family!", false);
        }
    }

    @FXML
    public void handleUpdate() {
        if (selectedFamily == null) {
            showStatus("Please select a family to update.", false);
            return;
        }
        if (!validateInputs()) return;

        selectedFamily.setHouseholdName(householdNameField.getText().trim());
        selectedFamily.setPhone(phoneField.getText().trim());
        selectedFamily.setLocation(locationField.getText().trim());
        selectedFamily.setFamilySize(Integer.parseInt(familySizeField.getText().trim()));
        selectedFamily.setNationalId(nationalIdField.getText().trim());
        selectedFamily.setVulnerabilityLevel(vulnerabilityCombo.getValue());

        if (familyDAO.updateFamily(selectedFamily)) {
            showStatus("Family updated successfully!", true);
            handleReset();
            loadFamilies();
        } else {
            showStatus("Failed to update family!", false);
        }
    }

    @FXML
    public void handleDelete() {
        if (selectedFamily == null) {
            showStatus("Please select a family to delete.", false);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete \"" + selectedFamily.getHouseholdName() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                if (familyDAO.deleteFamily(selectedFamily.getFamilyId())) {
                    showStatus("Family deleted successfully!", true);
                    handleReset();
                    loadFamilies();
                } else {
                    showStatus("Failed to delete family!", false);
                }
            }
        });
    }

    @FXML
    public void handleReset() {
        householdNameField.clear();
        phoneField.clear();
        locationField.clear();
        familySizeField.clear();
        nationalIdField.clear();
        vulnerabilityCombo.setValue(null);
        registrationDatePicker.setValue(null);
        selectedFamily = null;
        familyTable.getSelectionModel().clearSelection();
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
            Stage stage = (Stage) householdNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (householdNameField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty() ||
            locationField.getText().trim().isEmpty() ||
            familySizeField.getText().trim().isEmpty() ||
            nationalIdField.getText().trim().isEmpty() ||
            vulnerabilityCombo.getValue() == null ||
            registrationDatePicker.getValue() == null) {
            showStatus("All fields are required!", false);
            return false;
        }
        try {
            int size = Integer.parseInt(familySizeField.getText().trim());
            if (size <= 0) {
                showStatus("Family size must be a positive number!", false);
                return false;
            }
        } catch (NumberFormatException e) {
            showStatus("Family size must be a number!", false);
            return false;
        }
        return true;
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
    }
}
