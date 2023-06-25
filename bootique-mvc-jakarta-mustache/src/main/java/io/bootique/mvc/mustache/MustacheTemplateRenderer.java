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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheResolver;
import io.bootique.mvc.Template;
import io.bootique.mvc.renderer.TemplateRenderer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

/**
 * A template renderer that locates child templates based on the location of the root template.
 */
public class MustacheTemplateRenderer implements TemplateRenderer {

    // we have to resort to the ugly ThreadLocal approach due to the lack of rendering context in Mustache Java library
    private final ThreadLocal<Template> templateContext;
    private final MustacheFactory mustacheFactory;

    public MustacheTemplateRenderer() {
        this.templateContext = new ThreadLocal<>();
        this.mustacheFactory = new DefaultMustacheFactory(new ContextAwareMustacheResolver());
    }

    @Override
    public void render(Writer out, Template template, Object rootModel) throws IOException {

        // TODO: cache templates...

        Mustache mustache = compile(template);
        mustache.execute(out, rootModel).flush();
    }

    /**
     * Encapsulates context-aware Mustache template compilation logic.
     */
    protected Mustache compile(Template template) {

        startContext(template);
        try {
            // presumably Mustache closes the reader on its own...
            Reader reader = template.reader();
            return mustacheFactory.compile(reader, template.getName());
        } finally {
            endContext();
        }
    }

    private void startContext(Template template) {
        templateContext.set(template);
    }

    private void endContext() {
        templateContext.set(null);
    }

    class ContextAwareMustacheResolver implements MustacheResolver {

        @Override
        public Reader getReader(String resourceName) {
            Template template = Objects.requireNonNull(
                    templateContext.get(),
                    "No root template, called outside of compilation context");

            return template.reader(resourceName);
        }
    }
}
