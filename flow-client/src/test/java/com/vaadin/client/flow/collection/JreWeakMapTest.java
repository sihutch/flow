/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */

package com.vaadin.client.flow.collection;

import org.junit.Assert;
import org.junit.Test;

public class JreWeakMapTest {

    @Test
    public void testBasicMapOperations() {
        Object one = new Object();
        Object two = new Object();
        Object three = new Object();

        JsWeakMap<Object, Integer> map = JsCollections.weakMap();

        map.set(one, 1).set(two, 2);

        Assert.assertTrue(map.has(one));
        Assert.assertTrue(map.has(two));
        Assert.assertFalse(map.has(three));

        Assert.assertEquals(1, (int) map.get(one));
        Assert.assertEquals(2, (int) map.get(two));
        Assert.assertNull(map.get(three));

        Assert.assertTrue(map.delete(one));
        Assert.assertFalse(map.delete(three));
        Assert.assertFalse(map.has(one));
    }

    @Test
    public void testOnlyObjectKeysAllowed() {
        // All types directly mapped to native counterparts in GWT (Integer is
        // still boxed)
        assertBadKey("string");
        assertBadKey(Double.valueOf(0));
        assertBadKey(Boolean.TRUE);
    }

    private static void assertBadKey(Object key) {
        try {
            JsWeakMap<Object, String> map = JsCollections.weakMap();
            map.set(key, "value");
            Assert.fail("set should throw for " + key);
        } catch (Exception expected) {
            // All is ok
        }
    }
}
