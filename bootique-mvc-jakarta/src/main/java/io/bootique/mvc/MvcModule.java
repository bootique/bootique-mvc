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

import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.di.Binder;
import io.bootique.di.Provides;
import io.bootique.jersey.JerseyModule;
import io.bootique.mvc.renderer.ByExtensionTemplateRendererFactory;
import io.bootique.mvc.renderer.TemplateRenderer;
import io.bootique.mvc.renderer.TemplateRendererFactory;
import io.bootique.mvc.resolver.TemplateResolver;
import io.bootique.mvc.resolver.TemplateResolverFactory;

import javax.inject.Singleton;
import java.util.Map;

public class MvcModule extends ConfigModule {

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
    public void configure(Binder binder) {
        JerseyModule.extend(binder).addFeature(MvcFeature.class);
        MvcModule.extend(binder).initAllExtensions();
    }

    @Singleton
    @Provides
    MvcFeature createMvcFeature(TemplateResolver templateResolver, TemplateRendererFactory templateRendererFactory) {
        return new MvcFeature(templateResolver, templateRendererFactory);
    }

    @Singleton
    @Provides
    TemplateRendererFactory createTemplateRendererFactory(Map<String, TemplateRenderer> renderersByExtension) {
        return new ByExtensionTemplateRendererFactory(renderersByExtension);
    }

    @Singleton
    @Provides
    TemplateResolver createTemplateResolver(ConfigurationFactory configFactory) {
        return config(TemplateResolverFactory.class, configFactory).createResolver();
    }
}
