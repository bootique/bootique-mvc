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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

class TtlCache implements RenderableTemplateCache {

    private final long ttlMs;
    private final ConcurrentMap<String, CacheEntry> cache;

    public TtlCache(long ttlMs) {
        this.ttlMs = ttlMs;
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public <T> T get(Template template, Function<Template, T> renderedTemplateMaker) {
        return cache
                .computeIfAbsent(template.getUrl().toExternalForm(), k -> new CacheEntry())
                .updateIfNeeded(template, renderedTemplateMaker);
    }

    class CacheEntry {

        private final Lock lock;

        volatile long expiresOn;
        Object value;

        CacheEntry() {
            // create as expired
            this.expiresOn = System.currentTimeMillis() - 1;
            this.lock = new ReentrantLock();
        }

        <T> T updateIfNeeded(Template template, Function<Template, T> renderedTemplateMaker) {

            long e = this.expiresOn;
            if (e < System.currentTimeMillis()) {

                lock.lock();
                try {

                    // Save a call to System.currentTimeMillis(), instead see if someone else already updated
                    // the template while we were waiting.

                    if (e >= this.expiresOn) {
                        this.value = renderedTemplateMaker.apply(template);
                        this.expiresOn = System.currentTimeMillis() + ttlMs;
                    }

                } finally {
                    lock.unlock();
                }
            }

            return (T) value;
        }
    }
}
