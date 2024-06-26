/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.data.provider;

import java.util.EventObject;
import java.util.Objects;

/**
 * An event fired when the data of a {@code DataProvider} changes.
 *
 * @see DataProviderListener
 *
 * @author Vaadin Ltd
 * @since 1.0
 *
 * @param <T>
 *            the data type
 */
public class DataChangeEvent<T> extends EventObject {

    /**
     * An event fired when a single item of a {@code DataProvider} has been
     * updated.
     *
     * @param <T>
     *            the data type
     */
    public static class DataRefreshEvent<T> extends DataChangeEvent<T> {

        private final T item;
        private boolean refreshChildren;

        /**
         * Creates a new data refresh event originating from the given data
         * provider.
         *
         * @param source
         *            the data provider, not null
         * @param item
         *            the updated item, not null
         */
        public DataRefreshEvent(DataProvider<T, ?> source, T item) {
            this(source, item, false);
        }

        /**
         * Creates a new data refresh event originating from the given data
         * provider.
         *
         * @param source
         *            the data provider, not null
         * @param item
         *            the updated item, not null
         * @param refreshChildren
         *            whether, in hierarchical providers, subelements should be
         *            refreshed as well
         */
        public DataRefreshEvent(DataProvider<T, ?> source, T item,
                boolean refreshChildren) {
            super(source);
            Objects.requireNonNull(item, "Refreshed item can't be null");
            this.item = item;
            this.refreshChildren = refreshChildren;
        }

        /**
         * Gets the refreshed item.
         *
         * @return the refreshed item
         */
        public T getItem() {
            return item;
        }

        /**
         * Gets the a boolean whether the refresh is supposed to be
         * refreshChildren (in hierarchical data providers).
         *
         * @return whether, in hierarchical providers, subelements should be
         *         refreshed as well
         */
        public boolean isRefreshChildren() {
            return refreshChildren;
        }
    }

    /**
     * Creates a new {@code DataChangeEvent} event originating from the given
     * data provider.
     *
     * @param source
     *            the data provider, not null
     */
    public DataChangeEvent(DataProvider<T, ?> source) {
        super(source);
    }

    @Override
    public DataProvider<T, ?> getSource() {
        return (DataProvider<T, ?>) super.getSource();
    }
}
