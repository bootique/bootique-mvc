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
import io.bootique.Bootique;
import io.bootique.jersey.JerseyModule;
import io.bootique.jetty.junit5.JettyTester;
import io.bootique.junit5.BQApp;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestTool;
import io.bootique.mvc.mustache.view.ConcreteView;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@BQTest
public class PartialsIT {

    @BQTestTool
    static final JettyTester jetty = JettyTester.create();

    @BQApp
    public static BQRuntime app = Bootique.app("-s")
            .autoLoadModules()
            .module(b -> BQCoreModule.extend(b).setProperty("bq.mvc.templateBase", "classpath:"))
            .module(b -> JerseyModule.extend(b).addResource(Api.class))
            .module(jetty.moduleReplacingConnectors())
            .createRuntime();

    @Test
    public void test() {
        Response r = jetty.getTarget().path("/").request().get();
        JettyTester.assertOk(r)
                .assertContent("<p1start>one<p1end>zero<p2start>two<p2end>");
    }

    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public static class Api {

        @GET
        public ConcreteView getV1() {
            Model m = new Model("zero", "one", "two");
            return new ConcreteView("PartialsIT.mustache", m);
        }
    }

    public static class Model {
        private final String p0;
        private final String p1;
        private final String p2;

        public Model(String p0, String p1, String p2) {
            this.p0 = p0;
            this.p1 = p1;
            this.p2 = p2;
        }

        public String getP0() {
            return p0;
        }

        public String getP1() {
            return p1;
        }

        public String getP2() {
            return p2;
        }
    }
}
