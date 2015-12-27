package com.nestlabs.demo.client.service;

/**
 * Represents interface of callback for asynchronous operation.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public interface Callback<T> {

    /**
     * This method is invoked when operation was successfully completed.
     *
     * @param result The result of operation.
     */
    void onSuccess(T result);

    /**
     * This method is invoked when operation was completed with failure.
     *
     * @param t The error.
     */
    void onFailure(Throwable t);

}
