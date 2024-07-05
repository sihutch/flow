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
 * Component representing a <code>&lt;li&gt;</code> element.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
@Tag(Tag.LI)
public class ListItem extends HtmlContainer implements ClickNotifier<ListItem> {

    /**
     * Creates a new empty list item.
     */
    public ListItem() {
        super();
    }

    /**
     * Creates a new list item with the given child components.
     *
     * @param components
     *            the child components
     */
    public ListItem(Component... components) {
        super(components);
    }

    /**
     * Creates a new list item with the given text.
     *
     * @param text
     *            the text
     */
    public ListItem(String text) {
        super();
        setText(text);
    }
}
