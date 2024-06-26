/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.internal;

import java.lang.reflect.InvocationTargetException;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinService;

public class DebugWindowConnectionTest {

    private DebugWindowConnection reload = new DebugWindowConnection();

    @Test
    public void onConnect_suspend_sayHello() {
        AtmosphereResource resource = Mockito.mock(AtmosphereResource.class);
        Broadcaster broadcaster = Mockito.mock(Broadcaster.class);
        Mockito.when(resource.getBroadcaster()).thenReturn(broadcaster);

        reload.onConnect(resource);

        Assert.assertTrue(reload.isLiveReload(resource));
        Mockito.verify(resource).suspend(-1);
        Mockito.verify(broadcaster).broadcast("{\"command\": \"hello\"}",
                resource);
    }

    @Test
    public void reload_twoConnections_sendReloadCommand() {
        AtmosphereResource resource1 = Mockito.mock(AtmosphereResource.class);
        AtmosphereResource resource2 = Mockito.mock(AtmosphereResource.class);
        Broadcaster broadcaster = Mockito.mock(Broadcaster.class);
        Mockito.when(resource1.getBroadcaster()).thenReturn(broadcaster);
        Mockito.when(resource2.getBroadcaster()).thenReturn(broadcaster);
        reload.onConnect(resource1);
        reload.onConnect(resource2);
        Assert.assertTrue(reload.isLiveReload(resource1));
        Assert.assertTrue(reload.isLiveReload(resource2));

        reload.reload();

        Mockito.verify(broadcaster).broadcast("{\"command\": \"reload\"}",
                resource1);
        Mockito.verify(broadcaster).broadcast("{\"command\": \"reload\"}",
                resource2);
    }

    @Test
    public void reload_resourceIsNotSet_reloadCommandIsNotSent() {
        AtmosphereResource resource = Mockito.mock(AtmosphereResource.class);
        Broadcaster broadcaster = Mockito.mock(Broadcaster.class);
        Mockito.when(resource.getBroadcaster()).thenReturn(broadcaster);
        Assert.assertFalse(reload.isLiveReload(resource));

        reload.reload();

        Mockito.verifyZeroInteractions(broadcaster);
    }

    @Test
    public void reload_resourceIsDisconnected_reloadCommandIsNotSent() {
        AtmosphereResource resource = Mockito.mock(AtmosphereResource.class);
        Broadcaster broadcaster = Mockito.mock(Broadcaster.class);
        Mockito.when(resource.getBroadcaster()).thenReturn(broadcaster);
        reload.onConnect(resource);
        Assert.assertTrue(reload.isLiveReload(resource));
        Mockito.reset(broadcaster);
        reload.onDisconnect(resource);
        Assert.assertFalse(reload.isLiveReload(resource));

        reload.reload();

        Mockito.verifyZeroInteractions(broadcaster);
    }

    @Test
    public void getBackend_JRebelClassEventListenerClassLoaded_returnsJREBEL() {
        class JRebelInitializer {
        }
        DebugWindowConnection reload = new DebugWindowConnection(
                new ClassLoader(getClass().getClassLoader()) {
                    @Override
                    protected Class<?> findClass(String name)
                            throws ClassNotFoundException {
                        switch (name) {
                        case "org.zeroturnaround.jrebel.vaadin.JRebelClassEventListener":
                            return JRebelInitializer.class;
                        default:
                            throw new ClassNotFoundException();
                        }
                    }
                });
        Assert.assertEquals(BrowserLiveReload.Backend.JREBEL,
                reload.getBackend());
    }

    @Test
    public void getBackend_HotSwapVaadinIntegrationClassLoaded_returnsHOTSWAP_AGENT() {
        class VaadinIntegration {
        }
        DebugWindowConnection reload = new DebugWindowConnection(
                new ClassLoader(getClass().getClassLoader()) {
                    @Override
                    protected Class<?> findClass(String name)
                            throws ClassNotFoundException {
                        switch (name) {
                        case "org.hotswap.agent.plugin.vaadin.VaadinIntegration":
                            return VaadinIntegration.class;
                        default:
                            throw new ClassNotFoundException();
                        }
                    }
                });
        Assert.assertEquals(BrowserLiveReload.Backend.HOTSWAP_AGENT,
                reload.getBackend());
    }

    @Test
    public void getBackend_SpringBootDevtoolsClassesLoaded_returnsSPRING_BOOT_DEVTOOLS() {
        class SpringServlet {
        }
        class LiveReloadServer {
        }
        DebugWindowConnection reload = new DebugWindowConnection(
                new ClassLoader(getClass().getClassLoader()) {
                    @Override
                    protected Class<?> findClass(String name)
                            throws ClassNotFoundException {
                        switch (name) {
                        case "com.vaadin.flow.spring.SpringServlet":
                            return SpringServlet.class;
                        case "org.springframework.boot.devtools.livereload.LiveReloadServer":
                            return LiveReloadServer.class;
                        default:
                            throw new ClassNotFoundException();
                        }
                    }
                });
        Assert.assertEquals(BrowserLiveReload.Backend.SPRING_BOOT_DEVTOOLS,
                reload.getBackend());
    }

    @Test
    public void backwardsCompatibilityClassExists() {
        // JRebel and HotswapAgent live reload triggering only works if
        // com.vaadin.flow.internal.BrowserLiveReloadAccess exists on classpath.
        ClassLoader classLoader = getClass().getClassLoader();
        String className = "com.vaadin.flow.internal.BrowserLiveReloadAccess";
        String methodName = "getLiveReload";
        try {
            Class<?> clazz = classLoader.loadClass(className);
            clazz.getMethod(methodName, VaadinService.class);
            clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException
                | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail(className
                    + " required on classpath for JRebel / HotswapAgent live reload integration, must be instantiable and have method "
                    + methodName + " accepting a VaadinService");
        }
    }

    public static BrowserLiveReload mockBrowserLiveReloadImpl(
            VaadinContext context) {
        DebugWindowConnection liveReload = Mockito
                .mock(DebugWindowConnection.class);
        context.setAttribute(DebugWindowConnection.class, liveReload);
        return liveReload;
    }

}
