package com.example.device_observer.model;

/**
 * Simple record represents generated data from device
 * 
 * @param index Index pointing into a list of all available devices
 * @param data Just random data value - can represents temperature for example
 */
public record DeviceData(int index, int data) {}
