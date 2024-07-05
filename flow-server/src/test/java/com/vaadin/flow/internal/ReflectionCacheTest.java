/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.flow.internal;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.util.TestUtil;

public class ReflectionCacheTest {

    @Test
    public void generateCachedValues() {
        AtomicInteger count = new AtomicInteger();

        ReflectionCache<Object, Integer> cache = new ReflectionCache<>(
                type -> count.incrementAndGet());

        Assert.assertEquals(0, count.get());

        Assert.assertEquals(1, cache.get(Object.class).intValue());
        Assert.assertEquals(2, cache.get(String.class).intValue());

        Assert.assertEquals(1, cache.get(Object.class).intValue());

        Assert.assertEquals(2, count.get());
    }

    @Test
    public void cacheContains() {
        ReflectionCache<Object, Object> cache = new ReflectionCache<>(
                type -> type);

        Assert.assertFalse(cache.contains(Object.class));

        cache.get(Object.class);
        Assert.assertTrue(cache.contains(Object.class));
        Assert.assertFalse(cache.contains(String.class));
    }

    @Test
    public void cacheClear() {
        ReflectionCache<Object, Object> cache = new ReflectionCache<>(
                type -> type);

        cache.get(Object.class);
        Assert.assertTrue(cache.contains(Object.class));

        cache.clear();
        Assert.assertFalse(cache.contains(Object.class));
    }

    @Test
    public void clearAll() {
        ReflectionCache<Object, Object> cache1 = new ReflectionCache<>(
                type -> type);
        ReflectionCache<Object, Object> cache2 = new ReflectionCache<>(
                type -> type);

        cache1.get(Object.class);
        cache2.get(Object.class);

        ReflectionCache.clearAll();

        Assert.assertFalse(cache1.contains(Object.class));
        Assert.assertFalse(cache2.contains(Object.class));
    }

    @Test
    public void cacheIsGarbageCollected() throws InterruptedException {
        ReflectionCache<Object, Object> cache1 = new ReflectionCache<>(
                type -> type);
        WeakReference<ReflectionCache<Object, Object>> ref = new WeakReference<>(
                cache1);

        cache1 = null;
        Assert.assertTrue(TestUtil.isGarbageCollected(ref));
    }

    @Test
    public void cacheIsClearedAfterGc() throws InterruptedException {
        ReflectionCache<Object, Object> cache = new ReflectionCache<>(
                type -> type);
        cache.get(Object.class);

        // Ensure garbage is collected before clearing
        TestUtil.isGarbageCollected(new WeakReference<>(new Object()));

        ReflectionCache.clearAll();

        Assert.assertFalse(cache.contains(Object.class));
    }

    @Test
    public void currentInstancesNotAvailable() {
        String currentString = "My string";
        CurrentInstance.set(String.class, currentString);

        ReflectionCache<Object, String> cache = new ReflectionCache<>(
                type -> type.getSimpleName() + ": "
                        + CurrentInstance.get(String.class));

        try {
            String result = cache.get(Object.class);

            Assert.assertEquals("Current instance should not be in the result",
                    "Object: null", result);
            Assert.assertEquals(
                    "Current instance should be preserved after running",
                    currentString, CurrentInstance.get(String.class));
        } finally {
            CurrentInstance.set(String.class, null);
        }
    }
}
