/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.webcomponent;

import javax.servlet.annotation.WebServlet;

import com.vaadin.flow.server.VaadinServlet;

@WebServlet(urlPatterns = { "/frontend/*", "/VAADIN/*" }, asyncSupported = true)
public class CompatibilityDevServlet extends VaadinServlet {
}
