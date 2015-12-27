package com.nestlabs.demo.client.model;

/**
 * Represents a property of device.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public class Property {

    /**
     * The name of property.
     */
    private final String name;

    /**
     * The value of property.
     */
    private final String value;

    /**
     * The unit of measure for property.
     */
    private final String unit;

    public Property(String name, String value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

}
