/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.server;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.internal.CurrentInstance;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouteNotFoundError;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.BootstrapHandler.BootstrapContext;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;

public class BootstrapContextTest {

    private MockVaadinSession session;
    private UI ui;
    private VaadinRequest request = Mockito.mock(VaadinRequest.class);

    private Function<VaadinRequest, String> callback = request -> "";;

    @Tag(Tag.A)
    @Push(value = PushMode.MANUAL, transport = Transport.LONG_POLLING)
    private static class MainView extends Component implements RouterLayout {

    }

    @Tag(Tag.A)
    private static class OtherView extends Component {

    }

    @Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
    private static class CustomRouteNotFound extends RouteNotFoundError {

    }

    @ParentLayout(MainView.class)
    private static class AnotherCustomRouteNotFound extends RouteNotFoundError {

    }

    @Before
    public void setUp() throws ServiceException {
        MockVaadinSession session = new MockVaadinSession();
        session.lock();
        ui = new UI();
        ui.getInternals().setSession(session);

        session.getService().init();
        VaadinSession.setCurrent(session);
    }

    @After
    public void tearDown() {
        ui.getSession().unlock();
        CurrentInstance.clearAll();
    }

    @Test
    public void getPushAnnotation_routeTargetPresents_pushFromTheClassDefinitionIsUsed() {
        ui.getInternals().getRouter().getRegistry().setRoute("foo",
                MainView.class, Collections.emptyList());
        Mockito.when(request.getPathInfo()).thenReturn("/foo");

        BootstrapContext context = new BootstrapContext(request,
                Mockito.mock(VaadinResponse.class), session, ui, callback);

        Optional<Push> push = context
                .getPageConfigurationAnnotation(Push.class);
        Assert.assertTrue(push.isPresent());
        Push pushAnnotation = push.get();
        Assert.assertEquals(PushMode.MANUAL, pushAnnotation.value());
        Assert.assertEquals(Transport.LONG_POLLING, pushAnnotation.transport());
    }

    @Test
    public void getPushAnnotation_routeTargetPresents_pushDefinedOnParentLayout_pushFromTheClassDefinitionIsUsed() {
        ui.getInternals().getRouter().getRegistry().setRoute("foo",
                OtherView.class, Collections.singletonList(MainView.class));
        Mockito.when(request.getPathInfo()).thenReturn("/foo");

        BootstrapContext context = new BootstrapContext(request,
                Mockito.mock(VaadinResponse.class), session, ui, callback);

        Optional<Push> push = context
                .getPageConfigurationAnnotation(Push.class);
        Assert.assertTrue(push.isPresent());
        Push pushAnnotation = push.get();
        Assert.assertEquals(PushMode.MANUAL, pushAnnotation.value());
        Assert.assertEquals(Transport.LONG_POLLING, pushAnnotation.transport());
    }

    @Test
    public void getPushAnnotation_routeTargetIsAbsent_pushFromTheErrorNavigationTargetIsUsed() {
        Mockito.when(request.getPathInfo()).thenReturn("/bar");

        ApplicationRouteRegistry registry = ApplicationRouteRegistry
                .getInstance(ui.getSession().getService().getContext());
        registry.setErrorNavigationTargets(
                Collections.singleton(CustomRouteNotFound.class));

        BootstrapContext context = new BootstrapContext(request,
                Mockito.mock(VaadinResponse.class), session, ui, request -> "");

        Optional<Push> push = context
                .getPageConfigurationAnnotation(Push.class);
        Assert.assertTrue(push.isPresent());
        Push pushAnnotation = push.get();
        Assert.assertEquals(PushMode.AUTOMATIC, pushAnnotation.value());
        Assert.assertEquals(Transport.WEBSOCKET, pushAnnotation.transport());
    }

    @Test
    public void getPushAnnotation_routeTargetIsAbsent_pushIsDefinedOnParentLayout_pushFromTheErrorNavigationTargetParentLayoutIsUsed() {
        Mockito.when(request.getPathInfo()).thenReturn("/bar");
        ApplicationRouteRegistry registry = ApplicationRouteRegistry
                .getInstance(ui.getSession().getService().getContext());
        registry.setErrorNavigationTargets(
                Collections.singleton(AnotherCustomRouteNotFound.class));

        BootstrapContext context = new BootstrapContext(request,
                Mockito.mock(VaadinResponse.class), session, ui, request -> "");

        Optional<Push> push = context
                .getPageConfigurationAnnotation(Push.class);
        Assert.assertTrue(push.isPresent());
        Push pushAnnotation = push.get();
        Assert.assertEquals(PushMode.MANUAL, pushAnnotation.value());
        Assert.assertEquals(Transport.LONG_POLLING, pushAnnotation.transport());
    }
}
