/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class BaseController {

    // ── المتغيرات الـ static لأنها تأثر على كل الشاشات ──
    private static double currentFontSize = 13.0;
    private static String currentFontFamily = "Segoe UI";
    private static boolean isDarkTheme = false;

    // ── File Menu ──────────────────────────────────────────

    @FXML
    public void handleExit() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to exit GHADS?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Exit");
        confirm.setHeaderText("Confirm Exit");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                Platform.exit();
            }
        });
    }

    // ── Format Menu ────────────────────────────────────────

    @FXML
    public void handleFontSize() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                String.valueOf((int) currentFontSize),
                "11", "12", "13", "14", "15", "16", "18", "20"
        );
        dialog.setTitle("Font Size");
        dialog.setHeaderText("Choose Font Size");
        dialog.setContentText("Size:");
        dialog.showAndWait().ifPresent(size -> {
            currentFontSize = Double.parseDouble(size);
            applyFontToAllScenes();
        });
    }

    @FXML
    public void handleFontFamily() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                currentFontFamily,
                "Segoe UI", "Arial", "Tahoma", "Verdana",
                "Times New Roman", "Calibri", "Georgia"
        );
        dialog.setTitle("Font Family");
        dialog.setHeaderText("Choose Font Style");
        dialog.setContentText("Font:");
        dialog.showAndWait().ifPresent(font -> {
            currentFontFamily = font;
            applyFontToAllScenes();
        });
    }

    @FXML
    public void handleThemeToggle() {
        isDarkTheme = !isDarkTheme;
        applyThemeToAllScenes();
    }

    // ── Help Menu ──────────────────────────────────────────

    @FXML
    public void handleAbout() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About GHADS");
        about.setHeaderText("Gaza Humanitarian Aid Distribution System");
        about.setContentText(
            "Version: 1.0\n\n" +
            "GHADS helps humanitarian organizations in Gaza\n" +
            "coordinate aid distribution for displaced families.\n\n" +
            "It prevents duplicate assistance by maintaining\n" +
            "one shared database across all organizations.\n\n" +
            "────────────────────────\n" +
            "Developer: [Your Name]\n" +
            "Course: Programming III Lab — CSCI 2108\n" +
            "Instructor: Aya N. Alharazin\n" +
            "Islamic University of Gaza — 2026"
        );
        about.showAndWait();
    }

    // ── Helper Methods ─────────────────────────────────────

    private void applyFontToAllScenes() {
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage stage && stage.getScene() != null) {
                stage.getScene().getRoot().setStyle(
                    stage.getScene().getRoot().getStyle() +
                    "; -fx-font-size: " + currentFontSize + "px;" +
                    " -fx-font-family: '" + currentFontFamily + "';"
                );
            }
        }
    }

    private void applyThemeToAllScenes() {
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage stage && stage.getScene() != null) {
                Scene scene = stage.getScene();
                if (isDarkTheme) {
                    scene.getRoot().getStyleClass().remove("light-theme");
                    if (!scene.getRoot().getStyleClass().contains("dark-theme")) {
                        scene.getRoot().getStyleClass().add("dark-theme");
                    }
                } else {
                    scene.getRoot().getStyleClass().remove("dark-theme");
                    if (!scene.getRoot().getStyleClass().contains("light-theme")) {
                        scene.getRoot().getStyleClass().add("light-theme");
                    }
                }
            }
        }
    }

    // Getters للـ subclasses
    public static double getCurrentFontSize() { return currentFontSize; }
    public static String getCurrentFontFamily() { return currentFontFamily; }
    public static boolean isDarkTheme() { return isDarkTheme; }
}