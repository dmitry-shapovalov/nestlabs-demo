package com.nestlabs.demo.client.ui.device;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.nestlabs.demo.client.model.DeviceType;
import com.nestlabs.demo.client.service.DeviceService;
import com.nestlabs.demo.client.ui.NameTokens;

/**
 * This is a presenter for the page that displays a list of smoke alarms.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public class SmokeAlarmListPresenter extends DeviceListPresenter<SmokeAlarmListPresenter.MyProxy> {

    @ProxyStandard
    @NameToken(NameTokens.SMOKE_CO_ALARMS)
    public interface MyProxy extends ProxyPlace<SmokeAlarmListPresenter> {
    }

    @Inject
    public SmokeAlarmListPresenter(DeviceService deviceService, EventBus eventBus, MyView view, MyProxy proxy) {
        super(DeviceType.SMOKE_CO_ALARM, deviceService, eventBus, view, proxy);

        view.setCaption("Smoke + CO Alarms");
    }
    
}
