/**
 *  Licensed to ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import io.bootique.mvc.renderer.TemplateRendererFactory;
import io.bootique.mvc.resolver.TemplateResolver;

public class AbstractViewWriter implements MessageBodyWriter<AbstractView> {

	private TemplateResolver templateResolver;
	private TemplateRendererFactory templateRendererFactory;

	// TODO dirty ... other properties are set in constructor, but this one
	// awaits injection. So the object is in a partial state for the part of its
	// lifecycle...
	@Context
	private Provider<ResourceInfo> resourceInfoProvider;

	public AbstractViewWriter(TemplateResolver templateResolver, TemplateRendererFactory templateRendererFactory) {
		this.templateResolver = templateResolver;
		this.templateRendererFactory = templateRendererFactory;
	}

	@Override
	public long getSize(AbstractView t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return AbstractView.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(AbstractView t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {

		// doublecheck injection....
		Objects.requireNonNull(resourceInfoProvider, "'resourceInfoProvider' was not injected");

		Writer out = new OutputStreamWriter(entityStream, t.getEncoding());
		Template template = templateResolver.resolve(t.getTemplateName(), t.getClass());
		templateRendererFactory.getRenderer(template).render(out, template, t);

		// flush but do not close the underlying stream
		out.flush();
	}

}
