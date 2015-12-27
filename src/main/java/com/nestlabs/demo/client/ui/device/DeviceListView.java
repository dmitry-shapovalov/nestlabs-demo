package com.nestlabs.demo.client.ui.device;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.nestlabs.demo.client.model.Device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a view for a list of devices.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public class DeviceListView extends ViewImpl implements DeviceListPresenter.MyView {

    interface Binder extends UiBinder<Widget, DeviceListView> {
    }

    @UiField
    Label captionLabel;

    @UiField
    HTMLPanel panel;

    private final Map<String, DeviceWidget> deviceWidgetMap = new HashMap<>();

    @Inject
    DeviceListView(Binder binder) {
        initWidget(binder.createAndBindUi(this));
    }

    @Override
    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    @Override
    public void display(List<Device> devices) {
        deviceWidgetMap.clear();

        for (Device device : devices) {
            DeviceWidget deviceWidget = new DeviceWidget();
            deviceWidget.display(device);
            panel.add(deviceWidget);
            deviceWidgetMap.put(device.getId(), deviceWidget);
        }
    }

    @Override
    public void update(Device device) {
        DeviceWidget deviceWidget = deviceWidgetMap.get(device.getId());
        if (deviceWidget != null) {
            deviceWidget.display(device);
        }
    }

}
