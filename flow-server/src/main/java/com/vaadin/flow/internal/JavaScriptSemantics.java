/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.internal;

import elemental.json.JsonValue;

/**
 * Helpers for replicating JavaScript semantics in Java.
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public class JavaScriptSemantics {
    private JavaScriptSemantics() {
        // Only static helpers
    }

    /**
     * Gets the boolean value of the provided value:
     * <ul>
     * <li><code>null</code> is <code>false</code>.
     * <li>String values are <code>true</code>, except for the empty string.
     * <li>Numerical values are <code>true</code>, except for 0 and
     * <code>NaN</code>.
     * <li>JSON object and JSON array values are always <code>true</code>.
     * </ul>
     *
     * @param value
     *            the value to check for truthness
     * @return <code>true</code> if the provided value is trueish according to
     *         JavaScript semantics, otherwise <code>false</code>
     */
    public static boolean isTrueish(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else if (value instanceof JsonValue) {
            return ((JsonValue) value).asBoolean();
        } else if (value instanceof Number) {
            double number = ((Number) value).doubleValue();
            // Special comparison to keep sonarqube happy
            return !Double.isNaN(number)
                    && Double.doubleToLongBits(number) != 0;
        } else if (value instanceof String) {
            return !((String) value).isEmpty();
        } else {
            throw new IllegalStateException(
                    "Unsupported type: " + value.getClass());
        }
    }

}
