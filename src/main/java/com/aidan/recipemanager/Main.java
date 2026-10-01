package com.aidan.recipemanager;

import com.aidan.recipemanager.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseInitializer.initialize();

        Label label = new Label("Recipe Manager");

        StackPane root = new StackPane(label);

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Recipe Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
