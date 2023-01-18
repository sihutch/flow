package com.vaadin.flow.internal;

import java.io.Serializable;
import java.util.function.Consumer;

public interface StateNodeReference extends Serializable {
    StateNode get();

    StateNode getOrParent();

    void visitOrChildren(Consumer<StateNode> consumer);
}
