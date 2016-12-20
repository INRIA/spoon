/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.javapoet;

import com.google.testing.compile.CompilationRule;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.lang.model.element.TypeElement;
import org.junit.Rule;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class AnnotationSpecTest {

  @Retention(RetentionPolicy.RUNTIME)
  public @interface AnnotationA {
  }

  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  public @interface AnnotationB {
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface AnnotationC {
    String value();
  }

  public enum Breakfast {
    WAFFLES, PANCAKES;
    public String toString() { return name() + " with cherries!"; };
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface HasDefaultsAnnotation {

    byte a() default 5;

    short b() default 6;

    int c() default 7;

    long d() default 8;

    float e() default 9.0f;

    double f() default 10.0;

    char[] g() default {0, 0xCAFE, 'z', '€', 'ℕ', '"', '\'', '\t', '\n'};

    boolean h() default true;

    Breakfast i() default Breakfast.WAFFLES;

    AnnotationA j() default @AnnotationA();

    String k() default "maple";

    Class<? extends Annotation> l() default AnnotationB.class;

    int[] m() default {1, 2, 3};

    Breakfast[] n() default {Breakfast.WAFFLES, Breakfast.PANCAKES};

    Breakfast o();

    int p();

    AnnotationC q() default @AnnotationC("foo");

    Class<? extends Number>[] r() default {Byte.class, Short.class, Integer.class, Long.class};

  }

  @HasDefaultsAnnotation(
      o = Breakfast.PANCAKES,
      p = 1701,
      f = 11.1,
      m = {9, 8, 1},
      l = Override.class,
      j = @AnnotationA,
      q = @AnnotationC("bar"),
      r = {Float.class, Double.class})
  public class IsAnnotated {
    // empty
  }

}
