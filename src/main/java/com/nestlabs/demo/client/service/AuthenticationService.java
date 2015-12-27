package com.nestlabs.demo.client.service;

/**
 * This is an interface of the service that provides methods for authentication of users.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 27.12.2015
 */
public interface AuthenticationService {

    /**
     * Authenticates user by means of access token.
     *
     * @param accessToken The access token.
     * @param callback The callback of operation.
     */
    void authenticate(String accessToken, Callback<Void> callback);

}
