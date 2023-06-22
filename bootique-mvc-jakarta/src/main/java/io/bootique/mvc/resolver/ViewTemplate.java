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

/**
 * A template implementation that resolves template location using view Java type package to construct
 * template path.
 *
 * @since 3.0
 */
public class ViewTemplate implements Template {

    private final String templateName;
    private final Class<?> viewType;
    private final FolderResourceFactory templateBase;
    private final Charset templateEncoding;

    private volatile URL url;

    public ViewTemplate(
            String templateName,
            Class<?> viewType,
            FolderResourceFactory templateBase,
            Charset templateEncoding) {

        this.templateName = templateName;
        this.viewType = viewType;
        this.templateBase = templateBase;
        this.templateEncoding = templateEncoding;
    }

    @Override
    public URL getUrl() {

        // No synchronization. No harm if the URL is resolved multiple times in parallel
        if (url == null) {
            String path = relativeResourcePath();
            this.url = templateBase.getUrl(path);
        }

        return url;
    }

    @Override
    public Charset getEncoding() {
        return templateEncoding;
    }

    @Override
    public String getName() {
        return templateName;
    }

    protected String relativeResourcePath() {

        // path = viewPackagePath + templateNameWithExt

        String normalizedName = templateName.startsWith("/")
                ? templateName.substring(1) : templateName;

        Package pack = viewType.getPackage();
        String packagePath = pack != null ? pack.getName().replace('.', '/') + "/" : "";
        return packagePath + normalizedName;
    }
}
