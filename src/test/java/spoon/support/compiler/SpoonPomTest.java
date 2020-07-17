package spoon.support.compiler;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;
import spoon.MavenLauncher;
import spoon.support.StandardEnvironment;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SpoonPomTest {

	@Test
	public void getSourceVersion() throws IOException, XmlPullParserException {
		checkVersion("src/test/resources/maven-launcher/null-build/pom.xml", 11);
		checkVersion("src/test/resources/maven-launcher/java-11/pom.xml", 11);
		checkVersion("src/test/resources/maven-launcher/pac4j/pom.xml", 8);
		checkVersion("src/test/resources/maven-launcher/source-directory/pom.xml", 8);
		checkVersion("src/test/resources/maven-launcher/very-simple/pom.xml", 8);
		checkVersion("pom.xml", 8);

	}

	public void checkVersion(String path, int expected) throws IOException, XmlPullParserException {
		SpoonPom pomModel = new SpoonPom(path, null, MavenLauncher.SOURCE_TYPE.APP_SOURCE, new StandardEnvironment());
		int version = pomModel.getSourceVersion();

		//contract: Java version is read accurately from pom and does not trigger exceptions
		assertEquals(expected, version);
	}
}