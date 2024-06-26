/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.uitest.ui.template;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.WebElement;

import com.vaadin.flow.testcategory.IgnoreOSGi;
import com.vaadin.flow.testutil.ChromeBrowserTest;
import com.vaadin.testbench.TestBenchElement;

public class TemplateInTemplateWithIdIT extends ChromeBrowserTest {

    @Test
    public void childTemplateInstanceHandlesEvent() {
        open();

        TestBenchElement template = $(TestBenchElement.class).id("template");
        TestBenchElement child = template.$(TestBenchElement.class).id("child");

        WebElement text = child.$(TestBenchElement.class).id("text");
        Assert.assertEquals("@Id injected!", text.getText());
    }
}
