package com.nestlabs.demo.client.ui;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * The root view of application.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView {

    interface Binder extends UiBinder<Widget, ApplicationView> {
    }

    @UiField
    HTMLPanel contentPanel;

    @Inject
    ApplicationView(Binder binder) {
        initWidget(binder.createAndBindUi(this));

        bindSlot(ApplicationPresenter.CONTENT_SLOT, contentPanel);
    }

}

