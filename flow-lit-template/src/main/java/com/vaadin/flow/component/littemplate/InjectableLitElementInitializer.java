/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.component.littemplate;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.template.internal.AbstractInjectableElementInitializer;
import com.vaadin.flow.dom.Element;

/**
 * Initialize a lit template element with data.
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 *
 * @author Vaadin Ltd
 * @since
 *
 */
public class InjectableLitElementInitializer
        extends AbstractInjectableElementInitializer {

    private static final String DYNAMIC_ATTRIBUTE_PREFIX = "Template {} contains an attribute {} in element {} which";
    private final Class<? extends Component> templateClass;

    /**
     * Creates an initializer for the {@code element}.
     *
     * @param element
     *            element to initialize
     * @param templateClass
     *            the class of the template component
     */
    public InjectableLitElementInitializer(Element element,
            Class<? extends Component> templateClass) {
        super(element);
        this.templateClass = templateClass;
    }

    @Override
    public void accept(Map<String, String> templateAttributes) {
        if (templateAttributes.containsKey("disabled")) {
            String errorMessage = String.format(
                    "Lit template '%s' injected element '%s' with id '%s'"
                            + " uses the disabled attribute.%n"
                            + "Mapped components should instead be disabled "
                            + "using the 'setEnabled(false)' method on the server side.",
                    templateClass.getName(), getElement().getTag(),
                    templateAttributes.get("id"));
            throw new IllegalAttributeException(errorMessage);
        }
        super.accept(templateAttributes);
    }

    @Override
    protected boolean isStaticAttribute(String name, String value) {
        if (name.startsWith("?")) {
            // this is a boolean attribute binding, ignore it since we don't
            // support bindings: the value is not an expression
            getLogger().debug(
                    "{} starts with '?' and ignored by initialization since this is an attribute binding",
                    DYNAMIC_ATTRIBUTE_PREFIX, templateClass.getSimpleName(),
                    name, getElement().getTag());
            return false;
        }
        if (name.startsWith(".")) {
            // this is a property binding, ignore it since we don't support
            // bindings: the value is not an expression
            getLogger().debug(
                    "{} starts with '.' and ignored by initialization since this is a property binding",
                    DYNAMIC_ATTRIBUTE_PREFIX, templateClass.getSimpleName(),
                    name, getElement().getTag());
            return false;
        }
        if (name.startsWith("@")) {
            // this is an event listener
            getLogger().debug(
                    "{} starts with '@' and ignored by initialization since this is an event listener declration",
                    DYNAMIC_ATTRIBUTE_PREFIX, templateClass.getSimpleName(),
                    name, getElement().getTag());
            return false;
        }
        if (value == null) {
            return true;
        }
        if (value.contains("${") && value.contains("}")) {
            // this is a dynamic value
            getLogger().debug(
                    "Template {} contains an attribute {} in element {} whose value"
                            + " is dynamic and it's ignored by initialization",
                    templateClass.getSimpleName(), name, getElement().getTag());
            return false;
        }
        return true;
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(InjectableLitElementInitializer.class);
    }
}
