package com.nestlabs.demo.client.service;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents a wrapper for root JavaScript Firebase object.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 27.12.2015
 */
class Firebase {

    private final JavaScriptObject reference;

    public Firebase() {
        this.reference = init();
    }

    public JavaScriptObject getReference() {
        return reference;
    }

    private native JavaScriptObject init() /*-{
        return new $wnd.Firebase('wss://developer-api.nest.com');
    }-*/;

}
