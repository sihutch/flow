/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.di;

import java.io.Serializable;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.polymertemplate.NpmTemplateParser;
import com.vaadin.flow.component.polymertemplate.TemplateParser;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.NavigationEvent;
import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.BootstrapPageResponse;
import com.vaadin.flow.server.DependencyFilter;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.communication.UidlWriter;

/**
 * Delegate for discovering, creating and managing instances of various types
 * used by Flow. Dependency injection frameworks can provide an implementation
 * that manages instances according to the conventions of that framework.
 * <p>
 * {@link VaadinService} will by default use {@link ServiceLoader} for finding
 * an instantiator implementation. Deployment will fail if multiple candidates
 * are returning <code>true</code> from {@link #init(VaadinService)}. If no
 * candidate is found, {@link DefaultInstantiator} will be used. It is possible
 * to override this mechanism by overriding
 * {@link VaadinService#createInstantiator}.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public interface Instantiator extends Serializable {
    /**
     * Initializes this instantiator. This method is run only once and before
     * running any other method. An implementation can opt-out from being used
     * by returning <code>false</code>. It is recommended that all
     * implementations provide a way for application developers to disable an
     * implementation so that it can be present on the classpath without
     * preventing the application from being deployed in cases when multiple
     * candidates are available.
     *
     * @param service
     *            the Vaadin service for which this instance is initialized
     * @return <code>true</code> if this instance should be considered as a
     *         candidate for usage for the provided service; <code>false</code>
     *         to opt-out from the selection process
     * @deprecated The {@link Instantiator} instance should be created by an
     *             {@link InstantiatorFactory} which should just return
     *             {@code null} if the provided {@code service} can't be handled
     *             by it
     */
    @Deprecated
    boolean init(VaadinService service);

    /**
     * Gets all service init listeners to use. In addition to listeners defined
     * in some way native to a specific instantiator, it is also recommended to
     * support the default {@link ServiceLoader} convention. This can be done by
     * including the items from
     * {@link DefaultInstantiator#getServiceInitListeners()} in the returned
     * stream.
     *
     * @return stream of service init listeners, not <code>null</code>
     */
    Stream<VaadinServiceInitListener> getServiceInitListeners();

    /**
     * Processes the available bootstrap listeners. This method can supplement
     * the set of bootstrap listeners provided by
     * {@link VaadinServiceInitListener} implementations.
     * <p>
     * The default implementation returns the original listeners without
     * changes.
     * <p>
     * The order of the listeners inside the stream defines the order of the
     * execution of those listeners by the
     * {@link VaadinService#modifyBootstrapPage(BootstrapPageResponse)} method.
     *
     * @param serviceInitListeners
     *            a stream of bootstrap listeners provided by service init
     *            listeners, not <code>null</code>
     *
     * @return a stream of all bootstrap listeners to use, not <code>null</code>
     */
    default Stream<BootstrapListener> getBootstrapListeners(
            Stream<BootstrapListener> serviceInitListeners) {
        return serviceInitListeners;
    }

    /**
     * Processes the available dependency filters. This method can supplement
     * the set of dependency filters provided by
     * {@link VaadinServiceInitListener} implementations.
     * <p>
     * The default implementation returns the original handlers without changes.
     * <p>
     * The order of the filters inside the stream defines the order of the
     * execution of those listeners by the
     * {@link UidlWriter#createUidl(UI, boolean)} method.
     *
     * @param serviceInitFilters
     *            a stream of dependency filters provided by service init
     *            listeners, not <code>null</code>
     *
     * @return a stream of all dependency filters to use, not <code>null</code>
     */
    default Stream<DependencyFilter> getDependencyFilters(
            Stream<DependencyFilter> serviceInitFilters) {
        return serviceInitFilters;
    }

    /**
     * Provides an instance of any given type, this is an abstraction that
     * allows to make use of DI-frameworks from add-ons.
     * <p>
     * How the object is created and whether it is being cached or not is up to
     * the implementation.
     *
     * @param type
     *            the instance type to create, not <code>null</code>
     * @param <T>
     *            the type of the instance to create
     *
     * @return an instance of the given type
     */
    <T> T getOrCreate(Class<T> type);

    /**
     * Creates an instance of a navigation target or router layout. This method
     * is not called in cases when a component instance is reused when
     * navigating.
     *
     * @param routeTargetType
     *            the instance type to create, not <code>null</code>
     * @param event
     *            the navigation event for which the instance is created, not
     *            <code>null</code>
     * @param <T>
     *            the route target type
     *
     * @return the created instance, not <code>null</code>
     */
    default <T extends HasElement> T createRouteTarget(Class<T> routeTargetType,
            NavigationEvent event) {
        return getOrCreate(routeTargetType);
    }

    /**
     * Creates an instance of a component by its {@code componentClass}.
     *
     * @param componentClass
     *            the instance type to create, not <code>null</code>
     * @param <T>
     *            the component type
     *
     * @return the created instance, not <code>null</code>
     */
    <T extends Component> T createComponent(Class<T> componentClass);

    /**
     * Gets the instantiator to use for the given UI.
     *
     * @param ui
     *            the attached UI for which to find an instantiator, not
     *            <code>null</code>
     * @return the instantiator, not <code>null</code>
     */
    static Instantiator get(UI ui) {
        assert ui != null;

        VaadinSession session = ui.getSession();
        assert session != null;

        return session.getService().getInstantiator();
    }

    /**
     * Get the I18NProvider if one has been defined.
     *
     * @return I18NProvier instance
     */
    default I18NProvider getI18NProvider() {
        return getOrCreate(I18NProvider.class);
    }

    /**
     * Returns {@link TemplateParser} for this service.
     *
     * @return A non-null template parser.
     */
    default TemplateParser getTemplateParser() {
        return NpmTemplateParser.getInstance();
    }
}
