/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.flow.internal.nodefeature;

import com.vaadin.flow.internal.StateNode;

/**
 * List of nodes describing the child elements of an element.
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public class ElementChildrenList extends StateNodeReferenceList {
    /**
     * Creates a new element children list for the given node.
     *
     * @param node
     *            the node that the list belongs to
     */
    public ElementChildrenList(StateNode node) {
        super(node);
    }

    public void addNode(int index, StateNode node) {
        assert node != null;

        add(index, node);
    }

    public StateNode getNode(int index) {
        return super.get(index).get();
    }

    public StateNode removeNode(int index) {
        return super.remove(index).get();
    }

    public void clear() {
        super.clear();
    }

    public int indexOfNode(StateNode node) {
        for (int i = 0; i < size(); i++) {
            if (node.equals(getNode(i))) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return super.size();
    }
}
