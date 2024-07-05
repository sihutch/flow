/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.client;

import elemental.dom.Element;
import elemental.dom.Node;

/**
 * Utils class, intended to ease working with DOM elements on client side.
 *
 * @author Vaadin Ltd
 */
public class ElementUtil {

    private ElementUtil() {
        // Only static helpers
    }

    /**
     * Checks whether the {@code node} has required {@code tag}.
     *
     * @param node
     *            the node to check
     * @param tag
     *            the required tag name
     * @return {@code true} if the node has required tag name
     */
    public static boolean hasTag(Node node, String tag) {
        return node instanceof Element
                && tag.equalsIgnoreCase(((Element) node).getTagName());
    }

    /**
     * Searches the shadow root of the given context element for the given id or
     * searches the light DOM if the element has no shadow root.
     *
     * @param context
     *            the container element to search through
     * @param id
     *            the identifier of the element to search for
     * @return the element with the given {@code id} if found, otherwise
     *         <code>null</code>
     */
    public static native Element getElementById(Node context, String id)
    /*-{
       if (document.body.$ && document.body.$.hasOwnProperty && document.body.$.hasOwnProperty(id)) {
         // Exported WCs add their id to body.$ and cannot be found using a real id attribute
         return document.body.$[id];
       } else if (context.shadowRoot) {
         return context.shadowRoot.getElementById(id);
       } else if (context.getElementById) {
         return context.getElementById(id);
       } else if (id && id.match("^[a-zA-Z0-9-_]*$")) {
         // No funky characters in id so querySelector can be used directly
         return context.querySelector("#" + id);
       } else {
         // Find all elements with an id attribute and filter out the correct one
         return Array.from(context.querySelectorAll('[id]')).find(function(e) {return e.id == id});
       }
    }-*/;

}
