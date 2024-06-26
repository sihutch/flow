/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;

public class FrontendVaadinServletTest {

    @Test
    public void doNotServeNonStaticResources()
            throws ServletException, IOException {
        FrontendVaadinServlet servlet = new FrontendVaadinServlet() {
            @Override
            protected boolean serveStaticOrWebJarRequest(
                    HttpServletRequest request, HttpServletResponse response)
                    throws IOException {
                return false;
            }
        };
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        servlet.service(request, response);

        Mockito.verify(response).sendError(
                Mockito.eq(HttpServletResponse.SC_NOT_FOUND),
                Mockito.anyString());
    }

    @Test
    public void serveNonStaticResources() throws ServletException, IOException {
        FrontendVaadinServlet servlet = new FrontendVaadinServlet() {
            @Override
            protected boolean serveStaticOrWebJarRequest(
                    HttpServletRequest request, HttpServletResponse response)
                    throws IOException {
                return true;
            }
        };
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        servlet.service(request, response);

        Mockito.verifyZeroInteractions(response);
        Mockito.verifyZeroInteractions(request);
    }

}
