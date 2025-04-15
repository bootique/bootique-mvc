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

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.function.Function;

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
    private final Function<String, URL> onFailedUrl;
    private final Function<URL, Reader> onFailedReader;

    private volatile URL url;

    public DefaultTemplate(
            FolderResourceFactory base,
            String path,
            String name,
            Charset sourceEncoding,
            Function<String, URL> onFailedUrl,
            Function<URL, Reader> onFailedReader) {

        this.name = name;
        this.base = base;
        this.path = path;
        this.sourceEncoding = sourceEncoding;
        this.onFailedUrl = onFailedUrl;
        this.onFailedReader = onFailedReader;
    }

    @Override
    public URL getUrl() {

        // No synchronization. No harm if the URL is resolved multiple times if called concurrently the first time
        if (this.url == null) {
            this.url = getUrl(name);
        }

        return url;
    }

    @Override
    public URL getUrl(String resourceName) {

        // TODO: does it make sense to cache subresource URLs?

        try {
            String path = resourcePath(resourceName);
            return base.getUrl(path);
        } catch (Exception e) {
            return onFailedUrl(resourceName, e);
        }
    }

    @Override
    public Reader reader() {
        Charset encoding = getEncoding();

        URL url = getUrl();

        try {
            return new InputStreamReader(url.openStream(), encoding);
        } catch (Exception e) {
            return onFailedReader(url, e);
        }
    }

    @Override
    public Reader reader(String resourceName) {
        Charset encoding = getEncoding();

        URL url = getUrl(resourceName);
        try {
            return new InputStreamReader(url.openStream(), encoding);
        } catch (Exception e) {
            return onFailedReader(url, e);
        }
    }

    @Override
    public Charset getEncoding() {
        return sourceEncoding;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * A fallback for when finding a reader failed. Allows for optional recovery.
     *
     * @since 3.0
     */
    protected URL onFailedUrl(String resourceName, Exception urlFailure) {
        if (onFailedUrl != null) {
            return onFailedUrl.apply(resourceName);
        }

        throw new RuntimeException("Error resolving URL for resource: " + resourceName, urlFailure);
    }

    /**
     * A fallback for when finding a reader failed. Allows for optional recovery.
     *
     * @since 3.0
     */
    protected Reader onFailedReader(URL url, Exception readerFailure) {
        if (onFailedReader != null) {
            return onFailedReader.apply(url);
        }

        throw new RuntimeException("Error opening URL: " + url, readerFailure);
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
