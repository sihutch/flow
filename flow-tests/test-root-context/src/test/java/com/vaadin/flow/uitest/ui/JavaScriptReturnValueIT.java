/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.uitest.ui;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.flow.testutil.ChromeBrowserTest;

public class JavaScriptReturnValueIT extends ChromeBrowserTest {
    @Test
    public void testAllCombinations() {
        open();

        /*
         * There are 3 * 4 * 2 * 3 = 72 different combinations in the UI, let's
         * test all of them just because we can
         */
        for (String method : Arrays.asList("execPage", "execElement",
                "callElement")) {
            for (String value : Arrays.asList("string", "number", "null",
                    "error")) {
                for (String outcome : Arrays.asList("success", "failure")) {
                    for (String type : Arrays.asList("synchronous",
                            "resolvedpromise", "timeout")) {
                        testCombination(method, value, outcome, type);
                    }
                }
            }
        }
    }

    private void testCombination(String method, String value, String outcome,
            String type) {
        String combinationId = String.join(", ", method, value, outcome, type);
        String expectedStatus = getExpectedStatus(value, outcome);

        for (String target : Arrays.asList("clear", method, value, outcome,
                type, "run")) {
            findElement(By.id(target)).click();
        }

        if ("timeout".equals(type)) {
            try {
                Assert.assertEquals(
                        "Result should not be there immediately for "
                                + combinationId,
                        "Running...", findElement(By.id("status")).getText());

                waitUntil(ExpectedConditions.textToBe(By.id("status"),
                        expectedStatus), 2);
            } catch (TimeoutException e) {
                Assert.fail("Didn't reach expected result for " + combinationId
                        + ". Expected " + expectedStatus + " but got "
                        + findElement(By.id("status")).getText());
                e.printStackTrace();
            }
        } else {
            Assert.assertEquals("Unexpected result for " + combinationId,
                    expectedStatus, findElement(By.id("status")).getText());
        }
    }

    private String getExpectedStatus(String value, String outcome) {
        String prefix = "";
        if ("failure".equals(outcome)) {
            prefix = "Error: ";

            if ("null".equals(value)) {
                // Special case since the null is handled differently for errors
                // and for results
                return prefix + "null";
            } else if ("error".equals(value)) {
                // Message from inside the Error object should be included
                return prefix + "Error: message";
            }
        }

        switch (value) {
        case "string":
            return prefix + "foo";
        case "number":
            return prefix + "42";
        case "null":
            return prefix;
        case "error":
            // JreJsonObject.asString()
            return prefix + "[object Object]";
        default:
            throw new IllegalArgumentException(
                    "Unsupported value type: " + value);
        }
    }

}
