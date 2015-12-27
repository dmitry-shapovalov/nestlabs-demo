package com.nestlabs.demo.client.service;

import com.nestlabs.demo.client.model.Device;
import com.nestlabs.demo.client.model.DeviceType;

import java.util.List;

/**
 * This interface defines methods that allow to get information about devices and
 * to receive notifications related to changing of values of device properties.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public interface DeviceService {

    /**
     * Allows to get list of device of the specified type.
     *
     * @param deviceType The type of device.
     * @param callback The operation callback.
     */
    void get(DeviceType deviceType, Callback<List<Device>> callback);

    /**
     * Adds new listener for receiving of device change notifications.
     *
     * @param deviceType The type of device.
     * @param listener The change listener.
     */
    void addChangeListener(DeviceType deviceType, DeviceChangeListener listener);

}
