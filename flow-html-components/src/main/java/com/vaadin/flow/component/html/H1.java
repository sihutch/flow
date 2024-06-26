/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.component.html;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;

/**
 * Component representing a <code>&lt;h1&gt;</code> element.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
@Tag(Tag.H1)
public class H1 extends HtmlContainer implements ClickNotifier<H1> {

    /**
     * Creates a new empty heading.
     */
    public H1() {
        super();
    }

    /**
     * Creates a new heading with the given child components.
     *
     * @param components
     *            the child components
     */
    public H1(Component... components) {
        super(components);
    }

    /**
     * Creates a new heading with the given text.
     *
     * @param text
     *            the text
     */
    public H1(String text) {
        super();
        setText(text);
    }
}
