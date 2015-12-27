package com.nestlabs.demo.client.service;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * The GIN module for service layer of application.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 27.12.2015
 */
public class ServiceModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(AuthenticationService.class).to(DefaultAuthenticationService.class).in(Singleton.class);
        bind(DeviceService.class).to(DefaultDeviceService.class).in(Singleton.class);
    }

}
