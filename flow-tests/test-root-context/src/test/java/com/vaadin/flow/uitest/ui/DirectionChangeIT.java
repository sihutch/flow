/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */

package com.vaadin.flow.uitest.ui;

import java.util.function.Function;
import java.util.function.Predicate;

import com.vaadin.flow.component.Direction;
import com.vaadin.flow.testutil.ChromeBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DirectionChangeIT extends ChromeBrowserTest {

    private static final String RETURN_DIR_SCRIPT = "return document.dir;";

    @Test
    public void testDirection_changingDirection_updatesDocument() {
        open();

        // the value of document.dir is "" initially
        verifyDirection(null);

        clickElementWithJs("rtl-button");
        verifyDirection(Direction.RIGHT_TO_LEFT);

        clickElementWithJs("ltr-button");
        verifyDirection(Direction.LEFT_TO_RIGHT);
    }

    @Test
    public void testDirection_initialPageDirection_setCorrectly() {
        open("rtl");

        // due to #8028 / 8029 need to wait for JS execution to complete
        waitUntil(webDriver -> ((JavascriptExecutor) driver)
                .executeScript(RETURN_DIR_SCRIPT), 2);

        verifyDirection(Direction.RIGHT_TO_LEFT);

        clickElementWithJs("ltr-button");

        verifyDirection(Direction.LEFT_TO_RIGHT);
    }

    private void verifyDirection(Direction direction) {
        String dir = getDirection();
        Assert.assertEquals(direction == null ? "" : direction.getClientName(),
                dir);
    }

    private String getDirection() {
        return (String) executeScript(RETURN_DIR_SCRIPT);
    }
}
