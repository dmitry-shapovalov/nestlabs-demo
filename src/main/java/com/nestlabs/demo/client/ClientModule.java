package com.nestlabs.demo.client;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.nestlabs.demo.client.service.ServiceModule;
import com.nestlabs.demo.client.ui.ApplicationModule;
import com.nestlabs.demo.client.ui.NameTokens;

/**
 * The main GIN module of client part of application.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 27.12.2015
 */
public class ClientModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        install(
                new DefaultModule.Builder()
                        .defaultPlace(NameTokens.THERMOSTATS)
                        .errorPlace(NameTokens.ERROR)
                        .unauthorizedPlace(NameTokens.ERROR)
                        .build()
        );

        install(new ServiceModule());
        install(new ApplicationModule());
    }

}

