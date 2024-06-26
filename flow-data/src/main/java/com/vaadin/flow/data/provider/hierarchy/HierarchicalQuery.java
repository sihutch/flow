/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.data.provider.hierarchy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * Immutable hierarchical query object used to request data from a backend.
 * Contains the parent node, index limits, sorting and filtering information.
 *
 * @param <T>
 *            bean type
 * @param <F>
 *            filter type
 * @since 1.2
 */
public class HierarchicalQuery<T, F> extends Query<T, F> {

    private final T parent;

    /**
     * Constructs a new hierarchical query object with given filter and parent
     * node.
     *
     * @param filter
     *            filtering for fetching; can be <code>null</code>
     * @param parent
     *            the hierarchical parent object, <code>null</code>
     *            corresponding to the root node
     */
    public HierarchicalQuery(F filter, T parent) {
        super(filter);
        this.parent = parent;
    }

    /**
     * Constructs a new hierarchical query object with given offset, limit,
     * sorting and filtering.
     *
     * @param offset
     *            first index to fetch
     * @param limit
     *            fetched item count
     * @param sortOrders
     *            sorting order for fetching; used for sorting backends
     * @param inMemorySorting
     *            comparator for sorting in-memory data
     * @param filter
     *            filtering for fetching; can be <code>null</code>
     * @param parent
     *            the hierarchical parent object, <code>null</code>
     *            corresponding to the root node
     */
    public HierarchicalQuery(int offset, int limit,
            List<QuerySortOrder> sortOrders, Comparator<T> inMemorySorting,
            F filter, T parent) {
        super(offset, limit, sortOrders, inMemorySorting, filter);
        this.parent = parent;
    }

    /**
     * Get the hierarchical parent object, where <code>null</code> corresponds
     * to the root node.
     *
     * @return the hierarchical parent object
     */
    public T getParent() {
        return parent;
    }

    /**
     * Get an Optional of the hierarchical parent object.
     *
     * @see #getParent()
     * @return the result of {@link #getParent()} wrapped by an Optional
     */
    public Optional<T> getParentOptional() {
        return Optional.ofNullable(parent);
    }
}
