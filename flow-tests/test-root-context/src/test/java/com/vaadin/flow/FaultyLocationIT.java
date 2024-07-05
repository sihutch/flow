/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.testutil.ChromeBrowserTest;

public class FaultyLocationIT extends ChromeBrowserTest {

    @Test
    public void changeOnClient() {
        open();

        Assert.assertTrue("Faulty URL didn't return a 404 exception page.",
                getDriver().getPageSource()
                        .contains("Could not navigate to '%3Ffaulty'"));

    }

    @Override
    protected String getTestPath() {
        return "/view/%3ffaulty";
    }
}
