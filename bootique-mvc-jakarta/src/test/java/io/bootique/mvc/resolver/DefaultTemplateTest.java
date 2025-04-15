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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultTemplateTest {

    @Test
    public void checkPathWithinBounds() {

        DefaultTemplate.checkPathWithinBounds("t.txt");
        DefaultTemplate.checkPathWithinBounds("e/t.txt");
        DefaultTemplate.checkPathWithinBounds("/t.txt");
        DefaultTemplate.checkPathWithinBounds("/a/../b/../t.txt");

        assertThrows(RuntimeException.class, () -> DefaultTemplate.checkPathWithinBounds("../t.txt"));
        assertThrows(RuntimeException.class, () -> DefaultTemplate.checkPathWithinBounds("/../a/t.txt"));
    }

    @Test
    public void reader_Classpath() {
        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver"),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                null);

        assertEquals("test", read(template::reader));
    }

    @Test
    public void reader_Child_Classpath() {
        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver"),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                null);

        assertEquals("test-child", read(() -> template.reader("tNameChild.txt")));
    }

    @Test
    public void reader_Classpath_MissingPath() {
        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver-MISSING"),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                null);

        assertThrows(RuntimeException.class, template::reader);
    }

    @Test
    public void reader_Classpath_MissingPath_Recover() {
        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver-MISSING"),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                n -> getClass().getResource("tNameFallback.txt"),
                null);

        assertEquals("test-fallback", read(template::reader));
    }

    @Test
    public void reader_Classpath_MissingFile() {
        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver"),
                "",
                "tName-MISSING.txt",
                StandardCharsets.UTF_8,
                null,
                null);

        assertThrows(RuntimeException.class, template::reader);
    }

    @Test
    public void reader_Classpath_MissingFile_Recover() {
        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver"),
                "",
                "tName-MISSING.txt",
                StandardCharsets.UTF_8,
                n -> getClass().getResource("tNameFallback.txt"),
                null);

        assertEquals("test-fallback", read(template::reader));
    }

    @Test
    public void reader_File() throws URISyntaxException {

        Path p = Path.of(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver/").getUrl().toURI());
        assertTrue(p.toFile().exists());

        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory(p.toString()),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                null);

        assertEquals("test", read(template::reader));
    }

    @Test
    public void reader_Child_File() throws URISyntaxException {

        Path p = Path.of(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver/").getUrl().toURI());
        assertTrue(p.toFile().exists());

        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory(p.toString()),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                null);

        assertEquals("test-child", read(() -> template.reader("tNameChild.txt")));
    }

    @Test
    public void reader_Child_MissingFile() throws URISyntaxException {

        Path p = Path.of(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver/").getUrl().toURI());
        assertTrue(p.toFile().exists());

        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory(p.toString()),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                null);

        assertThrows(RuntimeException.class, () -> template.reader("tNameChild-MISSING.txt"));
    }

    @Test
    public void reader_Child_MissingFile_Recover() throws URISyntaxException {

        Path p = Path.of(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver/").getUrl().toURI());
        assertTrue(p.toFile().exists());

        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory(p.toString()),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                u -> new StringReader("test-in-memory-fallback"));

        assertEquals("test-in-memory-fallback", read(() -> template.reader("tNameChild-MISSING.txt")));
    }

    @Test
    public void reader_Child_MissingPath() throws URISyntaxException {

        Path p = Path.of(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver").getUrl().toURI());
        assertTrue(p.toFile().exists());

        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory(p + "-MISSING"),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                null);

        assertThrows(RuntimeException.class, () -> template.reader("tNameChild.txt"));
    }

    @Test
    public void reader_Child_MissingPath_Recover() throws URISyntaxException {

        Path p = Path.of(
                new FolderResourceFactory("classpath:io/bootique/mvc/resolver").getUrl().toURI());
        assertTrue(p.toFile().exists());

        DefaultTemplate template = new DefaultTemplate(
                new FolderResourceFactory(p + "-MISSING"),
                "",
                "tName.txt",
                StandardCharsets.UTF_8,
                null,
                u -> new StringReader("test-in-memory-fallback"));

        assertEquals("test-in-memory-fallback", read(() -> template.reader("tNameChild.txt")));
    }

    String read(Supplier<Reader> readerMaker) {
        StringBuilder out = new StringBuilder();
        try (Reader r = readerMaker.get()) {
            int i;
            char[] buffer = new char[1024];
            while ((i = r.read(buffer)) != -1) {
                out.append(buffer, 0, i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return out.toString();
    }
}
