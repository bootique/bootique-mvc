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

package io.bootique.mvc.mustache;

import io.bootique.BQModuleProvider;
import io.bootique.ModuleCrate;
import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.di.Provides;
import io.bootique.jersey.JerseyModule;
import io.bootique.mvc.MvcModule;
import io.bootique.mvc.renderer.RenderableTemplateCache;

import javax.inject.Singleton;
import java.util.Collection;

import static java.util.Arrays.asList;

public class MvcMustacheModule implements BQModule, BQModuleProvider {

    @Override
    public ModuleCrate moduleCrate() {
        return ModuleCrate.of(this)
                .description("Integrates Mustache-based renderer for bootique-mvc.")
                .build();
    }

    @Override
    @Deprecated(since = "3.0", forRemoval = true)
    public Collection<BQModuleProvider> dependencies() {
        return asList(new MvcModule(), new JerseyModule());
    }

    @Override
    public void configure(Binder binder) {
        MvcModule.extend(binder).setRenderer(".mustache", MustacheTemplateRenderer.class);
    }

    @Provides
    @Singleton
    MustacheTemplateRenderer provideTemplateRenderer(RenderableTemplateCache cache) {
        return new MustacheTemplateRenderer(cache);
    }
}
