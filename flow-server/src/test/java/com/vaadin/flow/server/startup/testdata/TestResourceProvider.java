/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.server.startup.testdata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.vaadin.flow.di.ResourceProvider;
import com.vaadin.flow.server.VaadinContext;

public class TestResourceProvider implements ResourceProvider {

    @Override
    public List<URL> getApplicationResources(VaadinContext context, String path)
            throws IOException {
        return null;
    }

    @Override
    public URL getApplicationResource(VaadinContext context, String path) {
        return null;
    }

    @Override
    public URL getClientResource(String path) {
        return null;
    }

    @Override
    public InputStream getClientResourceAsStream(String path)
            throws IOException {
        return null;
    }

}
