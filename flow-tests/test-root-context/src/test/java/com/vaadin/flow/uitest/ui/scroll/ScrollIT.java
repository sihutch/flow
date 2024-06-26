/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.flow.uitest.ui.scroll;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Objects;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;

public class ScrollIT extends AbstractScrollIT {

    @Test
    public void scrollPositionIsRestoredAfterNavigatingToNewPageAndBack() {
        open();

        final String initialPageUrl = driver.getCurrentUrl();
        final int xScrollAmount = 0;
        final int yScrollAmount = 400;

        scrollBy(xScrollAmount, yScrollAmount);
        checkPageScroll(xScrollAmount, yScrollAmount);

        clickElementWithJs(ScrollView.TRANSITION_URL_ID);

        while (true) {
            if (Objects.equals(initialPageUrl, driver.getCurrentUrl())) {
                checkPageScroll(xScrollAmount, yScrollAmount);
            } else {
                ensureThatNewPageIsNotScrolled();
                break;
            }
        }

        findElement(By.id(LongToOpenView.BACK_BUTTON_ID)).click();

        assertThat("Did not return back on initial page",
                driver.getCurrentUrl(), is(initialPageUrl));
        checkPageScroll(xScrollAmount, yScrollAmount);
    }

    @Test
    public void anchorUrlsWorkProperly() {
        open();

        final int xScrollAmount = 0;
        final int yScrollAmount = 400;

        Point anchorElementLocation = findElement(
                By.id(ScrollView.ANCHOR_DIV_ID)).getLocation();

        scrollBy(xScrollAmount, yScrollAmount);
        checkPageScroll(xScrollAmount, yScrollAmount);

        clickElementWithJs(ScrollView.SIMPLE_ANCHOR_URL_ID);
        checkPageScroll(anchorElementLocation.getX(),
                anchorElementLocation.getY());
        assertThat("Expected url to change to anchor one",
                driver.getCurrentUrl(), endsWith(ScrollView.ANCHOR_URL));

        scrollBy(xScrollAmount, yScrollAmount);
        clickElementWithJs(ScrollView.ROUTER_ANCHOR_URL_ID);
        checkPageScroll(anchorElementLocation.getX(),
                anchorElementLocation.getY());
        assertThat("Expected url to change to anchor one",
                driver.getCurrentUrl(), endsWith(ScrollView.ANCHOR_URL));
    }

    @Test
    public void scrollPositionIsRestoredWhenNavigatingToHistoryWithAnchorLink() {
        open();

        clickElementWithJs(ScrollView.ROUTER_ANCHOR_URL_ID);
        assertThat("Expected url to change to anchor one",
                driver.getCurrentUrl(), endsWith(ScrollView.ANCHOR_URL));

        scrollBy(0, 400);
        final int originalScrollX = getScrollX();
        final int originalScrollY = getScrollY();

        clickElementWithJs(ScrollView.TRANSITION_URL_ID);
        findElement(By.id(LongToOpenView.BACK_BUTTON_ID)).click();

        assertThat("Expected url to change to anchor one",
                driver.getCurrentUrl(), endsWith(ScrollView.ANCHOR_URL));
        checkPageScroll(originalScrollX, originalScrollY);
    }

    @Test
    public void scrollPositionShouldBeAtAnchorWhenNavigatingFromOtherPage() {
        open();

        Point anchorElementLocation = findElement(
                By.id(ScrollView.ANCHOR_DIV_ID)).getLocation();
        scrollBy(0, 400);

        clickElementWithJs(ScrollView.TRANSITION_URL_ID);
        clickElementWithJs(LongToOpenView.ANCHOR_LINK_ID);
        assertThat("Expected url to change to anchor one",
                driver.getCurrentUrl(), endsWith(ScrollView.ANCHOR_URL));
        checkPageScroll(anchorElementLocation.getX(),
                anchorElementLocation.getY());
    }

}
