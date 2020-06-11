package spoon.smpl;

import org.junit.BeforeClass;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.support.compiler.ZipFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.smpl.TestUtils.*;

import java.io.File;
import java.io.IOException;

/**
 * This test cherry picks and tests patch application on each true positive from the C4J patch "sticky_broadcast"
 */
public class C4JStickyBroadcastTest {
	private static CtModel spoonModel = null;
	private static SmPLRule rule;

	@BeforeClass
	public static void buildModel() {
		if (spoonModel != null) {
			return;
		}

		rule = SmPLParser.parse("@@\n" +
								"Intent intent;\n" +
								"@@\n" +
								"(\n" +
								"- sendStickyBroadcast(intent);\n" +
								"+ sendBroadcast(intent);\n" +
								"|\n" +
								"- removeStickyBroadcast(intent);\n" +
								")\n");

		Launcher launcher = new Launcher();

		try {
			launcher.addInputResource(new ZipFolder(new File("src/test/resources/C4JSendStickyBroadcast.zip")));
			launcher.buildModel();
		} catch (IOException e) {
			fail("failed to build model");
		}

		spoonModel = launcher.getModel();
	}

	CtClass<?> getClassFromModel(String qualifiedName) {
		for (CtType<?> ctType : spoonModel.getAllTypes()) {
			if (ctType instanceof CtClass && ctType.getQualifiedName().equals(qualifiedName)) {
				return (CtClass<?>) ctType;
			}
		}

		fail("class " + qualifiedName + " not found");
		return null;
	}

	CtClass<?> getInnerClass(String qualifiedName) {
		String[] parts = qualifiedName.split("\\$", 2);
		CtClass<?> class_ = getClassFromModel(parts[0]);
		return class_.getNestedType(parts[1]);
	}

	CtMethod<?> getMethod(String qualifiedName) {
		String[] parts = qualifiedName.split("::", 2);
		CtClass<?> class_ = getClassFromModel(parts[0]);

		return class_.getMethodsByName(parts[1]).stream().findFirst().get();
	}

	CtMethod<?> getMethodFromInnerClass(String qualifiedName) {
		String[] parts = qualifiedName.split("::", 2);
		CtClass<?> class_ = getInnerClass(parts[0]);

		return class_.getMethodsByName(parts[1]).stream().findFirst().get();
	}

	String testMethod(String qualifiedName) {
		return testMethod(getMethod(qualifiedName));
	}

	String testMethod(CtMethod<?> targetMethod) {
		CFGModel model = new CFGModel(methodCfg(targetMethod));
		ModelChecker checker = new ModelChecker(model);
		rule.getFormula().accept(checker);

		ModelChecker.ResultSet results = checker.getResult();
		Transformer.transform(model, results.getAllWitnesses());

		if (results.size() > 0 && rule.getMethodsAdded().size() > 0) {
			Transformer.copyAddedMethods(model, rule);
		}

		model.getCfg().restoreUnsupportedElements();

		return targetMethod.toString();
	}

	private int countOccurrences(String text, String find) {
		return (text.length() - text.replace(find, "").length()) / find.length();
	}

	@Test
	public void testSendBroadcastUploadsAdded() {
		String result = testMethod("com.owncloud.android.files.services.FileUploader::sendBroadcastUploadsAdded");
		assertFalse(result.contains("    start.setPackage(getPackageName());\n    sendStickyBroadcast(start);"));
		assertTrue(result.contains("    start.setPackage(getPackageName());\n    sendBroadcast(start);"));
	}

	@Test
	public void testSendBroadcastUploadStarted() {
		String result = testMethod("com.owncloud.android.files.services.FileUploader::sendBroadcastUploadStarted");
		assertFalse(result.contains("    start.setPackage(getPackageName());\n    sendStickyBroadcast(start);"));
		assertTrue(result.contains("    start.setPackage(getPackageName());\n    sendBroadcast(start);"));
	}

	@Test
	public void testSendBroadcastUploadFinished() {
		String result = testMethod("com.owncloud.android.files.services.FileUploader::sendBroadcastUploadFinished");
		assertFalse(result.contains("    end.setPackage(getPackageName());\n    sendStickyBroadcast(end);"));
		assertTrue(result.contains("    end.setPackage(getPackageName());\n    sendBroadcast(end);"));
	}

	@Test
	public void testSendBroadcastDownloadFinished() {
		String result = testMethod("com.owncloud.android.files.services.FileDownloader::sendBroadcastDownloadFinished");
		assertFalse(result.contains("    end.setPackage(getPackageName());\n    sendStickyBroadcast(end);"));
		assertTrue(result.contains("    end.setPackage(getPackageName());\n    sendBroadcast(end);"));
	}

	@Test
	public void testSendBroadcastNewDownload() {
		String result = testMethod("com.owncloud.android.files.services.FileDownloader::sendBroadcastNewDownload");
		assertFalse(result.contains("    added.setPackage(getPackageName());\n    sendStickyBroadcast(added);"));
		assertTrue(result.contains("    added.setPackage(getPackageName());\n    sendBroadcast(added);"));
	}

	@Test
	public void testOnReceive1() {
		CtMethod<?> method = getMethodFromInnerClass("com.owncloud.android.ui.preview.PreviewImageActivity$DownloadFinishReceiver::onReceive");
		assertEquals(1, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = testMethod(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}

	@Test
	public void testOnReceive2() {
		CtMethod<?> method = getMethodFromInnerClass("com.owncloud.android.ui.activity.FileDisplayActivity$SyncBroadcastReceiver::onReceive");
		assertEquals(2, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = testMethod(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}

	@Test
	public void testOnReceive3() {
		CtMethod<?> method = getMethodFromInnerClass("com.owncloud.android.ui.activity.FileDisplayActivity$UploadFinishReceiver::onReceive");
		assertEquals(1, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = testMethod(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}

	@Test
	public void testOnReceive4() {
		CtMethod<?> method = getMethodFromInnerClass("com.owncloud.android.ui.activity.FileDisplayActivity$DownloadFinishReceiver::onReceive");
		assertEquals(1, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = testMethod(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}

	@Test
	public void testOnReceive5() {
		CtMethod<?> method = getMethodFromInnerClass("com.owncloud.android.ui.activity.FolderPickerActivity$SyncBroadcastReceiver::onReceive");
		assertEquals(2, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = testMethod(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}
}
