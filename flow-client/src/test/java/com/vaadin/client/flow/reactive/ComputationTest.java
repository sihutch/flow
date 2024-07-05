/**
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>}  for the full
 * license.
 */
package com.vaadin.client.flow.reactive;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class ComputationTest {
    private TestReactiveEventRouter router = new TestReactiveEventRouter();

    private AtomicInteger computeCount = new AtomicInteger();

    @Test
    public void testRerunIfDirty() {
        CountingComputation computation = new CountingComputation(router);

        Assert.assertEquals("Initial compute should not trigger before flush",
                0, computation.getCount());

        Reactive.flush();

        Assert.assertEquals("Flush should trigger initial compute", 1,
                computation.getCount());

        Reactive.flush();

        Assert.assertEquals(
                "Another recompute should not trigger since dependency has not changed",
                1, computation.getCount());

        router.invalidate();

        Assert.assertEquals(
                "Invalidation should not trigger recompute until flush", 1,
                computation.getCount());

        Reactive.flush();

        Assert.assertEquals("Should recompute after flush after invalidation",
                2, computation.getCount());
    }

    @Test
    public void testStopComputationBeforeInitialFlush() {
        CountingComputation computation = new CountingComputation(router);

        computation.stop();

        Reactive.flush();

        Assert.assertEquals(0, computation.getCount());
    }

    @Test
    public void testStopComputationBeforeInvalidate() {
        CountingComputation computation = new CountingComputation(router);

        Reactive.flush();

        computation.stop();

        router.invalidate();

        Reactive.flush();

        Assert.assertEquals(1, computation.getCount());
    }

    @Test
    public void testStopComputationBeforeReflush() {
        CountingComputation computation = new CountingComputation(router);

        Reactive.flush();

        router.invalidate();

        computation.stop();

        Reactive.flush();

        Assert.assertEquals(1, computation.getCount());
    }

    @Test
    public void testChangeDependencies() {

        TestReactiveEventRouter otherRouter = new TestReactiveEventRouter();

        Reactive.runWhenDependenciesChange(() -> {
            int count = computeCount.incrementAndGet();
            if (count % 2 == 0) {
                router.registerRead();
            } else {
                otherRouter.registerRead();
            }
        });

        Reactive.flush();
        // Now depending on otherRouter

        router.invalidate();
        Reactive.flush();

        Assert.assertEquals("Invalidating router should not cause recompute", 1,
                computeCount.get());

        otherRouter.invalidate();
        Reactive.flush();
        // Now depending on otherRouter

        Assert.assertEquals("Invalidating otherRouter should cause recompute",
                2, computeCount.get());

        otherRouter.invalidate();
        Reactive.flush();

        Assert.assertEquals(
                "Invalidating otherRouter should not cause recompute", 2,
                computeCount.get());

        router.invalidate();
        Reactive.flush();
        // Now depending on otherRouter

        Assert.assertEquals("Invalidating router should cause recompute", 3,
                computeCount.get());
    }

    @Test
    public void testReactiveListeners() {
        Reactive.runWhenDependenciesChange(() -> {
            if (computeCount.get() == 0) {
                router.addListener(event -> computeCount.incrementAndGet());
                router.registerRead();
            }
        });

        Reactive.flush();

        Assert.assertEquals(
                "First flush registers listener, but doesn't increment count",
                0, computeCount.get());

        router.invalidate();

        Assert.assertEquals("Listener is fired right away, but removed", 1,
                computeCount.get());

        router.invalidate();
        Assert.assertEquals(
                "Listner has been removed, so count isn't increased", 1,
                computeCount.get());

        Reactive.flush();
        Assert.assertEquals("Listener is not added again", 1,
                computeCount.get());

        router.invalidate();
        Assert.assertEquals("Listner has not been added again", 1,
                computeCount.get());
    }

    @Test
    public void escapeReactive() {
        Reactive.runWhenDependenciesChange(() -> {
            computeCount.incrementAndGet();
            Reactive.runWithComputation(null, router::registerRead);
        });

        Reactive.flush();

        Assert.assertEquals(1, computeCount.get());

        router.invalidate();

        Reactive.flush();

        Assert.assertEquals("No dependency was registered", 1,
                computeCount.get());
    }

    @Test
    public void fireInvalidateEventsWhenStopping() {
        CountingComputation computation = new CountingComputation(router);

        AtomicInteger invalidateCount = new AtomicInteger();

        computation.onNextInvalidate(e -> invalidateCount.incrementAndGet());

        computation.stop();

        Assert.assertEquals(0, computeCount.get());
        Assert.assertEquals(1, invalidateCount.get());
    }

}
