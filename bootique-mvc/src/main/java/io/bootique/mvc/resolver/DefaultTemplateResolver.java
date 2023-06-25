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

public class DefaultTemplateResolver implements TemplateResolver {

    private final Charset templateEncoding;
    private final FolderResourceFactory templateBase;


    public DefaultTemplateResolver(FolderResourceFactory templateBase, Charset templateEncoding) {
        this.templateBase = templateBase;
        this.templateEncoding = Objects.requireNonNull(templateEncoding, "Null templateEncoding");
    }

    @Override
    public Template resolve(String templateName, Class<?> viewType) {

        // Bootique MVC template is just a URL resolver. So no need to cache it.
        // Actual templates may be cached by rendering engine providers in provider-specific way

        Package pkg = viewType.getPackage();
        String path = pkg != null ? pkg.getName().replace('.', '/') + "/" : "";

        return new DefaultTemplate(templateBase, path, templateName, templateEncoding);
    }
}
