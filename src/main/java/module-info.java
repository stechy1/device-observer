module com.example.device_observer {
  
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  
  exports com.example.device_observer to javafx.graphics;
  opens com.example.device_observer.controller to javafx.fxml;
}