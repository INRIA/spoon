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

import java.util.ArrayList;
import java.util.List;

import spoon.test.imports.testclasses2.apachetestsuite.LangTestSuite;
import spoon.test.imports.testclasses2.apachetestsuite.enums.EnumTestSuite;

/**
 * Test suite for [lang].
 *
 * @author Stephen Colebourne
 * @version $Id$
 */
public class AllLangTestSuite {

    /**
     * Construct a new instance.
     */
    public AllLangTestSuite(String name) {
    }

    /**
     * Command-line interface.
     */
    public static void main(String[] args) {
    }

    /**
     * Get the suite of tests
     */
    public static Object suite() {
        List suite = new ArrayList();
        suite.add(LangTestSuite.suite());
        suite.add(EnumTestSuite.suite());
        suite.add(spoon.test.imports.testclasses2.apachetestsuite.enum2.EnumTestSuite.suite());
        return suite;
    }
}
