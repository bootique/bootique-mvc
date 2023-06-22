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

import io.bootique.resource.FolderResourceFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultTemplateResolverTest {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Test
    public void testResolve_EmptyBase() throws MalformedURLException {

        DefaultTemplateResolver resolver = resolver("");

        URL expected = baseUrl("io/bootique/mvc/resolver/tName.txt");
        assertEquals(expected, resolver.resolve("tName.txt", DefaultTemplateResolverTest.class).getUrl());
    }

    @Test
    public void testResolve_EmptyBase_Absolute() throws MalformedURLException {

        DefaultTemplateResolver resolver = resolver("");
        URL expected = baseUrl("/tName.txt");
        assertEquals(expected, resolver.resolve("/tName.txt", DefaultTemplateResolverTest.class).getUrl());
    }

    @Test
    public void testResolve_FilePathBase() throws IOException {

        DefaultTemplateResolver resolver = resolver("/tmp");

        File canonical = new File("/tmp/io/bootique/mvc/resolver/tName.txt").getCanonicalFile();

        assertEquals(canonical.toURI().toURL(),
                resolver.resolve("tName.txt", DefaultTemplateResolverTest.class).getUrl());
    }

    @Test
    public void testResolve_FilePathBase_Absolute() throws IOException {

        DefaultTemplateResolver resolver = resolver("/tmp");

        File canonical = new File("/tmp/tName.txt").getCanonicalFile();

        assertEquals(canonical.toURI().toURL(),
                resolver.resolve("/tName.txt", DefaultTemplateResolverTest.class).getUrl());
    }

    @Test
    public void testResolve_FilePathBase_Slash() throws IOException {

        DefaultTemplateResolver resolver = resolver("/tmp/");
        File canonical = new File("/tmp/io/bootique/mvc/resolver/tName.txt").getCanonicalFile();

        assertEquals(canonical.toURI().toURL(),
                resolver.resolve("tName.txt", DefaultTemplateResolverTest.class).getUrl());
    }

    @Test
    public void testResolve_FilePathBase_Slash_Absolute() throws IOException {

        DefaultTemplateResolver resolver = resolver("/tmp/");
        File canonical = new File("/tmp/tName.txt").getCanonicalFile();
        assertEquals(canonical.toURI().toURL(),
                resolver.resolve("/tName.txt", DefaultTemplateResolverTest.class).getUrl());
    }


    @Test
    public void testResolve_UrlBase() {

        DefaultTemplateResolver resolver = resolver("http://example.org/a");

        assertEquals("http://example.org/a/io/bootique/mvc/resolver/tName.txt",
                resolver.resolve("tName.txt", DefaultTemplateResolverTest.class).getUrl().toExternalForm());
    }

    @Test
    public void testResolve_UrlBase_Absolute() {

        DefaultTemplateResolver resolver = resolver("http://example.org/a");
        assertEquals("http://example.org/a/tName.txt",
                resolver.resolve("/tName.txt", DefaultTemplateResolverTest.class).getUrl().toExternalForm());
    }

    @Test
    public void testResolve_UrlBase_Slash() {

        DefaultTemplateResolver resolver = resolver("http://example.org/a/");

        assertEquals("http://example.org/a/io/bootique/mvc/resolver/tName.txt",
                resolver.resolve("tName.txt", DefaultTemplateResolverTest.class).getUrl().toExternalForm());
    }

    @Test
    public void testResolve_UrlBase_Slash_Absolute() {

        DefaultTemplateResolver resolver = resolver("http://example.org/a/");
        assertEquals("http://example.org/a/tName.txt",
                resolver.resolve("/tName.txt", DefaultTemplateResolverTest.class).getUrl().toExternalForm());
    }

    @Test
    public void testResolve_ClasspathBase() throws MalformedURLException {

        DefaultTemplateResolver resolver = resolver("classpath:");

        URL expected = baseClasspathUrl("io/bootique/mvc/resolver/tName.txt");
        assertEquals(expected, resolver.resolve("tName.txt", DefaultTemplateResolverTest.class).getUrl());
    }

    @Test
    public void testResolve_ClasspathBase_Absolute() throws MalformedURLException {

        DefaultTemplateResolver resolver = resolver("classpath:");

        URL expected = baseClasspathUrl("rootName.txt");
        assertEquals(expected, resolver.resolve("/rootName.txt", DefaultTemplateResolverTest.class).getUrl(),
                () -> "Expected root resource regardless of the view class");
    }

    @Test
    public void testResolve_ClasspathBase_Slash() throws MalformedURLException {
        DefaultTemplateResolver resolver = resolver("classpath:/");
        assertEquals(baseClasspathUrl("io/bootique/mvc/resolver/tName.txt"),
                resolver.resolve("tName.txt", DefaultTemplateResolverTest.class).getUrl());
    }

    private DefaultTemplateResolver resolver(String basePath) {
        return new DefaultTemplateResolver(new FolderResourceFactory(basePath), DEFAULT_CHARSET);
    }

    private URL baseClasspathUrl(String resourceRelativePath) throws MalformedURLException {
        return baseUrl("target/test-classes/", resourceRelativePath);
    }

    private URL baseUrl(String... relativePaths) throws MalformedURLException {
        return Paths.get(System.getProperty("user.dir"), relativePaths).toUri().toURL();
    }
}
