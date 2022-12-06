package com.example.device_observer.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class contains all the data about one device and properties for visualization
 */
public class DeviceInfo {

  /**
   * Amount of points visible in graph for one device
   */
  public static int DATA_WINDOW_SIZE = 10;

  /**
   * Synchronization window offset for devices which generates data less frequently
   */
  private int windowOffset;
  
  /**
   * Total count of received messages of this device
   */
  private final IntegerProperty totalMessages = new SimpleIntegerProperty(this, "totalMessages", 0);

  /**
   * Chart series of received data
   */
  private final XYChart.Series<Integer, Integer> series = new XYChart.Series<>();
  /**
   * Just a reference to series data
   */
  private final ObservableList<XYChart.Data<Integer, Integer>> data = series.getData();

  /**
   * Data buffer for received data from devices ready to be shown
   */
  private final Queue<Integer> dataBuffer = new ConcurrentLinkedQueue<>();
  
  private final TableModel tableModel;
  
  public DeviceInfo(String name) {
    this(name, 0);
  }
  public DeviceInfo(String name, int windowOffset) {
    series.setName(name);
    this.windowOffset = windowOffset;
    this.tableModel = new TableModel(this);
  }

  
  public XYChart.Series<Integer, Integer> getSeries() {
    return series;
  }
  
  public void addEntry(int value) {
    dataBuffer.add(value);
  }
  
  public int updateData(int windowOffsetDelta) {
    int totalMessagesValue = this.totalMessages.get();
    final int size = data.size();
    final int remainingSize = DATA_WINDOW_SIZE - size;
    // very temporary size
    final int dataSize = dataBuffer.size();
    windowOffset += windowOffsetDelta;
    
    if (dataSize == 0) {
      return totalMessagesValue + windowOffset;
    }

    if (remainingSize > dataSize) {
      for (int i = 0; i < dataSize; i++) {
        data.add(new XYChart.Data<>(windowOffset + totalMessagesValue++, dataBuffer.remove()));
      }
    } else {
      data.add(new XYChart.Data<>(windowOffset + totalMessagesValue++, dataBuffer.remove()));
    }
    
    if (data.size() > DATA_WINDOW_SIZE) {
      data.remove(0, data.size() - DATA_WINDOW_SIZE);
    }
    
    this.totalMessages.setValue(totalMessagesValue);
    return totalMessagesValue + windowOffset;
  }

  public int getTotalMessagesWithOffset() {
    return totalMessages.get() + windowOffset;
  }

  public StringProperty nameProperty() {
    return series.nameProperty();
  }
  
  public IntegerProperty totalMessagesProperty() {
    return totalMessages;
  }

  public TableModel getTableModel() {
    return tableModel;
  }

}
