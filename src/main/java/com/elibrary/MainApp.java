package com.elibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for E-Library System
 * Entry point of the JavaFX application
 */
public class MainApp extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            
            // Load the login selection screen
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginSelection.fxml"));
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("E-Library Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Get the primary stage
     * @return Primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Change the current scene
     * @param fxmlFile FXML file path
     * @param title Window title
     */
    public static void changeScene(String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource(fxmlFile));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(MainApp.class.getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error changing scene: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
