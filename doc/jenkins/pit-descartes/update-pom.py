#!/usr/bin/env python
import xml.etree.ElementTree as et

pom_file = "pom.xml"
descartes_file = "descartes-config.xml"

spaces={'xmlns':'http://maven.apache.org/POM/4.0.0','schemaLocation':'http://maven.apache.org/xsd/maven-4.0.0.xsd'}

pomTree = et.parse(pom_file)
descartesTree = et.parse(descartes_file)

rootDescartes = descartesTree.getroot()

pomPitPlugin = pomTree.find(".//{http://maven.apache.org/POM/4.0.0}plugins/{http://maven.apache.org/POM/4.0.0}plugin[{http://maven.apache.org/POM/4.0.0}artifactId='pitest-maven']")

for element in descartesTree.findall('*/'):
    pomPitPlugin.append(element)

pomTree.write(pom_file, encoding="UTF-8", default_namespace=spaces['xmlns'], xml_declaration=False)