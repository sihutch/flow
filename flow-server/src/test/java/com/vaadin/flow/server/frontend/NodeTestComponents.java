/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.server.frontend;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.AbstractTheme;
import com.vaadin.flow.theme.Theme;

/**
 * A container class for all components used in tests.
 */
public class NodeTestComponents extends NodeUpdateTestUtil {

    public static final String BUTTON_COMPONENT_FQN = ButtonComponent.class
            .getName();
    public static final String ICON_COMPONENT_FQN = IconComponent.class
            .getName();

    @NpmPackage(value = "@vaadin/vaadin-button", version = "1.1.1")
    class ButtonComponent extends Component {
    }

    @JsModule("@polymer/iron-icon/iron-icon.js")
    class IconComponent extends Component {
    }

    @JsModule("@vaadin/vaadin-date-picker/src/vaadin-date-picker.js")
    @JsModule("@vaadin/vaadin-date-picker/src/vaadin-month-calendar.js")
    @JavaScript("ExampleConnector.js")
    public static class VaadinBowerComponent extends Component {
    }

    @NpmPackage(value = "@vaadin/vaadin-element-mixin", version = "1.1.2")
    @JsModule("@vaadin/vaadin-element-mixin/vaadin-element-mixin.js")
    public static class VaadinElementMixin extends Component {
    }

    @JsModule("./foo-dir/vaadin-npm-component.js")
    public static class VaadinNpmComponent extends Component {
    }

    @JsModule("vaadin-mixed-component/src/vaadin-mixed-component.js")
    public static class VaadinMixedComponent extends Component {
    }

    @JsModule("./local-template.js")
    @JsModule("3rdparty/component.js")
    public static class LocalTemplate extends Component {
    }

    @JsModule("./local-p3-template.js")
    @NpmPackage(value = "@foo/var-component", version = "1.1.0")
    public static class LocalP3Template extends Component {
    }

    @JsModule("unresolved/component")
    public static class UnresolvedComponent extends Component {
    }

    @JsModule("@vaadin/example-flag/experimental-module-1.js")
    @JsModule("@vaadin/example-flag/experimental-module-2.js")
    @JavaScript("experimental-Connector.js")
    @Tag("example-experimental-component")
    public static class ExampleExperimentalComponent extends Component {
    }

    @Route("flag-view")
    public static class FlagView extends Component {
        ExampleExperimentalComponent component;
    }

    @JsModule("./foo.js")
    @CssImport("@vaadin/vaadin-mixed-component/bar.css")
    @CssImport("./foo.css")
    @CssImport(value = "./foo.css")
    @CssImport(value = "./foo.css", include = "bar")
    @CssImport(value = "./foo.css", id = "baz")
    @CssImport(value = "./foo.css", id = "baz", include = "bar")
    @CssImport(value = "./foo.css", themeFor = "foo-bar")
    @CssImport(value = "./foo.css", themeFor = "foo-bar", include = "bar")
    public static class FlatImport extends Component {
    }

    @JsModule("@vaadin/vaadin-mixed-component/src/vaadin-mixed-component.js")
    @JsModule("@vaadin/vaadin-mixed-component/src/vaadin-something-else.js")
    @JsModule("@vaadin/vaadin-mixed-component/src/vaadin-something-else")
    @JsModule("@vaadin/vaadin-mixed-component/src/vaadin-custom-themed-component.js")
    public static class TranslatedImports extends Component {

    }

    @JsModule("./common-js-file.js")
    @Theme(themeClass = LumoTest.class, variant = LumoTest.DARK)
    @Route
    public static class MainLayout implements RouterLayout {
        @Override
        public Element getElement() {
            return null;
        }
    }

    @Route(value = "", layout = MainLayout.class)
    public static class MainView extends Component {
        ButtonComponent buttonComponent;
        IconComponent iconComponent;
        VaadinBowerComponent vaadinBowerComponent;
        VaadinElementMixin vaadinElementMixin;
        VaadinNpmComponent vaadinNpmComponent;
        VaadinMixedComponent vaadinMixedComponent;
        LocalTemplate localP2Template;
        LocalP3Template localP3Template;
        UnresolvedComponent frontendP3Template;
        FlatImport flatImport;
        TranslatedImports translatedImports;
        JavaScriptOrder order;
    }

    /**
     * Lumo component theme class implementation.
     */
    @JsModule("@vaadin/vaadin-lumo-styles/color.js")
    @JsModule("@vaadin/vaadin-lumo-styles/typography.js")
    @JsModule("@vaadin/vaadin-lumo-styles/sizing.js")
    @JsModule("@vaadin/vaadin-lumo-styles/spacing.js")
    @JsModule("@vaadin/vaadin-lumo-styles/style.js")
    @JsModule("@vaadin/vaadin-lumo-styles/icons.js")
    @JsModule("./lumo-includes.ts")
    public static class LumoTest implements AbstractTheme {

        public static final String LIGHT = "light";
        public static final String DARK = "dark";

        public LumoTest() {
        }

        @Override
        public String getBaseUrl() {
            return "src/";
        }

        @Override
        public String getThemeUrl() {
            return "theme/lumo/";
        }

        @Override
        public List<String> getHeaderInlineContents() {
            return Collections.singletonList("<custom-style>\n"
                    + "    <style include=\"lumo-color lumo-typography\"></style>\n"
                    + "</custom-style>");
        }

        @Override
        public Map<String, String> getHtmlAttributes(String variant) {
            if (variant.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, String> attributes = new HashMap<>(1);
            switch (variant) {
            case LIGHT:
                attributes.put("theme", LIGHT);
                break;
            case DARK:
                attributes.put("theme", DARK);
                break;
            default:
                LoggerFactory.getLogger(LumoTest.class.getName()).warn(
                        "Lumo theme variant not recognized: '{}'. Using no variant.",
                        variant);
            }
            return attributes;
        }
    }

    @NpmPackage(value = "@webcomponents/webcomponentsjs", version = "2.2.10")
    public static class ExtraImport {
    }

    @JavaScript("javascript/a.js")
    @JavaScript("javascript/b.js")
    @JavaScript("javascript/c.js")
    @JsModule("jsmodule/g.js")
    public static class JavaScriptOrder extends Component {

    }
}
