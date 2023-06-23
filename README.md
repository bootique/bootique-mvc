<!--
  Licensed to ObjectStyle LLC under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ObjectStyle LLC licenses
  this file to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

[![build test deploy](https://github.com/bootique/bootique-mvc/actions/workflows/maven.yml/badge.svg)](https://github.com/bootique/bootique-mvc/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.bootique.mvc/bootique-mvc.svg?colorB=brightgreen)](https://search.maven.org/artifact/io.bootique.mvc/bootique-mvc/)

A basic MVC web framework for [Bootique](http://bootique.io) for processing requests and responding with 
template-generated views. Implemented on top of JAX-RS, specifically [bootique-jersey](https://github.com/bootique/bootique-jersey)). 
bootique-mvc can work with multiple template engines, providing integration with [Mustache](https://mustache.github.io/) 
and [FreeMarker](https://freemarker.apache.org) out of the box. 

This framework is suitable for simple HTML UIs, with minimal server-side rendering (e.g. when most of the UI work is 
done on the client with JavaScript). 

Code examples: [bootique-mvc-demo](https://github.com/bootique-examples/bootique-mvc-demo).

## Usage
### Prerequisites
Include ```bootique-bom```:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.bootique.bom</groupId>
            <artifactId>bootique-bom</artifactId>
            <version>3.0.M1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Include the flavor of bootique-mvc you are planning to use, e.g. Jakarta / Mustache:

```xml
<dependency>
	<groupId>io.bootique.mvc</groupId>
	<artifactId>bootique-mvc-jakarta-mustache</artifactId>
</dependency>
```

### Create HTML page

Create a "view" class extending `AbstractView`. Pay attention to the Java package. Template name defined in constructor 
will be located under the path matching the view package:

```java
package org.example.view;

public class SomePageView extends AbstractView {

    private final String firstName;
    private final String lastName;

    public SomePageView(String firstName, String lastName) {
        super("some-page.mustache");

        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
```
Now let's create a Mustache template in the project resources folder under `org/example/view/some-page.mustache`.
The view object serves as a root context during template rendering. Properties of the object act as models to fill 
the dynamic parts of the template.

```
<html>
<body>
<h1>Hi, {{firstName}} {{lastName}}!</h1>
</body>
</html>
```

Configure the app to resolve templates relative to classpath:

```yaml
mvc:
  templateBase: "classpath:"
```

Finally, create a "controller" class, which is a JAX-RS endpoint that returns views. Note that a single controller 
can return multiple "pages".

```java
@Path("/")
@Produces(MediaType.TEXT_HTML)
public static class Pages {

    @GET
    @Path("some-page")
    public SomePageView somePage(@QueryParam("fn") String firstName, @QueryParam("ln") String lastName) {
        return new SomePageView(firstName, lastName);
    }
}
```

Now when you hit `/some-page?fn=Joe&ln=Smith`, you'd get a page that says "hi".

### Template Resolving

In the example above we set the template base to be "classpath:" root. But it can also be set to a filesystem directory
or a URL on a public web server. The only requirement is that a single base should be used by all templates. 

The rules for template name resolution:

* template paths are resolved relative to the `templateBase` location.
* if a template name DOES NOT start with a forward slash, before resolving the template, a path corresponding to the 
view package is prepended to the name. So in the example above `some-page.mustache` becomes 
`classpath:org/example/view/some-page.mustache`. 
* if a template name starts with a forward slash, it is resolved directly against `templateBase`. So 
`/some-page.mustache` would be resolved as `classpath:some-page.mustache`.
* Template names can reference parent directories via `../`. So `../some-page.mustache` would be resolved as 
`classpath:org/example/some-page.mustache`. If a parent directory is outside of the `templateBase`, an exception is thrown.
* Templates can have includes (called "partials" in Mustache). The rules for resolving includes  are the same as the 
root template above. 