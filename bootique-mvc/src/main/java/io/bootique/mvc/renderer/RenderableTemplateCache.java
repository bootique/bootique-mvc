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

import java.time.Duration;
import java.util.function.Function;

/**
 * A cache to store provider-specific templates.
 *
 * @since 3.0
 * @deprecated in favor of the Jakarta flavor
 */
@Deprecated(since = "3.0", forRemoval = true)
public interface RenderableTemplateCache {

    /**
     * Creates a dummy cache that does not store anything and reloads entries on every call.
     */
    static RenderableTemplateCache ofNoCache() {
        return new NoCache();
    }

    /**
     * Creates an unbounded cache with the specified entry TTL.
     */
    static RenderableTemplateCache of(Duration ttl) {
        return new TtlCache(ttl.toMillis());
    }

    <T> T get(Template template, Function<Template, T> renderedTemplateMaker);
}
