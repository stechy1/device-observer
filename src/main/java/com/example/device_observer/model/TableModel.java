package com.example.device_observer.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableModel {
  
  private final StringProperty name = new SimpleStringProperty(this, "name");
  private final IntegerProperty totalMessages = new SimpleIntegerProperty(this, "totalMessages");
  private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible", true);

  public TableModel(DeviceInfo deviceInfo) {
    name.bindBidirectional(deviceInfo.nameProperty());
    totalMessages.bind(deviceInfo.totalMessagesProperty());
  }

  public String getName() {
    return name.get();
  }

  public StringProperty nameProperty() {
    return name;
  }

  public int getTotalMessages() {
    return totalMessages.get();
  }

  public IntegerProperty totalMessagesProperty() {
    return totalMessages;
  }

  public boolean isVisible() {
    return visible.get();
  }

  public BooleanProperty visibleProperty() {
    return visible;
  }

}
