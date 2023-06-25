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

import java.util.Map;

public class ByExtensionTemplateRenderers implements TemplateRenderers {

	private final Map<String, TemplateRenderer> renderersByExtension;

	public ByExtensionTemplateRenderers(Map<String, TemplateRenderer> renderersByExtension) {
		// expecting each extension in the map to start with "."
		this.renderersByExtension = renderersByExtension;
	}

	@Override
	public TemplateRenderer getRenderer(Template template) {
		String ext = getExtension(template.getName());
		TemplateRenderer renderer = renderersByExtension.get(ext);
		if (renderer == null) {
			throw new IllegalArgumentException("Unsupported template extension: "
					+ ext
					+ ", supported extensions: "
					+ renderersByExtension.keySet());
		}

		return renderer;
	}

	String getExtension(String path) {
		int dot = path.lastIndexOf('.');
		if (dot <= 0 || dot == path.length() - 1) {
			// TODO: if we only have one renderer, perhaps no extension is fine?
			throw new IllegalArgumentException("Path without extension: " + path);
		}

		// include dot in the result
		return path.substring(dot);
	}
}
