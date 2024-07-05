/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.uitest.ui;

import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.uitest.servlet.ViewTestLayout;

@JavaScript("frontend://in-memory-connector.js")
@Route(value = "com.vaadin.flow.uitest.ui.InMemoryChildrenView", layout = ViewTestLayout.class)
public class InMemoryChildrenView extends AbstractDivView {

    @Override
    protected void onShow() {
        Label label = new Label();
        label.setId("in-memory");
        label.setText("In memory element");
        getElement().appendVirtualChild(label.getElement());
        getElement().executeJs("window.inMemoryConnector.init(this, $0)",
                label);
        Div target = new Div();
        target.setId("target");
        add(target);
        add(createButton("Add copy of in-memory element to the target", "copy",
                event -> getElement().callJsFunction("useInMemoryElement",
                        target)));
    }
}
