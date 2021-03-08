#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
    This script tries to find tests with missing contract comments.
    
    Example usage: $ ./tools/missing-contracts.py src/test
"""

import sys
import re
import os

def files(dir):
    result = list()
    
    for file in os.listdir(dir):
        file = f"{dir}/{file}"
        
        if os.path.isdir(file):
            result += files(file)
        elif os.path.isfile(file):
            result.append(file)
    
    return result


def javafiles(dir):
    return list(filter(lambda filename: re.match(r"^.+\.java$", filename) != None, files(dir)))


def balanced(s, open, close):
    def balanced_inner(s, open, close, offset, depth):
        string = False
        
        while True:
            if offset >= len(s) or (not string and depth == 1 and s[offset] == close):
                break
            
            if s[offset] == '"':
                string = not string
            elif not string:
                depth += (1 if s[offset] == open else -1 if s[offset] == close else 0)
            
            offset += 1
        
        return offset + 1
    
    return s[0:balanced_inner(s, open, close, 0, 0)]


def tests(file):
    text = open(file).read()
    result = list()
    
    for match in re.finditer(r"@Test", text):
        result.append((file, balanced(text[match.span()[0]:], "{", "}")))
    
    return result


def methodname(testmethodsource):
    return re.search(r"public void ([^\(]+)", testmethodsource).group(1)


def has_contract_comment(testmethodsource):
    return len(re.findall(r"(?i)//\s*contract:", testmethodsource)) > 0


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("usage: {} path/to/source".format(sys.argv[0]))
        quit()
    
    sources = [(file, test_source) for suite in list(map(tests, javafiles(sys.argv[1]))) for (file, test_source) in suite]
    
    for (file, source) in sources:
        if not has_contract_comment(source):
            print(file, methodname(source))
