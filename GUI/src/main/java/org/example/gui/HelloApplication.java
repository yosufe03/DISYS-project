package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Locale.setDefault(Locale.GERMANY);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Energy Community");
        stage.setScene(scene);
        stage.show();
    }
}
