package com.nestlabs.demo.client.model;

/**
 * Represents an interface of device.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public interface Device {

    /**
     * Returns an identifier of device.
     *
     * @return The identifier of device.
     */
    String getId();

    /**
     * Returns a type of device.
     *
     * @return The type of device.
     */
    DeviceType getType();

    /**
     * Returns an array of device properties.
     *
     * @return The array of device properties.
     */
    Property[] getProperties();

}
