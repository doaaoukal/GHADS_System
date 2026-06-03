/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.net.URL;

public class SceneManager {

    /**
     * Globally switches the current stage scene to a new FXML view.
     * Automatically attaches the global CSS stylesheet for design consistency.
     * * @param fxmlPath The relative path to the FXML file (e.g., "/view/LoginView.fxml")
     * @param node     Any active Node on the current scene to fetch the current Stage window
     */
    public static void switchScene(String fxmlPath, Node node) {
        try {
            // Ensure the path starts with a forward slash for proper resource mapping
            if (!fxmlPath.startsWith("/")) {
                fxmlPath = "/" + fxmlPath;
            }

            // Using ContextClassLoader ensures robust asset loading across different environments
            URL fxmlResource = Thread.currentThread().getContextClassLoader().getResource(fxmlPath.substring(1));
            if (fxmlResource == null) {
                // Fallback approach if context loader fails
                fxmlResource = SceneManager.class.getResource(fxmlPath);
            }

            if (fxmlResource == null) {
                throw new java.io.FileNotFoundException("FXML file not found at path: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlResource);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Automatically inject global CSS stylesheet to guarantee unified layout colors and styling
            try {
                URL cssResource = Thread.currentThread().getContextClassLoader().getResource("style/style.css");
                if (cssResource == null) {
                    cssResource = SceneManager.class.getResource("/style/style.css");
                }
                
                if (cssResource != null) {
                    String css = cssResource.toExternalForm();
                    scene.getStylesheets().add(css);
                } else {
                    System.out.println("Warning: 'style.css' not found in resources/style/ folder.");
                }
            } catch (Exception e) {
                System.out.println("Warning: Failed to load global stylesheet application layout.");
            }

            // Fetch the active window stage and seamlessly swap the scene
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            
            // Adjust and center the window frame nicely on screen to avoid clipping
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            System.err.println("Critical Error: Unable to swap scene to -> " + fxmlPath);
            e.printStackTrace();
        }
    }
}