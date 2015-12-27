package com.nestlabs.demo.client.ui.device;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * The GIN module for pages of devices.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public class DeviceModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        bind(DeviceListPresenter.MyView.class).to(DeviceListView.class);

        bind(ThermostatListPresenter.class).in(Singleton.class);
        bind(ThermostatListPresenter.MyProxy.class).asEagerSingleton();

        bind(SmokeAlarmListPresenter.class).in(Singleton.class);
        bind(SmokeAlarmListPresenter.MyProxy.class).asEagerSingleton();

        bind(CameraListPresenter.class).in(Singleton.class);
        bind(CameraListPresenter.MyProxy.class).asEagerSingleton();
    }

}

