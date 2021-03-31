#! /bin/python3
"""Script for injecting the latest SNAPSHOT version of Spoon into all pom.xml
files it finds in the curren tworking directory or any subdirectory.
"""
import xml.etree.ElementTree as ET
import subprocess
import pathlib

SPOON_SNAPSHOT_REPO = """
<repository>
    <id>ow2.org-snapshot</id>
    <name>Maven Repository for Spoon Snapshots</name>
    <url>https://repository.ow2.org/nexus/content/repositories/snapshots/</url>
    <snapshots/>
</repository>
"""
MAVEN_NAMESPACE = "http://maven.apache.org/POM/4.0.0"
NAMESPACES = {"": MAVEN_NAMESPACE}

MAVEN_VERSIONS_COMMAND = "mvn -B versions:use-latest-versions -DallowSnapshots -Dincludes=fr.inria.gforge.spoon".split()


def main():
    ET.register_namespace("", MAVEN_NAMESPACE)
    for pom_file in pathlib.Path(".").rglob("pom.xml"):
        inject_snapshot_repo(pom_file)
        subprocess.run(MAVEN_VERSIONS_COMMAND)


def inject_snapshot_repo(pom_file: pathlib.Path) -> None:
    tree = ET.parse(str(pom_file))
    root = tree.getroot()

    repositories = root.find("repositories", NAMESPACES) or ET.SubElement(
        root, "repositories"
    )
    snapshot_repo = ET.fromstring(SPOON_SNAPSHOT_REPO)
    snapshot_repo_url = snapshot_repo.find("url").text

    for repo in repositories.findall("repository", NAMESPACES):
        url = repo.find("url", NAMESPACES).text
        if url == snapshot_repo_url:
            return

    repositories.append(snapshot_repo)

    tree.write(str(pom_file))


if __name__ == "__main__":
    main()
