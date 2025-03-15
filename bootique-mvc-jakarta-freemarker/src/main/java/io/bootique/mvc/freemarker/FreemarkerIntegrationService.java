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

package io.bootique.mvc.freemarker;

import freemarker.cache.TemplateLookupContext;
import freemarker.cache.TemplateLookupResult;
import freemarker.cache.TemplateLookupStrategy;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.bootique.BootiqueException;

import jakarta.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class FreemarkerIntegrationService {

    private final Configuration config;

    @Inject
    public FreemarkerIntegrationService() {
        this.config = new Configuration(Configuration.getVersion());

        this.config.setTemplateLoader(new URLTemplateLoader() {
            @Override
            protected URL getURL(String name) {
                try {
                    return new URL(name);
                } catch (MalformedURLException e) {
                    throw new BootiqueException(-1, e.getMessage(), e);
                }
            }
        });
        this.config.setTemplateLookupStrategy(new TemplateLookupStrategy() {
            @Override
            public TemplateLookupResult lookup(TemplateLookupContext templateLookupContext) throws IOException {

                // root template, can be different from the template we currently look up
                // (e.g. in case of #include directive)
                io.bootique.mvc.Template bqTemplate = (io.bootique.mvc.Template) templateLookupContext
                        .getCustomLookupCondition();
                URL templateUrl = bqTemplate.getUrl(templateLookupContext.getTemplateName());
                return templateLookupContext.lookupWithAcquisitionStrategy(templateUrl.toString());
            }
        });
        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.config.setLogTemplateExceptions(false);
    }

    Template getTemplate(io.bootique.mvc.Template bqTemplate) throws IOException {
        return config.getTemplate(
                bqTemplate.getName(),
                null,
                bqTemplate,
                bqTemplate.getEncoding().name(),
                true,
                false);
    }
}
