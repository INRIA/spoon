#! /bin/python3
"""Script for injecting the latest SNAPSHOT version of Spoon into all pom.xml
files it finds in the current working directory or any subdirectory.

Requires the ``defusedxml`` package to be installed separately.

This script is compatible with Python 3.5+
"""
import xml.etree.ElementTree as ET
import subprocess
import pathlib

from typing import Optional

SPOON_SNAPSHOT_REPO = """
<repository>
    <id>spoon-snapshot-repo</id>
    <name>Maven Repository for Spoon Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    <snapshots/>
</repository>
"""
MAVEN_NAMESPACE = "http://maven.apache.org/POM/4.0.0"
NAMESPACES = {"": MAVEN_NAMESPACE}

MAVEN_VERSIONS_COMMAND = "mvn -B -U versions:use-latest-versions -DallowSnapshots -Dincludes=fr.inria.gforge.spoon".split()


def main():
    ET.register_namespace("", MAVEN_NAMESPACE)
    pom_file = pathlib.Path("pom.xml")
    inject_snapshot_repo(pom_file)
    subprocess.run(MAVEN_VERSIONS_COMMAND, cwd=str(pom_file.parent))



def inject_snapshot_repo(pom_file: pathlib.Path) -> None:
    tree = ET.parse(str(pom_file))
    root = tree.getroot()

    repositories = root.find(in_maven_namespace("repositories"))
    if not repositories:
        repositories = ET.fromstring("<repositories></repositories>")
        root.append(repositories)

    snapshot_repo = ET.fromstring(SPOON_SNAPSHOT_REPO)
    snapshot_repo_url = snapshot_repo.find("url").text

    for repo in repositories.findall(in_maven_namespace("repository")):
        url = repo.find(in_maven_namespace("url")).text
        if url == snapshot_repo_url:
            return

    repositories.append(snapshot_repo)

    tree.write(str(pom_file))


def in_maven_namespace(tag: str) -> str:
    """Wrap the tag in the default Maven namespace.

    If porting this script to Python 3.6+, then this method can be removed and
    one can instead search with a default namespace like so:

    someElement.find(tag, namespaces={"": MAVEN_NAMESPACE})

    This does not appear to work in Python 3.5
    """
    return "{{{}}}{}".format(MAVEN_NAMESPACE, tag)


if __name__ == "__main__":
    main()
