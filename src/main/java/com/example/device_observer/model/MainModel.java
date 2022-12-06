package com.example.device_observer.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Model for main controller
 */
public class MainModel {

  /**
   * Helper static counter of total device count
   */
  private static int deviceCounter = 0;

  /**
   * Hash map of all devices for easy access to device by name/id
   */
  private final Map<String, DeviceInfo> deviceMap = new HashMap<>();
  /**
   * List of all devices - used for easy access while "receiving" data from device
   * Device data contains index pointing into this list
   */
  private final List<DeviceInfo> devices = new CopyOnWriteArrayList<>();
  /**
   * Collection of models for table
   */
  private final ObservableList<TableModel> devicesInTable = FXCollections.observableArrayList();
  /**
   * Collection of visible chart series
   */
  private final ObservableList<XYChart.Series<Integer, Integer>> chartSeries = FXCollections.observableArrayList();

  /**
   * Variable used for synchronization of very fast and very slow devices
   */
  private int synchronizationRounds = 0;
  
  /**
   * Average message count across all available devices
   */
  private int averageMessageCount = 0;
  
  /**
   * Max amount of received message of one device
   */
  private int maxMessageCount = 0;

  /**
   * Callback which is triggered whenever there is added or removed a device
   */
  private Consumer<Integer> devicesChangeCallback;

  public DeviceInfo addDevice() {
    // create new name for device - increment counter and use its value
    final String name = Integer.toString(++deviceCounter);
    // create instance of device with name and index to graph with highest value
    final DeviceInfo deviceInfo = new DeviceInfo(name, Math.max(0, maxMessageCount - 1));
    final TableModel tableModel = deviceInfo.getTableModel();
    
    devices.add(deviceInfo);
    devicesInTable.add(tableModel);
    chartSeries.add(deviceInfo.getSeries());
    
    tableModel.visibleProperty().addListener((observable, oldValue, newValue) -> this.toggleSeriesVisibility(deviceInfo.getSeries(), newValue));
    
    if (devicesChangeCallback != null) {
      devicesChangeCallback.accept(devices.size());
    }
    return deviceMap.put(name, deviceInfo);
  }
  
  public void removeDevice(String name) {
    final DeviceInfo deviceInfo = deviceMap.get(name);
    devicesInTable.remove(deviceInfo.getTableModel());
    chartSeries.remove(deviceInfo.getSeries());
    deviceMap.remove(name);

    if (devicesChangeCallback != null) {
      devicesChangeCallback.accept(devices.size());
    }
  }
  
  public DeviceInfo getDevice(String name) {
    return deviceMap.get(name);
  }

  /**
   * Runs on a worker thread
   * @param data Data
   */
  public void addData(DeviceData data) {
    // get device by index from data
    final DeviceInfo deviceInfo = devices.get(data.index() % devices.size());
    // if the device exists
    if (deviceInfo != null) {
      // add new data value - this does not show the value
      deviceInfo.addEntry(data.data());
    }
  }

  /**
   * Runs on JavaFX thread.
   * Show all received data in some amount of time on chart
   */
  public void synchronizeChartData() {
    // store if index of data will be necessary
    boolean offsetSyncNecessary = synchronizationRounds % 10 == 0;
    int accumulator = 0;
    // iterates through all devices - no synchronization necessary
    // manipulation with devices is also done in JavaFX thread
    for (DeviceInfo deviceInfo : deviceMap.values()) {
      int windowOffsetDelta = 0;
      int totalMessages = deviceInfo.getTotalMessagesWithOffset();
      if (offsetSyncNecessary && totalMessages < averageMessageCount) {
        windowOffsetDelta = (averageMessageCount - totalMessages) / 2;
      }
      // add new data to graph and remove old if necessary
      totalMessages = deviceInfo.updateData(windowOffsetDelta);
      // update max message count
      maxMessageCount = Math.max(maxMessageCount, totalMessages);
      // update accumulator for all message count
      accumulator += totalMessages;
    }
    
    // compute average message count
    averageMessageCount = accumulator / deviceMap.size();
    // increment synchronization rounds
    synchronizationRounds++;
    
    // just to be sure the counter will not overflow
    if (Integer.MAX_VALUE - 1 == synchronizationRounds) {
      synchronizationRounds = 0;
    }
  }
  
  public ObservableList<XYChart.Series<Integer, Integer>> getChartSeries() {
    return chartSeries;
  }

  public ObservableList<TableModel> getTableModelData() {
    return devicesInTable;
  }

  /**
   * Add/remove chart series from chart
   * 
   * @param series Series
   * @param visible Visible
   */
  public void toggleSeriesVisibility(XYChart.Series<Integer, Integer> series, boolean visible) {
    if (visible) {
      chartSeries.add(series);
    } else {
      chartSeries.remove(series);
    }
  }
  
  public void setDevicesChangeCallback(Consumer<Integer> changeCallback) {
    this.devicesChangeCallback = changeCallback;
  }
}
