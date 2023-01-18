package com.vaadin.flow.internal;

import java.util.function.Consumer;

public class HardStateNodeReference implements StateNodeReference {
    private final StateNode node;

    public HardStateNodeReference(StateNode node) {
        this.node = node;
    }

    @Override
    public void visitOrChildren(Consumer<StateNode> consumer) {
        consumer.accept(node);
    }

    @Override
    public StateNode getOrParent() {
        return node;
    }

    @Override
    public StateNode get() {
        return node;
    }
}