package com.vaadin.flow.internal.nodefeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.flow.internal.HardStateNodeReference;
import com.vaadin.flow.internal.StateNode;
import com.vaadin.flow.internal.StateNodeReference;

public class StateNodeReferenceList extends NodeList<StateNodeReference> {
    protected StateNodeReferenceList(StateNode node) {
        super(node);
    }

    @Override
    protected boolean isNodeValues() {
        return true;
    }

    protected void add(int index, StateNode item) {
        assert item != null;

        super.add(index, new HardStateNodeReference(item));
        attachPotentialChild(item);
    }

    @Override
    protected void addAll(Collection<? extends StateNodeReference> items) {
        super.addAll(items);
        items.forEach(this::attachPotentialChild);
    }

    @Override
    protected StateNodeReference remove(int index) {
        StateNodeReference removed = super.remove(index);
        detatchPotentialChild(removed);
        return removed;
    }

    @Override
    protected void clear() {
        int size = size();
        List<StateNode> children = null;
        if (size > 0) {
            children = new ArrayList<>(size);
            forEachChild(children::add);
        }
        super.clear();
        if (size > 0) {
            children.forEach(this::detatchPotentialChild);
        }
    }

    @Override
    public void forEachChild(Consumer<StateNode> action) {
        iterator().forEachRemaining(
                reference -> reference.visitOrChildren(action));
    }
}
