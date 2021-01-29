#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
    This script generates a class EndToEndTests that runs all end-to-end tests found
    under a given path.
    
    Example: $ ./gen-e2e-testsuite.py src/test/resources/endtoend
    
    The end-to-end tests should be plain text files formatted as follows:
    
        [name]
        SomeTestName
        
        [contract]
        A description of the contract being tested
        
        [input]
        << code for a java class >>
        
        [expected]
        << code for a java class >>
        
        [patch]
        << smpl patch text >>
    
    The [sections] can be given in any order.
"""

import os
import sys
import re

template = """
package spoon.smpl;

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import static spoon.smpl.TestUtils.*;

public class EndToEndTests {
    private CtClass<?> getTargetClass(String code) {
        CtModel model = SpoonJavaParser.parse(code);
        return (CtClass<?>) model.getRootPackage()
                                 .getTypes()
                                 .stream()
                                 .filter(ctType -> ctType.getComments()
                                                         .stream()
                                                         .noneMatch(comment -> comment.getContent()
                                                                                      .contains("skip")))
                                 .findFirst().get();
    }

    private void runSingleTest(String smpl, String inputCode, String expectedCode) {
        SmPLRule rule = SmPLParser.parse(smpl);
        CtClass<?> input = getTargetClass(inputCode);
        CtClass<?> expected = getTargetClass(expectedCode);

        input.getMethods().forEach((method) -> {
            if (method.getComments().stream().anyMatch(x -> x.getContent().toLowerCase().equals("skip"))) {
                return;
            }

            if (!rule.isPotentialMatch(method)) {
                return;
            }

            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);

            ModelChecker.ResultSet results = checker.getResult();

            for (ModelChecker.Result result : results) {
                if (!result.getEnvironment().isEmpty()) {
                    fail("nonempty environment");
                }
            }

            Transformer.transform(model, results.getAllWitnesses());

            if (results.size() > 0 && rule.getMethodsAdded().size() > 0) {
                Transformer.copyAddedMethods(model, rule);
            }
            model.getCfg().restoreUnsupportedElements();
        });

        assertEquals(expected.toString(), input.toString());
    }
__STUFF__
}
"""

def all_files(path):
    output = list()
    
    for f in os.listdir(path):
        filename = "{}/{}".format(path, f)
        
        if os.path.isfile(filename) and ".txt" in filename:
            output += [filename]
        elif os.path.isdir(filename):
            output += all_files(filename)
    
    return sorted(output)


def get_contract(filename):
    return re.search(r"(?<=\[contract\]\n)[^\n]+", open(filename).read()).group()


def gen_single(filename):
    output = list()
    
    output.append("")
    output.append("    @Test")
    output.append("    public void test_{}() {{".format(re.sub(r"\.txt$", "", filename).replace("/", "_")))
    output.append("        // contract: {}".format(get_contract(filename)))
    output.append("        Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault(\"{}\", \"\"));".format(filename))
    output.append("        runSingleTest(test.get(\"patch\"), test.get(\"input\"), test.get(\"expected\"));")
    output.append("    }")
    
    return output


def gen_suite(path):
    stuff = list()
    
    for file in all_files(path):
        stuff += gen_single(file)
    
    return template.replace("__STUFF__", "\n".join(stuff))


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("usage: {} path/to/endtoendtestfiles".format(sys.argv[0]))
        quit()
    
    print(gen_suite(sys.argv[1]))
