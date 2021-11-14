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

package io.bootique.mvc.jakarta.freemarker;

import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.jersey.v3.JerseyModule;
import io.bootique.jetty.v11.junit5.JettyTester;
import io.bootique.junit5.BQApp;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestTool;
import io.bootique.mvc.jakarta.freemarker.views.HelloWorldView;
import io.bootique.mvc.jakarta.freemarker.views.hierarchy.PageView;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@BQTest
public class MvcFreemarkerModuleIT {

    @BQTestTool
    static final JettyTester jetty = JettyTester.create();

    @BQApp
    static final BQRuntime app = Bootique.app("--config=classpath:MvcFreemarkerModuleIT.yml", "-s")
            .autoLoadModules()
            .module(binder -> JerseyModule.extend(binder).addResource(Api.class))
            .module(jetty.moduleReplacingConnectors())
            .createRuntime();

    @Test
    public void shouldRenderSimpleHelloMessage() {
        Response r1 = jetty.getTarget().path("/hello").request().get();
        assertEquals(Response.Status.OK.getStatusCode(), r1.getStatus());
        assertEquals("Hello John Doe!", r1.readEntity(String.class));
    }

    @Test
    public void shouldRenderPageThatUsesHierarchicalLayoutWithAllSectionsPopulated() {
        Response r1 = jetty.getTarget().path("/hierarchy-all").request().get();
        assertEquals(Response.Status.OK.getStatusCode(), r1.getStatus());
        assertEquals("This is custom header.This is custom content.This is custom footer.", r1.readEntity(String.class));
    }

    @Test
    public void shouldRenderPageThatUsesHierarchicalLayoutWithJustSomeSectionsPopulated() {
        Response r1 = jetty.getTarget().path("/hierarchy-some").request().get();
        assertEquals(Response.Status.OK.getStatusCode(), r1.getStatus());
        assertEquals("Default header.This is custom content.", r1.readEntity(String.class));
    }

    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public static class Api {

        @GET
        @Path("/hello")
        public HelloWorldView getV1() {
            return new HelloWorldView("hello-world.ftl", "John", "Doe");
        }

        @GET
        @Path("/hierarchy-all")
        public PageView getAllMacrosPage() {
            return new PageView("all-macros.ftl", Collections.emptyMap());
        }

        @GET
        @Path("/hierarchy-some")
        public PageView getOneMacroPage() {
            return new PageView("one-macro.ftl", Collections.emptyMap());
        }

    }
}
