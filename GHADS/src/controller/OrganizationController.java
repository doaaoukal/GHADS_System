/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.OrganizationDAO;
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
import util.BaseController;

public class OrganizationController extends BaseController {

    @FXML private TableView<Organization> orgTable;
    @FXML private TableColumn<Organization, Integer> colId;
    @FXML private TableColumn<Organization, String> colName;
    @FXML private TableColumn<Organization, String> colType;
    @FXML private TableColumn<Organization, String> colContact;

    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField contactField;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Button resetBtn;
    @FXML private Label statusLabel;

    private OrganizationDAO orgDAO = new OrganizationDAO();
    private ObservableList<Organization> orgList = FXCollections.observableArrayList();
    private User currentUser;
    private Organization selectedOrg;

    public void initData(User user) {
        this.currentUser = user;
        setupTable();
        loadOrganizations();
        setupTableSelection();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("orgId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
    }

    private void loadOrganizations() {
        orgList.setAll(orgDAO.getAllOrganizations());
        orgTable.setItems(orgList);
    }

    private void setupTableSelection() {
        orgTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedOrg = newVal;
                nameField.setText(newVal.getName());
                typeField.setText(newVal.getType());
                contactField.setText(newVal.getContactInfo());
            }
        });
    }

    @FXML
    public void handleAdd() {
        if (!validateInputs()) return;

        // Check duplicate name
        boolean exists = orgDAO.getAllOrganizations().stream()
                .anyMatch(o -> o.getName().equalsIgnoreCase(nameField.getText().trim()));
        if (exists) {
            showStatus("Organization name already exists!", false);
            return;
        }

        Organization org = new Organization(
                nameField.getText().trim(),
                typeField.getText().trim(),
                contactField.getText().trim()
        );

        if (orgDAO.addOrganization(org)) {
            showStatus("Organization added successfully!", true);
            handleReset();
            loadOrganizations();
        } else {
            showStatus("Failed to add organization!", false);
        }
    }

    @FXML
    public void handleUpdate() {
        if (selectedOrg == null) {
            showStatus("Please select an organization to update.", false);
            return;
        }
        if (!validateInputs()) return;

        selectedOrg.setName(nameField.getText().trim());
        selectedOrg.setType(typeField.getText().trim());
        selectedOrg.setContactInfo(contactField.getText().trim());

        if (orgDAO.updateOrganization(selectedOrg)) {
            showStatus("Organization updated successfully!", true);
            handleReset();
            loadOrganizations();
        } else {
            showStatus("Failed to update organization!", false);
        }
    }

    @FXML
    public void handleDelete() {
        if (selectedOrg == null) {
            showStatus("Please select an organization to delete.", false);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete \"" + selectedOrg.getName() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                if (orgDAO.deleteOrganization(selectedOrg.getOrgId())) {
                    showStatus("Organization deleted successfully!", true);
                    handleReset();
                    loadOrganizations();
                } else {
                    showStatus("Failed to delete organization!", false);
                }
            }
        });
    }

    @FXML
    public void handleReset() {
        nameField.clear();
        typeField.clear();
        contactField.clear();
        selectedOrg = null;
        orgTable.getSelectionModel().clearSelection();
        statusLabel.setText("");
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboard.fxml"));
            Parent root = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty() ||
            typeField.getText().trim().isEmpty() ||
            contactField.getText().trim().isEmpty()) {
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