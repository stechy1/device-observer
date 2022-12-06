package com.example.device_observer.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import com.example.device_observer.model.MainModel;
import com.example.device_observer.model.TableModel;

import java.net.URL;
import java.util.ResourceBundle;

public class TableController implements Initializable {
  
  // region Variables
  private MainModel mainModel;

  // region FXML

  @FXML
  private TableView<TableModel> table;

  @FXML
  private TableColumn<TableModel, String> columnName;
  @FXML
  private TableColumn<TableModel, Number> columnTotalMessages;
  @FXML
  private TableColumn<TableModel, Boolean> columnToggleView;

  // endregion
  
  // endregion

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // just table data binding
    columnName.setCellValueFactory(param -> param.getValue().nameProperty());
    columnTotalMessages.setCellValueFactory(param -> param.getValue().totalMessagesProperty());
    columnToggleView.setCellValueFactory(param -> param.getValue().visibleProperty());
    
    columnName.setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()));
    columnToggleView.setCellFactory(param -> new CheckBoxTableCell<>());
  }

  public void setMainModel(MainModel mainModel) {
    this.mainModel = mainModel;
    // wire up table items with model data
    table.setItems(mainModel.getTableModelData());
  }

  // region Button handlers
  
  public void handleAddDevice(ActionEvent actionEvent) {
    mainModel.addDevice();
  }

  public void handleRemoveDevice(ActionEvent actionEvent) {
    mainModel.removeDevice(table.getSelectionModel().getSelectedItem().getName());
  }
  
  // endregion
}
