package com.nestlabs.demo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.nestlabs.demo.client.service.AuthenticationService;
import com.nestlabs.demo.client.service.Callback;

/**
 * This class is used at application startup in order to check existence of cookie with access token.
 * If access token is not found then redirection to login page is performed.
 * Otherwise the redirection to default place is performed.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 27.12.2015
 */
public class Bootstrap implements Bootstrapper {

    private final AuthenticationService authenticationService;

    private final PlaceManager placeManager;

    private static final String ACCESS_TOKEN_COOKIE = "nest-token";

    private static final String LOGIN_PAGE = "login";

    @Inject
    public Bootstrap(AuthenticationService authenticationService, PlaceManager placeManager) {
        this.authenticationService = authenticationService;
        this.placeManager = placeManager;
    }

    @Override
    public void onBootstrap() {
        String accessToken = Cookies.getCookie(ACCESS_TOKEN_COOKIE);
        if (accessToken != null) {
            authenticationService.authenticate(
                    accessToken,
                    new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            placeManager.revealCurrentPlace();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            GWT.log(t.getMessage());
                            redirectToLoginPage();
                        }
                    }
            );
        } else {
            redirectToLoginPage();
        }
    }

    private void redirectToLoginPage() {
        Window.Location.replace(GWT.getHostPageBaseURL() + LOGIN_PAGE);
    }

}
