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

package io.bootique.mvc.resolver;

import io.bootique.mvc.Template;
import io.bootique.resource.FolderResourceFactory;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultTemplateResolver implements TemplateResolver {

    private final Charset templateEncoding;
    private final FolderResourceFactory templateBase;
    private final ConcurrentMap<String, Template> cachedTemplates;

    public DefaultTemplateResolver(FolderResourceFactory templateBase, Charset templateEncoding) {
        this.templateBase = templateBase;
        this.templateEncoding = Objects.requireNonNull(templateEncoding, "Null templateEncoding");
        this.cachedTemplates = new ConcurrentHashMap<>();
    }

    @Override
    public Template resolve(String templateName, Class<?> viewType) {

        Package pkg = viewType.getPackage();

        // the key is not the same as template path. It is simply the cheapest unique String
        // we can produce for name and package name
        String key = pkg != null ? pkg.getName() + templateName : templateName;

        return cachedTemplates.computeIfAbsent(key, k -> createTemplate(templateName, pkg));
    }

    private Template createTemplate(String templateName, Package pkg) {
        String path = pkg != null ? pkg.getName().replace('.', '/') + "/" : "";
        return new DefaultTemplate(templateBase, path, templateName, templateEncoding);
    }
}
