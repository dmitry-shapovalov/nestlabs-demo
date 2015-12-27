package com.nestlabs.demo.client.service;

import com.nestlabs.demo.client.model.Device;

/**
 * This interface is used for receiving notifications related to changing of device properties.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public interface DeviceChangeListener {

    /**
     * This method is invoked when properties of the {@code device} were changed.
     *
     * @param device The device.
     */
    void onChange(Device device);

}
