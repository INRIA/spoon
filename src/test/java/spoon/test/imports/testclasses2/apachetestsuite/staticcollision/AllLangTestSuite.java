package spoon.test.imports.testclasses2.apachetestsuite.staticcollision;

/**
 * Created by urli on 22/06/2017.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import spoon.test.imports.testclasses2.apachetestsuite.LangTestSuite;
import spoon.test.imports.testclasses2.apachetestsuite.enums.EnumTestSuite;

/**
 * Test suite for [lang].
 *
 * @author Stephen Colebourne
 * @version $Id$
 */
public class AllLangTestSuite extends TestCase {

    /**
     * Construct a new instance.
     */
    public AllLangTestSuite(String name) {
        super(name);
    }

    /**
     * Command-line interface.
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * Get the suite of tests
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Commons-Lang (all) Tests");
        suite.addTest(LangTestSuite.suite());
        suite.addTest(EnumTestSuite.suite());
        suite.addTest(spoon.test.imports.testclasses2.apachetestsuite.enum2.EnumTestSuite.suite());
        return suite;
    }
}
