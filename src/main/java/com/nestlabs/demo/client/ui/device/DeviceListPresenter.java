package com.nestlabs.demo.client.ui.device;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.nestlabs.demo.client.model.Device;
import com.nestlabs.demo.client.model.DeviceType;
import com.nestlabs.demo.client.service.Callback;
import com.nestlabs.demo.client.service.DeviceChangeListener;
import com.nestlabs.demo.client.service.DeviceService;
import com.nestlabs.demo.client.ui.ApplicationPresenter;

import java.util.List;

/**
 * This is a presenter for the view that displays a list of devices.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public abstract class DeviceListPresenter<P extends ProxyPlace<?>> extends Presenter<DeviceListPresenter.MyView, P> {

    public interface MyView extends View {

        void setCaption(String caption);

        void display(List<Device> devices);

        void update(Device device);

    }

    /**
     * The type of devices shown in the page.
     */
    private final DeviceType deviceType;

    /**
     * The reference to Device Service.
     */
    private final DeviceService deviceService;

    public DeviceListPresenter(
            DeviceType deviceType,
            DeviceService deviceService,
            EventBus eventBus,
            MyView view,
            P proxy
    ) {
        super(eventBus, view, proxy, ApplicationPresenter.CONTENT_SLOT);

        this.deviceType = deviceType;
        this.deviceService = deviceService;
    }

    @Override
    protected void onBind() {
        // Get list of devices.
        deviceService.get(
                deviceType,
                new Callback<List<Device>>() {
                    @Override
                    public void onSuccess(List<Device> result) {
                        getView().display(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        GWT.log(t.getMessage());
                    }
                }
        );

        // Subscribe for changes.
        deviceService.addChangeListener(
                deviceType,
                new DeviceChangeListener() {
                    @Override
                    public void onChange(Device device) {
                        getView().update(device);
                    }
                }
        );
    }
}
