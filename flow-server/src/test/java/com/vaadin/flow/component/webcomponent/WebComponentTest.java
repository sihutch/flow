/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */

package com.vaadin.flow.component.webcomponent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.webcomponent.PropertyConfigurationImpl;
import com.vaadin.flow.server.webcomponent.WebComponentBinding;

import elemental.json.Json;
import elemental.json.JsonValue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class WebComponentTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private WebComponent<Component> webComponent;

    @Before
    public void init() {
        WebComponentBinding<Component> componentBinding = new WebComponentBinding<>(
                mock(Component.class));
        webComponent = new WebComponent<>(componentBinding, new Element("tag"));
    }

    @Test
    public void fireEvent_throwsWhenNameIsNull() {
        exception.expect(NullPointerException.class);
        exception.expectMessage("eventName");
        webComponent.fireEvent(null);
    }

    @Test
    public void fireEvent_doesNotThrowOnNullObjectData() {
        webComponent.fireEvent("name", null);
    }

    @Test
    public void fireEvent_throwsWhenOptionsIsNull() {
        exception.expect(NullPointerException.class);
        exception.expectMessage("options");
        webComponent.fireEvent("name", null, null);
    }

    @Test
    public void setProperty_throwsOnNullPropertyConfiguration() {
        exception.expect(NullPointerException.class);
        exception.expectMessage("propertyConfiguration");
        webComponent.setProperty(null, "value");
    }

    @Test
    public void setProperty_throwsOnUnknownProperty() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                "WebComponent does not have a property identified");

        WebComponentBinding<Component> binding = new WebComponentBinding<>(
                mock(Component.class));

        WebComponent<Component> webComponent = new WebComponent<>(binding,
                new Element("tag"));

        PropertyConfigurationImpl<Component, String> configuration = new PropertyConfigurationImpl<>(
                Component.class, "property", String.class, "value");

        webComponent.setProperty(configuration, "newValue");
    }

    @Test
    public void setProperty_throwsWhenGivenWrongPropertyTypeAsParameter() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Property 'property' of type "
                + "'java.lang.Integer' cannot be assigned value of type "
                + "'java.lang.String'!");

        PropertyConfigurationImpl<Component, Integer> intConfiguration = new PropertyConfigurationImpl<>(
                Component.class, "property", Integer.class, 0);

        WebComponentBinding<Component> binding = new WebComponentBinding<>(
                mock(Component.class));
        binding.bindProperty(intConfiguration, false, null);

        WebComponent<Component> webComponent = new WebComponent<>(binding,
                new Element("tag"));

        PropertyConfigurationImpl<Component, String> stringConfiguration = new PropertyConfigurationImpl<>(
                Component.class, "property", String.class, "value");

        webComponent.setProperty(stringConfiguration, "newValue");
    }

    @Test
    public void setProperty_attemptsToWriteSupportedTypes() {
        Element element = spy(new Element("tag"));

        // configurations
        PropertyConfigurationImpl<Component, Integer> intConfiguration = new PropertyConfigurationImpl<>(
                Component.class, "int", Integer.class, 0);
        PropertyConfigurationImpl<Component, Double> doubleConfiguration = new PropertyConfigurationImpl<>(
                Component.class, "double", Double.class, 0.0);
        PropertyConfigurationImpl<Component, String> stringConfiguration = new PropertyConfigurationImpl<>(
                Component.class, "string", String.class, "");
        PropertyConfigurationImpl<Component, Boolean> booleanConfiguration = new PropertyConfigurationImpl<>(
                Component.class, "boolean", Boolean.class, false);
        PropertyConfigurationImpl<Component, JsonValue> jsonConfiguration = new PropertyConfigurationImpl<>(
                Component.class, "json", JsonValue.class, Json.createNull());

        // binding
        WebComponentBinding<Component> binding = new WebComponentBinding<>(
                mock(Component.class));
        binding.bindProperty(intConfiguration, false, null);
        binding.bindProperty(doubleConfiguration, false, null);
        binding.bindProperty(stringConfiguration, false, null);
        binding.bindProperty(booleanConfiguration, false, null);
        binding.bindProperty(jsonConfiguration, false, null);

        // test
        WebComponent<Component> webComponent = new WebComponent<>(binding,
                element);

        webComponent.setProperty(intConfiguration, 1);
        verify(element, Mockito.times(1)).executeJs(
                ArgumentMatchers.anyString(), ArgumentMatchers.any(),
                ArgumentMatchers.any());
        webComponent.setProperty(doubleConfiguration, 1.0);
        verify(element, Mockito.times(2)).executeJs(
                ArgumentMatchers.anyString(), ArgumentMatchers.any(),
                ArgumentMatchers.any());
        webComponent.setProperty(stringConfiguration, "asd");
        verify(element, Mockito.times(3)).executeJs(
                ArgumentMatchers.anyString(), ArgumentMatchers.any(),
                ArgumentMatchers.any());
        webComponent.setProperty(booleanConfiguration, true);
        verify(element, Mockito.times(4)).executeJs(
                ArgumentMatchers.anyString(), ArgumentMatchers.any(),
                ArgumentMatchers.any());
        // JsonValue has a different number of arguments
        webComponent.setProperty(jsonConfiguration, Json.create(true));
        verify(element, Mockito.times(5)).executeJs(
                ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }
}
