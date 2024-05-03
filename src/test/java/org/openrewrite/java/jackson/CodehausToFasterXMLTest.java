/*
 * Copyright 2024 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.jackson.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.jackson;

import org.junit.jupiter.api.Test;
import org.openrewrite.config.Environment;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class CodehausToFasterXMLTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .recipe(Environment.builder().scanYamlResources().build()
            .activateRecipes("org.openrewrite.java.jackson.CodehausToFasterXML"))
          .parser(JavaParser.fromJavaVersion().classpath(JavaParser.runtimeClasspath()));
    }

    @Test
    void jsonInclude() {
        rewriteRun(
          //language=java
          java(
            """
              import org.codehaus.jackson.map.ObjectMapper;
              import org.codehaus.jackson.map.annotate.JsonSerialize;
              
              public class Test {
                  private static ObjectMapper initializeObjectMapper() {
                      ObjectMapper mapper = new ObjectMapper();
                      return mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
                  }
              }
              """,
            """
              import com.fasterxml.jackson.annotation.JsonInclude.Include;
              import com.fasterxml.jackson.databind.ObjectMapper;
              
              public class Test {
                  private static ObjectMapper initializeObjectMapper() {
                      ObjectMapper mapper = new ObjectMapper();
                      return mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                  }
              }
              """
          )
        );
    }
}
