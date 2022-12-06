package com.example.device_observer.service;

import com.example.device_observer.model.DeviceData;

/**
 * Represents device data consumer which is called whenever data from device come
 */
public interface DeviceDataConsumer {

  /**
   * Called whenever new data from some device arrive
   * 
   * @param data Data from device
   */
  void consumeData(DeviceData data);
  
}
