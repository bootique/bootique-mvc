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
 * A template implementation that resolves template locations relative to the template base. The root URL as well
 * as URLs for relative resource names are first prepended with the package name of the specified view Java type
 * before resolving against the base.
 *
 * @since 3.0
 */
public class ViewTemplate implements Template {

    private final String templateName;
    private final FolderResourceFactory templateBase;
    private final Charset templateEncoding;
    private final String packagePath;

    private volatile URL url;

    public ViewTemplate(
            String templateName,
            Class<?> viewType,
            FolderResourceFactory templateBase,
            Charset templateEncoding) {

        this.templateName = templateName;
        this.templateBase = templateBase;
        this.templateEncoding = templateEncoding;

        Package pack = viewType.getPackage();
        this.packagePath = pack != null ? pack.getName().replace('.', '/') + "/" : "";
    }

    @Override
    public URL getUrl() {

        // No synchronization. No harm if the URL is resolved multiple times in parallel
        if (url == null) {
            String path = resourcePath(templateName);
            this.url = templateBase.getUrl(path);
        }

        return url;
    }

    @Override
    public URL getUrl(String resourceName) {
        String path = resourcePath(resourceName);
        return templateBase.getUrl(path);
    }

    @Override
    public Charset getEncoding() {
        return templateEncoding;
    }

    @Override
    public String getName() {
        return templateName;
    }

    protected String resourcePath(String resource) {

        // Similar to "URI.resolve(URI)", if the resource path starts with "/", it is "absolute" and we resolve
        // it against the template root, if it does not - we resolve it relative to the View Java package path

        String path = resource.startsWith("/")
                ? resource
                : packagePath + resource;

        checkPathWithinBounds(path);
        return path;
    }

    protected void checkPathWithinBounds(String resourcePath) {

        if (resourcePath.length() < 2) {
            return;
        }

        int depth = 0;

        // account for windows paths
        String normalizedPath = resourcePath.replace('\\', '/');

        for (String component : normalizedPath.split("/")) {
            if (component.length() > 0) {
                if ("..".equals(component)) {
                    depth--;

                    if (depth < 0) {
                        throw new RuntimeException("Path is outside the template base: " + resourcePath);
                    }

                } else {
                    depth++;
                }
            }
        }
    }
}
