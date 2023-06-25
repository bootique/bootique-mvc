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
 * A template implementation that resolves template resources relative the template base. The root URL as well
 * as URLs of relative resource names are first prepended with the specified path before resolving against the base.
 *
 * @since 3.0
 */
public class DefaultTemplate implements Template {

    private final FolderResourceFactory base;
    private final String path;
    private final String name;
    private final Charset sourceEncoding;

    private volatile URL url;

    public DefaultTemplate(
            FolderResourceFactory base,
            String path,
            String name,
            Charset sourceEncoding) {

        this.name = name;
        this.base = base;
        this.sourceEncoding = sourceEncoding;
        this.path = path;
    }

    @Override
    public URL getUrl() {

        // No synchronization. No harm if the URL is resolved multiple times if called concurrently the first time
        if (url == null) {
            String path = resourcePath(name);
            this.url = base.getUrl(path);
        }

        return url;
    }

    @Override
    public URL getUrl(String resourceName) {

        // TODO: does it make sense to cache subresource URLs?

        String path = resourcePath(resourceName);
        return base.getUrl(path);
    }

    @Override
    public Charset getEncoding() {
        return sourceEncoding;
    }

    @Override
    public String getName() {
        return name;
    }

    protected String resourcePath(String resource) {

        // Similar to "URI.resolve(URI)", if the resource path starts with "/", it is "absolute" and we resolve
        // it against the template root, if it does not - we resolve it relative to the View Java package path

        String path = resource.startsWith("/")
                ? resource
                : this.path + resource;

        checkPathWithinBounds(path);
        return path;
    }

    protected static void checkPathWithinBounds(String resourcePath) {

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
