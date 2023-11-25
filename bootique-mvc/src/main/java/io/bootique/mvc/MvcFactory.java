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

package io.bootique.mvc;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.mvc.renderer.RenderableTemplateCache;
import io.bootique.mvc.resolver.DefaultTemplateResolver;
import io.bootique.resource.FolderResourceFactory;
import io.bootique.value.Duration;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @deprecated in favor of the Jakarta flavor
 */
@Deprecated(since = "3.0", forRemoval = true)
@BQConfig("Configures MVC services")
public class MvcFactory {

    private FolderResourceFactory templateBase;
    private Charset templateEncoding;
    private Duration templateTtl;

    public MvcFactory() {
        this.templateBase = new FolderResourceFactory("");
        this.templateEncoding = Charset.forName("UTF-8");
    }

    public RenderableTemplateCache createRenderableTemplateCache() {
        return templateTtl != null
                ? RenderableTemplateCache.of(templateTtl.getDuration())
                : RenderableTemplateCache.ofNoCache();
    }

    public DefaultTemplateResolver createResolver() {
        return new DefaultTemplateResolver(templateBase, templateEncoding);
    }

    /**
     * Sets a base location of templates. Templates paths are built using the
     * following formula:
     * <p>
     * <pre>
     * template_path = templateBase + resource_package_path + template_name_with_extension
     * </pre>
     * <p>
     * Base defines how the template is resolved. It can be a URL, a special
     * "classpath:" URL, or a file path.
     *
     * @param templateBase A base location of templates.
     */
    @BQConfigProperty("Sets a base location of templates. Templates paths are built using the following formula: " +
            "\"template_path = templateBase + resource_package_path + template_name_with_extension\".")
    public void setTemplateBase(FolderResourceFactory templateBase) {
        this.templateBase = Objects.requireNonNull(templateBase);
    }

    /**
     * Sets template encoding. Default is UTF-8.
     *
     * @param templateEncoding expected encoding of templates.
     */
    @BQConfigProperty("Sets template encoding. Default is UTF-8.")
    public void setTemplateEncoding(String templateEncoding) {
        this.templateEncoding = Charset.forName(Objects.requireNonNull(templateEncoding));
    }

    /**
     * @since 3.0
     */
    @BQConfigProperty("Sets template reload time interval. This controls template caching." +
            " By default is not set to anything, causing template reloading on every call")
    public void setTemplateTtl(Duration templateTtl) {
        this.templateTtl = templateTtl;
    }
}
