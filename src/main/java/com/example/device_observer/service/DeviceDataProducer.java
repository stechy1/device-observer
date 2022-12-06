package com.example.device_observer.service;

import com.example.device_observer.model.DeviceData;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Device data producer worker which simulates generating data from devices
 */
public class DeviceDataProducer implements Runnable {

  private static final int WORKER_THREAD_DELAY_MS = 100;
  private static final Random RANDOM = new Random();
  
  private final DeviceDataConsumer consumer;
  private final AtomicInteger upperBound = new AtomicInteger();
  
  protected boolean running = true;

  public DeviceDataProducer(DeviceDataConsumer consumer, int upperBound) {
    this.consumer = consumer;
    this.upperBound.set(upperBound);
  }

  @Override
  public void run() {
    while (running) {
      final int maxValue = upperBound.get();
      // random index of device
      final int index = RANDOM.nextInt(0, maxValue);
      // random value
      final int value = RANDOM.nextInt(0, (maxValue + 1) * 2);
      // notify about new data
      consumer.consumeData(new DeviceData(index, value));

      try {
        Thread.sleep(WORKER_THREAD_DELAY_MS);
      } catch (InterruptedException ignored) {
        // ignored
      }
    }
  }
  
  public void updateMaxDevices(int maxDevices) {
    upperBound.set(maxDevices);
  }
  
  public void stop() {
    running = false;
  }
  
  public void restart() {
    running = true;
  }
}
