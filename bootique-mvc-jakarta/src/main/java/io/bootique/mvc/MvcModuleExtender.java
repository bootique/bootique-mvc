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

import io.bootique.ModuleExtender;
import io.bootique.di.Binder;
import io.bootique.di.MapBuilder;
import io.bootique.mvc.renderer.TemplateRenderer;

public class MvcModuleExtender extends ModuleExtender<MvcModuleExtender> {

    private MapBuilder<String, TemplateRenderer> templateRenderers;

    MvcModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public MvcModuleExtender initAllExtensions() {
        contributeRenderers();
        return this;
    }

    public MvcModuleExtender setRenderer(String handledExtension, TemplateRenderer renderer) {
        contributeRenderers().putInstance(handledExtension, renderer);
        return this;
    }

    public MvcModuleExtender setRenderer(String handledExtension, Class<? extends TemplateRenderer> rendererType) {
        contributeRenderers().put(handledExtension, rendererType);
        return this;
    }

    protected MapBuilder<String, TemplateRenderer> contributeRenderers() {
        if (templateRenderers == null) {
            templateRenderers = newMap(String.class, TemplateRenderer.class);
        }
        return templateRenderers;
    }

}
