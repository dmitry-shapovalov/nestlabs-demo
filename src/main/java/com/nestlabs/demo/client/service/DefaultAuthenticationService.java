package com.nestlabs.demo.client.service;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;

/**
 * The implementation of {@link AuthenticationService} interface.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 27.12.2015
 */
class DefaultAuthenticationService implements AuthenticationService {

    /**
     * The reference to Firebase object.
     */
    @SuppressWarnings("unused")
    private final JavaScriptObject firebaseRef;

    @Inject
    public DefaultAuthenticationService(Firebase firebase) {
        this.firebaseRef = firebase.getReference();
    }

    @Override
    public native void authenticate(String accessToken, Callback<Void> callback) /*-{
        var firebaseRef = this.@com.nestlabs.demo.client.service.DefaultAuthenticationService::firebaseRef;

        firebaseRef.authWithCustomToken(
            accessToken,
            function (error, authData) {
                if (error) {
                    console.log("Authentication failed", error);
                    callback.@com.nestlabs.demo.client.service.Callback::onFailure(Ljava/lang/Throwable;)(
                        @java.lang.Exception::new(Ljava/lang/String;)(error)
                    );
                } else {
                    console.log("Authenticated succeded with payload:", authData);
                    callback.@com.nestlabs.demo.client.service.Callback::onSuccess(Ljava/lang/Object;)(null);
                }
            }
        );
    }-*/;

}
