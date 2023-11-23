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
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultTemplateResolver_JailbreakTest {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Test
    public void resolve_FilePathBase() {
        assertResolve("/tmp/a/b", "../tName.txt", new File("/tmp/a/b/io/bootique/mvc/tName.txt"));
        assertResolve("/tmp/a/b", "../../../../tName.txt", new File("/tmp/a/b/tName.txt"));
        assertResolve("/tmp/a/b", "../../x/tName.txt", new File("/tmp/a/b/io/bootique/x/tName.txt"));
    }

    @Test
    public void resolve_FilePathBase_Absolute() {
        assertResolve("/tmp/a/b", "../tName.txt", new File("/tmp/a/b/io/bootique/mvc/tName.txt"));
        assertResolve("/tmp/a/b", "../../../../tName.txt", new File("/tmp/a/b/tName.txt"));
        assertResolve("/tmp/a/b", "/x/y/../tName.txt", new File("/tmp/a/b/x/tName.txt"));
    }

    @Test
    public void resolve_FilePathBase_DisallowCrossingRoot() {
        assertBadPath("/tmp/a/b", "../../../../../tName.txt");
    }

    @Test
    public void resolve_FilePathBase_DisallowCrossingRoot_Absolute() {
        assertBadPath("/tmp/a/b", "/x/../../tName.txt");
    }

    private void assertBadPath(String resolverPath, String resourcePath) {
        assertThrows(RuntimeException.class, () ->
                resolver(resolverPath).resolve(resourcePath, DefaultTemplateResolver_JailbreakTest.class).getUrl());
    }

    private void assertResolve(String resolverPath, String resourcePath, File expected) {

        URL expectedUrl;
        try {
            expectedUrl = expected.getCanonicalFile().toURI().toURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(expectedUrl,
                resolver(resolverPath).resolve(resourcePath, DefaultTemplateResolver_JailbreakTest.class).getUrl());
    }

    private DefaultTemplateResolver resolver(String basePath) {
        return new DefaultTemplateResolver(new FolderResourceFactory(basePath), DEFAULT_CHARSET);
    }
}
