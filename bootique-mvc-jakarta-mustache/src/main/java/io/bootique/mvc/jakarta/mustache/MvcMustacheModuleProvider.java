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

package io.bootique.mvc.jakarta.mustache;

import io.bootique.BQModuleMetadata;
import io.bootique.BQModuleProvider;
import io.bootique.di.BQModule;
import io.bootique.jersey.v3.JerseyModuleProvider;
import io.bootique.mvc.jakarta.MvcModuleProvider;

import java.util.Collection;

import static java.util.Arrays.asList;

public class MvcMustacheModuleProvider implements BQModuleProvider {

    @Override
    public BQModule module() {
        return new MvcMustacheModule();
    }

    @Override
    public BQModuleMetadata.Builder moduleBuilder() {
        return BQModuleProvider.super
                .moduleBuilder()
                .description("Provides a renderer for bootique-mvc templates based on Mustache framework.");
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return asList(
                new MvcModuleProvider(),
                new JerseyModuleProvider()
        );
    }
}
