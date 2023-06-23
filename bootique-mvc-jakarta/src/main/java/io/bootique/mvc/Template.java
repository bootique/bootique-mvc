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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public interface Template {

    String getName();

    Charset getEncoding();

    /**
     * Resolves a URL of this template
     */
    URL getUrl();

    /**
     * Resolves a URL of a related resource (such as sub-template).
     *
     * @since 3.0
     */
    URL getUrl(String resourceName);

    default Reader reader() {
        Charset encoding = getEncoding();
        URL url = getUrl();
        try {
            return new InputStreamReader(url.openStream(), encoding);
        } catch (IOException e) {
            throw new RuntimeException("Error opening URL: " + url, e);
        }
    }

    /**
     * @since 3.0
     */
    default Reader reader(String resourceName) {
        Charset encoding = getEncoding();
        URL url = getUrl(resourceName);
        try {
            return new InputStreamReader(url.openStream(), encoding);
        } catch (IOException e) {
            throw new RuntimeException("Error opening URL: " + url, e);
        }
    }
}