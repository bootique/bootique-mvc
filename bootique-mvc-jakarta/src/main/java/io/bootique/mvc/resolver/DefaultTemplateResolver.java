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

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

public class DefaultTemplateResolver implements TemplateResolver {

    private Charset templateEncoding;
    private FolderResourceFactory templateBase;

    public DefaultTemplateResolver(FolderResourceFactory templateBase, Charset templateEncoding) {
        this.templateBase = templateBase;
        this.templateEncoding = Objects.requireNonNull(templateEncoding, "Null templateEncoding");
    }

    @Override
    public Template resolve(String templateName, Class<?> viewType) {

        // template is lazy, no need to cache it... Template rendering
        // providers should probably take care of caching of precompiled
        // templates
        return new Template() {

            @Override
            public URL getUrl() {
                return resourceUrl(templateName, viewType);
            }

            @Override
            public Charset getEncoding() {
                return templateEncoding;
            }

            @Override
            public String getName() {
                return templateName;
            }
        };
    }

    protected URL resourceUrl(String templateName, Class<?> viewType) {
        String path = relativeResourcePath(templateName, viewType);
        return templateBase.getUrl(path);
    }

    protected String relativeResourcePath(String templateName, Class<?> viewType) {

        // path = viewPackagePath + templateNameWithExt

        if (templateName.startsWith("/")) {
            templateName = templateName.substring(1);
        }

        Package pack = viewType.getPackage();
        String packagePath = pack != null ? pack.getName().replace('.', '/') + "/" : "";
        return packagePath + templateName;
    }
}
