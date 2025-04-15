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
package io.bootique.mvc.mustache;

import io.bootique.BQCoreModule;
import io.bootique.BQRuntime;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.mvc.Template;
import io.bootique.mvc.resolver.TemplateResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@BQTest
public class MustacheTemplateRenderer_AllowEmptyTemplatesIT {

    @BQTestTool
    static BQTestFactory factory = new BQTestFactory();

    @Test
    public void disallowEmpty(@TempDir Path tempDir) {

        BQRuntime runtime = factory.app()
                .autoLoadModules()
                .module(b -> BQCoreModule.extend(b).setProperty("bq.mvc.templateBase", tempDir.toString()))
                .createRuntime();

        TemplateResolver resolver = runtime.getInstance(TemplateResolver.class);

        Template template = resolver.resolve("MISSING.mustache", MustacheTemplateRenderer_AllowEmptyTemplatesIT.class);
        assertThrows(RuntimeException.class, () -> read(template::reader));
    }

    @Test
    public void allowEmpty(@TempDir Path tempDir) {

        BQRuntime runtime = factory.app()
                .autoLoadModules()
                .module(b -> BQCoreModule.extend(b).setProperty("bq.mvc.templateBase", tempDir.toString()))
                .module(b -> BQCoreModule.extend(b).setProperty("bq.mvc.allowMissingTemplates", "true"))
                .createRuntime();

        TemplateResolver resolver = runtime.getInstance(TemplateResolver.class);

        Template template = resolver.resolve("MISSING.mustache", MustacheTemplateRenderer_AllowEmptyTemplatesIT.class);
        assertEquals("", read(template::reader));
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
