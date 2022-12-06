package com.example.device_observer.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.util.Duration;

import com.example.device_observer.model.DeviceData;
import com.example.device_observer.model.MainModel;
import com.example.device_observer.service.DeviceDataConsumer;
import com.example.device_observer.service.DeviceDataProducer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Controller of main.fxml view.
 */
public class MainController implements Initializable, DeviceDataConsumer {

  // region Constants

  /**
   * Number of producer thread used for generating data
   */
  private static final int PRODUCER_COUNT = 4;
  
  /**
   * Default count of devices initialized on application start up 
   */
  private static final int TOTAL_DEVICES = 10;

  /**
   * Defines how often will be graph updated to show new data 
   */
  private static final int UPDATE_GUI_INTERVAL_MS = 200;

  /**
   * Executor service containing worker thread for producers
   */
  private final ExecutorService executor = Executors.newFixedThreadPool(PRODUCER_COUNT);
  
  // endregion

  // region Variables

  private final MainModel model = new MainModel();
  
  private final Timeline simulation = new Timeline();
  private final List<DeviceDataProducer> producers = new ArrayList<>();

  private final BooleanProperty simulationRunning = new SimpleBooleanProperty(this, "simulationRunning", false);
  
  // region FXML

  @FXML
  private LineChart<Integer, Integer> chart;

  @FXML
  private Button btnStart;
  @FXML
  private Button btnStop;

  @FXML
  private TableController tableController;
  
  // endregion FXML

  // endregion Variables

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initSimulation();
    initButtons();
    initChart();
    // initialize default count of devices to see something immediately after start up
    IntStream.range(0, TOTAL_DEVICES).forEach(value -> model.addDevice());
    tableController.setMainModel(model);
    model.setDevicesChangeCallback(totalDevices -> producers.forEach(producer -> producer.updateMaxDevices(totalDevices)));
    // create device data producers for each worker thread
    for (int i = 0; i < PRODUCER_COUNT; i++) {
      producers.add(new DeviceDataProducer(this, TOTAL_DEVICES));
    }
  }

  protected void initSimulation() {
    // Run the simulation indefinitely
    simulation.setCycleCount(Timeline.INDEFINITE);
    simulation.getKeyFrames().add(new KeyFrame(Duration.millis(UPDATE_GUI_INTERVAL_MS), this::updateChart));
  }
  
  protected void initButtons() {
    btnStart.disableProperty().bind(simulationRunning);
    btnStop.disableProperty().bind(simulationRunning.not());
  }
  
  protected void initChart() {
    chart.setData(model.getChartSeries());
    chart.setAnimated(false);
  }

  // runs on JavaFX thread
  private void updateChart(ActionEvent event) {
    model.synchronizeChartData();
  }

  /**
   * Runs on worker thread!
   * Only for store data to "temp" collection
   */
  @Override
  public void consumeData(DeviceData data) {
    model.addData(data);
  }

  /**
   * Stop simulation and all producers
   */
  protected void stopProducers() {
    simulationRunning.setValue(false);
    simulation.stop();
    
    producers.forEach(DeviceDataProducer::stop);
  }

  // region Button handlers

  @FXML
  public void handleStart(ActionEvent event) {
    simulationRunning.setValue(true);
    producers.forEach(DeviceDataProducer::restart);
    simulation.play();
    // trigger data generation
    for (int i = 0; i < PRODUCER_COUNT; i++) {
      executor.submit(producers.get(i));
    }
  }

  @FXML
  public void handleStop(ActionEvent event) {
    stopProducers();
  }

  public void stopWork() {
    stopProducers();
    
    // application shutdown hook - stop all the threads in executor service
    executor.shutdown();
  }

  // endregion
  
}
