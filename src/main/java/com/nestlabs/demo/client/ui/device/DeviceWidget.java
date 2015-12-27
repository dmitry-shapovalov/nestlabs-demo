package com.nestlabs.demo.client.ui.device;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nestlabs.demo.client.model.Device;
import com.nestlabs.demo.client.model.Property;

/**
 * The widget that displays information about some device.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public class DeviceWidget extends Composite {

    interface Binder extends UiBinder<Widget, DeviceWidget> {
    }

    @UiField
    Image image;

    @UiField
    FlexTable propertyTable;

    private static final Binder BINDER = GWT.create(Binder.class);

    public DeviceWidget() {
        initWidget(BINDER.createAndBindUi(this));
    }

    void display(Device device) {
        image.setUrl("images/" + device.getType().name().toLowerCase() + ".png");

        propertyTable.clear();

        int row = 0;
        for (Property property : device.getProperties()) {
            propertyTable.setText(row, 0, property.getName());
            propertyTable.setText(row, 1, property.getValue() + (property.getUnit() != null ? " " + property.getUnit() : ""));
            row++;
        }
    }

}
