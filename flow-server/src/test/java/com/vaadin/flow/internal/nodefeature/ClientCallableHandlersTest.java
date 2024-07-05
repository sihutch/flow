/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.internal.nodefeature;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.internal.StateNode;
import com.vaadin.flow.internal.StateTree;

public class ClientCallableHandlersTest {

    @Tag("div")
    static class NonTemplateComponentWithoutEventHandler extends Component {
    }

    @Tag("div")
    static class NonTemplateComponentWithEventHandler extends Component {

        @ClientCallable
        public void publishedMethod1() {

        }
    }

    @Test
    public void attach_noFeature() {
        StateTree tree = new StateTree(new UI().getInternals(),
                ElementChildrenList.class);

        StateNode stateNode = new StateNode(ClientCallableHandlers.class);

        tree.getRootNode().getFeature(ElementChildrenList.class).add(stateNode);
        Assert.assertEquals(0,
                stateNode.getFeature(ClientCallableHandlers.class).size());
    }

    @Test
    public void attach_noComponent() {
        StateTree tree = new StateTree(new UI().getInternals(),
                ElementChildrenList.class);

        StateNode stateNode = new StateNode(ComponentMapping.class,
                ClientCallableHandlers.class);

        tree.getRootNode().getFeature(ElementChildrenList.class).add(stateNode);
        Assert.assertEquals(0,
                stateNode.getFeature(ClientCallableHandlers.class).size());
    }

    @Test
    public void nonTemplateComponentWithEventHandler() {
        UI ui = new UI();
        NonTemplateComponentWithEventHandler component = new NonTemplateComponentWithEventHandler();
        ui.add(component);

        ClientCallableHandlers feature = component.getElement().getNode()
                .getFeature(ClientCallableHandlers.class);
        assertListFeature(feature, "publishedMethod1");
    }

    @Test
    public void nonTemplateComponentWithoutEventHandler() {
        UI ui = new UI();
        NonTemplateComponentWithoutEventHandler component = new NonTemplateComponentWithoutEventHandler();
        ui.add(component);

        ClientCallableHandlers feature = component.getElement().getNode()
                .getFeature(ClientCallableHandlers.class);
        assertListFeature(feature);
    }

    private void assertListFeature(SerializableNodeList<String> feature,
            String... expected) {
        Assert.assertEquals(expected.length, feature.size());
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], feature.get(i));
        }

    }

    private Stream<String> getDeclaredMethods(Class<?> clazz) {
        // Code coverage jacoco adds nice unexpected private static method
        // $jacocoInit which nobody needs
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers())
                        && !Modifier.isPrivate(method.getModifiers()))
                .map(Method::getName);
    }
}
