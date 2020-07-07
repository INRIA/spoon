#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
This script generates a test suite from text files formatted as follows (an "e2e test spec"):

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
"""


import re
import os


def multiline_str(text):
    """
    formats a string value potentially containing linebreaks into a Java-idiomatic
    multiline string on the form "line\n" + ...
    """
    return "\"" + "\\n\" +\n \"".join([x for x in text.split("\n") if x != ""]) + "\\n\"" 



def indent(n, xs):
    """
    indents a given list of lines by n spaces.
    """
    return list(map(lambda x: "{}{}".format(" "*n, x), xs))



def indent_multiline_str(xs):
    """
    indents a Java-idiomatic multiline string such that each line is aligned.
    """
    return xs if len(xs) < 2 else [xs[0]] + indent(xs[0].index("\"") - 1, xs[1:])



def valid_e2e(x):
    """
    given an object, returns the object if it is a dict containing the keys "name", "contract",
    "input", "expected" and "patch" and where "name" holds a valid Java method name, or raises
    an exception if any of these constraints do not hold.
    """
    if type(x) != dict:
        raise RuntimeError("invalid argument")
    
    missing = [s for s in ["name", "contract", "input", "expected", "patch"] if s not in x]
    
    if len(missing) > 0:
        raise RuntimeError("missing {}".format(missing))
    
    if re.sub(r"[^A-Za-z0-9_]", "", x["name"].strip()) != x["name"].strip():
        raise RuntimeError("invalid name")
    
    return x



def parse_e2e(text):
    """
    parses a given text (multiline string value) formatted according to
    
        [section1]
        content1
        
        [section2]
        content2
        
        ...
    
    returns a dict {section1: "content1", section2: "content2" ...}
    """
    return valid_e2e(dict(list(map(lambda x: (x[0], x[1].strip() + "\n"),
                                   re.findall(r"(?s)\[([^\]]+)\](.+?)(?=\[[a-z]|$)", text)))))



def parse_e2e_file(filename):
    """
    parses an e2e test spec in a given filename
    """
    try:
        return parse_e2e(open(filename).read().replace('"', '\\"'))
    except Exception as e:
        raise RuntimeError("invalid e2e file \"{}\": {}".format(filename, str(e))) from None



def test_from_file(filename):
    """
    generates a test case from a given filename (the file should contain an e2e test spec)
    """
    stuff = parse_e2e_file(filename)
    
    output = list()
    
    output.append("@Test")
    output.append("public void test{}() {{".format(re.sub(r"[^A-Za-z0-9_]", "", stuff["name"].strip())))
    output.append("    // contract: {}".format(stuff["contract"]))
    
    output += indent_multiline_str("    String inputCode = {};"
                                        .format(multiline_str(stuff["input"])).split("\n"))
    output.append("")
    
    output += indent_multiline_str("    String expectedCode = {};"
                                        .format(multiline_str(stuff["expected"])).split("\n"))
    output.append("")
    
    output += indent_multiline_str("    String smpl = {};"
                                        .format(multiline_str(stuff["patch"])).split("\n"))
    output.append("")
    
    output.append("    runSingleTest(smpl, inputCode, expectedCode);")
    output.append("}")
    
    return output



def gen_suite_tests(dirname, recursive):
    """
    generates test cases for every file in a given directory name, optionally recursively
    processing subdirectories.
    """
    output = list()
    
    for f in sorted(os.listdir(dirname)):
        filename = "{}/{}".format(dirname, f)
        
        if os.path.isfile(filename):
            output += test_from_file(filename)
        elif recursive and os.path.isdir(filename):
            output += gen_suite_tests(filename, recursive)
    
    return output



def gen_suite(suitename, dirname, recursive=True):
    """
    generates a test suite with a given name from the files in a given directory, optionally
    recursively processing subdirectories.
    """
    output = list()
    
    output.append("package spoon.smpl;")
    output.append("")
    
    output.append("import org.junit.Test;")
    output.append("import static org.junit.Assert.assertEquals;")
    output.append("import static org.junit.Assert.fail;")
    output.append("import spoon.Launcher;")
    output.append("import spoon.reflect.declaration.CtClass;")
    output.append("import static spoon.smpl.TestUtils.*;")
    output.append("")
    
    output.append("public class {} {{".format(suitename))
    
    output.append("    private void runSingleTest(String smpl, String inputCode, String expectedCode) {")
    output.append("        SmPLRule rule = SmPLParser.parse(smpl);")
    output.append("        CtClass<?> input = Launcher.parseClass(inputCode);")
    output.append("        CtClass<?> expected = Launcher.parseClass(expectedCode);")
    output.append("")
    output.append("        input.getMethods().forEach((method) -> {")
    output.append("            if (method.getComments().stream().anyMatch(x -> x.getContent().toLowerCase().equals(\"skip\"))) {")
    output.append("                return;")
    output.append("            }")
    output.append("")
    output.append("            if (!rule.isPotentialMatch(method)) {")
    output.append("                return;")
    output.append("            }")
    output.append("")
    output.append("            CFGModel model = new CFGModel(methodCfg(method));")
    output.append("            ModelChecker checker = new ModelChecker(model);")
    output.append("            rule.getFormula().accept(checker);")
    output.append("")
    output.append("            ModelChecker.ResultSet results = checker.getResult();")
    output.append("")
    output.append("            for (ModelChecker.Result result : results) {")
    output.append("                if (!result.getEnvironment().isEmpty()) {")
    output.append("                    fail(\"nonempty environment\");")
    output.append("                }")
    output.append("            }")
    output.append("")
    output.append("            Transformer.transform(model, results.getAllWitnesses());")
    output.append("")
    output.append("            if (results.size() > 0 && rule.getMethodsAdded().size() > 0) {")
    output.append("                Transformer.copyAddedMethods(model, rule);")
    output.append("            }")
    output.append("            model.getCfg().restoreUnsupportedElements();")
    output.append("        });")
    output.append("")
    output.append("        assertEquals(expected.toString(), input.toString());")
    output.append("    }")
    
    output += indent(4, gen_suite_tests(dirname, recursive))
    
    output.append("}")
    
    return "\n".join(output)



if __name__ == "__main__":
    import sys
    
    if len(sys.argv) < 3:
        print("usage: {} SUITENAME TESTSDIR".format(sys.argv[0]))
        quit()
    
    print(gen_suite(sys.argv[1], sys.argv[2]))
    sys.stderr.write("dont forget to validate the tests!\n")
