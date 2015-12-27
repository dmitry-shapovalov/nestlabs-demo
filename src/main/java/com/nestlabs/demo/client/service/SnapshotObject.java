package com.nestlabs.demo.client.service;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents a wrapper for Firebase snapshot object.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
class SnapshotObject extends JavaScriptObject {

    protected SnapshotObject() {
    }

    public final native String getProperty(String name) /*-{
        return this.child(name).val();
    }-*/;

}
