package com.example.device_observer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.example.device_observer.controller.MainController;

import java.util.Objects;

public class App extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    final FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/main.fxml")));
    final Parent rootNode = loader.load();
    final MainController mainController = loader.getController();
    stage.setScene(new Scene(rootNode));
    stage.setTitle("Device observer application");
    stage.setOnCloseRequest(event -> mainController.stopWork());
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
  
}
