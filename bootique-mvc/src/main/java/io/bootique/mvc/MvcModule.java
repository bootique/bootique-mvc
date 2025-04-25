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

package io.bootique.mvc;

import io.bootique.BQModule;
import io.bootique.ModuleCrate;
import io.bootique.config.ConfigurationFactory;
import io.bootique.di.Binder;
import io.bootique.di.Provides;
import io.bootique.jersey.JerseyModule;
import io.bootique.mvc.renderer.ByExtensionTemplateRenderers;
import io.bootique.mvc.renderer.RenderableTemplateCache;
import io.bootique.mvc.renderer.TemplateRenderer;
import io.bootique.mvc.renderer.TemplateRenderers;
import io.bootique.mvc.resolver.TemplateResolver;

import jakarta.inject.Singleton;
import java.util.Map;

public class MvcModule implements BQModule {

    private static final String CONFIG_PREFIX = "mvc";

    /**
     * Returns an instance of {@link MvcModuleExtender} used by downstream modules to load custom extensions to the
     * MvcModule. Should be invoked from a downstream Module's "configure" method.
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link MvcModuleExtender} that can be used to load custom extensions to the MvcModule.
     */
    public static MvcModuleExtender extend(Binder binder) {
        return new MvcModuleExtender(binder);
    }

    @Override
    public ModuleCrate crate() {
        return ModuleCrate.of(this)
                .description("Provides Bootique's own REST-based web MVC engine with pluggable view renderers.")
                .config(CONFIG_PREFIX, MvcFactory.class)
                .build();
    }

    @Override
    public void configure(Binder binder) {
        JerseyModule.extend(binder).addFeature(MvcFeature.class);
        MvcModule.extend(binder).initAllExtensions();
    }

    @Singleton
    @Provides
    MvcFeature createMvcFeature(TemplateResolver templateResolver, TemplateRenderers templateRenderers) {
        return new MvcFeature(templateResolver, templateRenderers);
    }

    @Singleton
    @Provides
    TemplateRenderers createTemplateRendererFactory(Map<String, TemplateRenderer> renderersByExtension) {
        return new ByExtensionTemplateRenderers(renderersByExtension);
    }

    @Singleton
    @Provides
    TemplateResolver createTemplateResolver(ConfigurationFactory configFactory) {
        return configFactory.config(MvcFactory.class, CONFIG_PREFIX).createResolver();
    }

    @Singleton
    @Provides
    RenderableTemplateCache createRenderableTemplateCache(ConfigurationFactory configFactory) {
        return configFactory.config(MvcFactory.class, CONFIG_PREFIX).createRenderableTemplateCache();
    }
}
