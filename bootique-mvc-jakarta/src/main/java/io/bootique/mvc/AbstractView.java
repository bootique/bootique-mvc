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

import java.nio.charset.Charset;

/**
 * A superclass of MVC views. It contains a name of the view template, that is
 * resolved outside of the view. AbstractView (or rather its concrete subclass)
 * is used as a root context for resolving the template. So subclasses would
 * normally define getters that allow to access the underlying model objects.
 */
public abstract class AbstractView {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	protected String templateName;
	protected Charset encoding;

	public AbstractView(String templateName) {
		this(templateName, UTF8);
	}

	public AbstractView(String templateName, Charset encoding) {
		this.templateName = templateName;
		this.encoding = encoding;
	}

	public String getTemplateName() {
		return templateName;
	}

	public Charset getEncoding() {
		return encoding;
	}
}
