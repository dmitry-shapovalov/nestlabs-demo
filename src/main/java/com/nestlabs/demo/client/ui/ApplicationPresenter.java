package com.nestlabs.demo.client.ui;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.proxy.Proxy;

/**
 * The root presenter of application.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public class ApplicationPresenter extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy> {

    @ProxyStandard
    public interface MyProxy extends Proxy<ApplicationPresenter> {
    }

    public interface MyView extends View {
    }

    public static final NestedSlot CONTENT_SLOT = new NestedSlot();

    @Inject
    ApplicationPresenter(EventBus eventBus, MyView view, MyProxy proxy) {
        super(eventBus, view, proxy, Presenter.RevealType.Root);
    }

}

