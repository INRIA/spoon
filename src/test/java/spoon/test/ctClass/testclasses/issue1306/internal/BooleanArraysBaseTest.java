/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2017 the original author or authors.
 */
package spoon.test.ctClass.testclasses.issue1306.internal;


/**
 * Base class for testing <code>{@link BooleanArrays}</code>, set up an instance with {@link StandardComparisonStrategy} and
 * another with {@link ComparatorBasedComparisonStrategy}.
 * <p>
 * Is in <code>org.assertj.core.internal</code> package to be able to set {@link BooleanArrays#failures} appropriately.
 * 
 * @author Joel Costigliola
 */
public class BooleanArraysBaseTest {

  protected boolean[] actual;
  protected Failures failures;
  protected BooleanArrays arrays;


  public void setUp() {
    actual = spoon.test.ctClass.testclasses.issue1306.test.BooleanArrays.arrayOf(true, false);
    failures = new Failures();
    arrays = new BooleanArrays();
    arrays.failures = failures;
  }

  public void testAnything() {

  }
}