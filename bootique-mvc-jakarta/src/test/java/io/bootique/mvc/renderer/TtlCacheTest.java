/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.mvc.renderer;

import io.bootique.mvc.Template;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TtlCacheTest {

    @Test
    public void testConcurrentAccess() throws MalformedURLException {

        TtlCache cache = new TtlCache(20);

        URL u1 = new URL("file:/tmp/t1.txt");
        Template t1 = mock(Template.class);
        when(t1.getUrl()).thenReturn(u1);

        URL u2 = new URL("file:/tmp/t2.txt");
        Template t2 = mock(Template.class);
        when(t2.getUrl()).thenReturn(u2);

        AtomicInteger r1c = new AtomicInteger(0);
        AtomicInteger g1c = new AtomicInteger(0);

        AtomicInteger r2c = new AtomicInteger(0);
        AtomicInteger g2c = new AtomicInteger(0);

        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        try {
            for (int i = 0; i < 300; i++) {

                // try to hit different keys in parallel and see how many refreshes have occurred

                threadPool.submit(() -> {
                    g1c.incrementAndGet();
                    cache.get(t1, t -> r1c.incrementAndGet());
                });

                threadPool.submit(() -> {
                    g2c.incrementAndGet();
                    cache.get(t2, t -> r2c.incrementAndGet());
                });

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            threadPool.shutdown();
        }

        assertEquals(300, g1c.get(), "Unexpected number of runs, did something get deadlocked?");
        assertEquals(300, g2c.get(), "Unexpected number of runs, did something get deadlocked?");
        assertTrue(r1c.get() > 5 && r1c.get() < 50, () -> "Unexpected number of c1 refreshes: " + r1c.get());
        assertTrue(r2c.get() > 5 && r2c.get() < 50, () -> "Unexpected number of c2 refreshes: " + r2c.get());
    }
}
