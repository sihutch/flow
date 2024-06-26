/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.data.binder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the result of a validation. A result may be either successful or
 * contain an error message in case of a failure.
 * <p>
 * ValidationResult instances are created using the factory methods
 * {@link #ok()} and {@link #error(String)}, denoting success and failure
 * respectively.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public interface ValidationResult extends Serializable {

    /**
     * Simple validation result implementation.
     *
     * @author Vaadin Ltd
     * @since 1.0
     *
     */
    class SimpleValidationResult implements ValidationResult {

        private final String error;
        private final ErrorLevel errorLevel;

        SimpleValidationResult(String error, ErrorLevel errorLevel) {
            if (error != null && errorLevel == null) {
                throw new IllegalStateException("ValidationResult has an "
                        + "error message, but no ErrorLevel is provided.");
            }
            this.error = error;
            this.errorLevel = errorLevel;
        }

        @Override
        public String getErrorMessage() {
            if (!getErrorLevel().isPresent()) {
                throw new IllegalStateException("The result is not an error. "
                        + "It cannot contain error message");
            } else {
                return error != null ? error : "";
            }
        }

        public Optional<ErrorLevel> getErrorLevel() {
            return Optional.ofNullable(errorLevel);
        }
    }

    /**
     * Returns the result message.
     * <p>
     * Throws an {@link IllegalStateException} if the result represents success.
     *
     * @return the error message
     * @throws IllegalStateException
     *             if the result represents success
     */
    String getErrorMessage();

    /**
     * Returns optional error level for this validation result. Error level is
     * not present for successful validation results.
     * <p>
     * <strong>Note:</strong> By default {@link ErrorLevel#INFO} and
     * {@link ErrorLevel#WARNING} are not considered to be blocking the
     * validation and conversion chain.
     *
     * @see #isError()
     *
     * @return optional error level; error level is present for validation
     *         results that have not passed validation
     *
     */
    Optional<ErrorLevel> getErrorLevel();

    /**
     * Checks if the result denotes an error.
     * <p>
     * <strong>Note:</strong> By default {@link ErrorLevel#INFO} and
     * {@link ErrorLevel#WARNING} are not considered to be errors.
     *
     * @return <code>true</code> if the result denotes an error,
     *         <code>false</code> otherwise
     */
    default boolean isError() {
        ErrorLevel errorLevel = getErrorLevel().orElse(null);
        return errorLevel != null && errorLevel != ErrorLevel.INFO
                && errorLevel != ErrorLevel.WARNING;
    }

    /**
     * Returns a successful result.
     *
     * @return the successful result
     */
    static ValidationResult ok() {
        return new SimpleValidationResult(null, null);
    }

    /**
     * Creates the validation result which represent an error with the given
     * {@code errorMessage}.
     *
     * @param errorMessage
     *            error message, not {@code null}
     * @return validation result which represent an error with the given
     *         {@code errorMessage}
     * @throws NullPointerException
     *             if {@code errorMessage} is null
     */
    static ValidationResult error(String errorMessage) {
        Objects.requireNonNull(errorMessage);
        return create(errorMessage, ErrorLevel.ERROR);
    }

    /**
     * Creates the validation result with the given {@code errorMessage} and
     * {@code errorLevel}. Results with {@link ErrorLevel} of {@code INFO} or
     * {@code WARNING} are not errors by default.
     *
     * @see #ok()
     * @see #error(String)
     *
     * @param errorMessage
     *            error message, not {@code null}
     * @param errorLevel
     *            error level, not {@code null}
     * @return validation result with the given {@code errorMessage} and
     *         {@code errorLevel}
     * @throws NullPointerException
     *             if {@code errorMessage} or {@code errorLevel} is {@code null}
     *
     */
    static ValidationResult create(String errorMessage, ErrorLevel errorLevel) {
        Objects.requireNonNull(errorMessage);
        Objects.requireNonNull(errorLevel);
        return new SimpleValidationResult(errorMessage, errorLevel);
    }
}
