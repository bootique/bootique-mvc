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

import io.bootique.mvc.renderer.TemplateRenderers;
import io.bootique.mvc.resolver.TemplateResolver;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class AbstractViewWriter implements MessageBodyWriter<AbstractView> {

    private final TemplateResolver templateResolver;
    private final TemplateRenderers templateRenderers;

    public AbstractViewWriter(TemplateResolver templateResolver, TemplateRenderers templateRenderers) {
        this.templateResolver = templateResolver;
        this.templateRenderers = templateRenderers;
    }

    @Override
    public long getSize(AbstractView t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return AbstractView.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(
            AbstractView t,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream)
            throws IOException {

        Writer out = new OutputStreamWriter(entityStream, t.getEncoding());
        Template template = templateResolver.resolve(t.getTemplateName(), t.getClass());
        templateRenderers.getRenderer(template).render(out, template, t);

        // flush but do not close the underlying stream
        out.flush();
    }

}
