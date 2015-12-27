package com.nestlabs.demo.client.ui;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.nestlabs.demo.client.ui.device.DeviceModule;

/**
 * The main GIN module of UI part of application.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public class ApplicationModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        bindPresenter(
                ApplicationPresenter.class,
                ApplicationPresenter.MyView.class,
                ApplicationView.class,
                ApplicationPresenter.MyProxy.class
        );

        install(new DeviceModule());
    }

}

