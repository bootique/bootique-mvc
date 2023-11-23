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
import io.bootique.mvc.resolver.DefaultTemplate;
import io.bootique.resource.FolderResourceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@BQTest
public class MustacheTemplateRenderer_TemplateTtlIT {

    @BQTestTool
    static BQTestFactory factory = new BQTestFactory();

    @Test
    public void default_NoCache(@TempDir Path tempDir) {

        BQRuntime runtime = factory.app()
                .autoLoadModules()
                .module(b -> BQCoreModule.extend(b).setProperty("bq.mvc.templateBase", tempDir.toString()))
                .createRuntime();

        MustacheTemplateRenderer renderer = runtime.getInstance(MustacheTemplateRenderer.class);

        Template template = template(tempDir, "t.mustache");

        writeTemplate(template, "v1: {{a}}");

        assertRender(renderer, template, Map.of("a", "A"), "v1: A\n");
        assertRender(renderer, template, Map.of("a", "AA"), "v1: AA\n");

        writeTemplate(template, "v2: {{a}}");

        assertRender(renderer, template, Map.of("a", "A"), "v2: A\n");
        assertRender(renderer, template, Map.of("a", "AA"), "v2: AA\n");
    }

    @Test
    public void ttl(@TempDir Path tempDir) throws InterruptedException {

        BQRuntime runtime = factory.app()
                .autoLoadModules()
                .module(b -> BQCoreModule.extend(b).setProperty("bq.mvc.templateBase", tempDir.toString()))
                .module(b -> BQCoreModule.extend(b).setProperty("bq.mvc.templateTtl", "300ms"))
                .createRuntime();

        MustacheTemplateRenderer renderer = runtime.getInstance(MustacheTemplateRenderer.class);

        Template template = template(tempDir, "t.mustache");

        writeTemplate(template, "v1: {{a}}");
        assertRender(renderer, template, Map.of("a", "A"), "v1: A\n");

        writeTemplate(template, "v2: {{a}}");
        assertRender(renderer, template, Map.of("a", "A"), "v1: A\n");

        Thread.sleep(301);
        assertRender(renderer, template, Map.of("a", "A"), "v2: A\n");
    }

    private static Template template(Path targetDir, String name) {
        return new DefaultTemplate(
                new FolderResourceFactory(targetDir.toString()),
                "",
                name,
                StandardCharsets.UTF_8);
    }

    private static void writeTemplate(Template template, String contents) {
        try {
            Files.write(
                    Paths.get(template.getUrl().toURI()),
                    List.of(contents),
                    StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertRender(
            MustacheTemplateRenderer renderer,
            Template template,
            Map<String, Object> context,
            String expected) {

        StringWriter out = new StringWriter();

        try {
            renderer.render(out, template, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(expected, out.toString());
    }
}
