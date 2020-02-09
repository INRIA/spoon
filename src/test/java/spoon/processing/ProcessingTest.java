/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.processing;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtType;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.test.processing.processors.MyProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ProcessingTest {

	@Test
	public void testInterruptAProcessor() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/processing/");
		final MyProcessor processor = new MyProcessor();
		launcher.addProcessor(processor);
		try {
			launcher.run();
		} catch (ProcessInterruption e) {
			fail("ProcessInterrupt exception must be catch in the ProcessingManager.");
		}
		assertFalse(processor.isShouldStayAtFalse());
	}

	@Test
	public void testSpoonTagger() {
		final Launcher launcher = new Launcher();
		launcher.addProcessor("spoon.processing.SpoonTagger");
		launcher.run();
		assertTrue(new File(launcher.getModelBuilder().getSourceOutputDirectory() + "/spoon/Spoon.java").exists());
	}

	private static class SimpleProcessor extends AbstractProcessor<CtType<?>> {
		@Override
		public void process(CtType<?> element) {
			System.out.println(">> Hello: " + element.getSimpleName() + " <<");
		}
	}

	@Test
	public void testStaticImport() throws IOException {
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		String[] sourcePath = new String[0];
		e.setNoClasspath(false);
		e.setSourceClasspath(sourcePath);
		e.setAutoImports(true);
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(l.getEnvironment()));

		Path path = Files.createTempDirectory("emptydir");
		l.addInputResource("src/test/resources/compilation3/A.java");
		l.addInputResource("src/test/resources/compilation3/subpackage/B.java");
		l.setSourceOutputDirectory(path.toFile());
		SimpleProcessor simpleProcessor = new SimpleProcessor();
		l.addProcessor(simpleProcessor);
		l.run();
	}

	private static class SimpleProcessor2 extends AbstractProcessor<CtBlock<?>> {
		@Override
		public void process(CtBlock<?> element) {
			System.out.println(">> Hello: " + element.toStringDebug() + " <<");
		}
	}

	@Test
	public void testAllSlideMainSourceFiles() throws IOException {
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		e.setNoClasspath(true);
		e.setAutoImports(true);
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(l.getEnvironment()));

		Path path = Files.createTempDirectory("emptydir");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/ActionStates.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Album.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/AlbumPager.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Announcement.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/BaseActivity.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/BaseActivityAnim.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/CancelSubNotifs.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/CommentSearch.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/CommentsScreen.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/CommentsScreenSingle.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/CreateMulti.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Crosspost.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/DeleteFile.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Discover.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/DonateView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Draw.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/EditCardsLayout.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/ForceTouchLink.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/FullScreenActivity.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/FullscreenVideo.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Gallery.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Inbox.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/LiveThread.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Loader.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Login.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/MainActivity.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/MakeExternal.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/ManageOfflineContent.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/MediaView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/ModQueue.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/MultiredditOverview.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/NewsActivity.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/OpenContent.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/PostReadLater.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Profile.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/ReaderMode.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Reauthenticate.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Related.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Search.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SendMessage.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Settings.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsAbout.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsBackup.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsComments.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsData.java");
//		The SourcePosition of elements are not consistent (This one does not make the test fail, it should)
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsFilter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsFont.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsGeneral.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsHandling.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsHistory.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsLibs.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsModeration.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsReddit.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsSubreddit.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsSynccit.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsTheme.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SettingsViewType.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SetupWidget.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Shadowbox.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/ShadowboxComments.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Shortcut.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Slide.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Submit.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SubredditSearch.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SubredditView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/SwipeTutorial.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Tumblr.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/TumblrPager.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Tutorial.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Website.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Activities/Wiki.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/AlbumView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/BaseAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/CommentAdapter.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/CommentAdapterHelper.java");
// ERROR spoon.Launcher - Cannot compare this: [21641, 21655] with other: ["21640", "21648"]   (This one does not make the test fail, it should)
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/CommentAdapterSearch.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/CommentItem.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/CommentNavType.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/CommentObject.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/CommentUrlObject.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/CommentViewHolder.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ContributionAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ContributionPosts.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ContributionPostsSaved.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ErrorAdapter.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/GalleryView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/GeneralPosts.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/HistoryPosts.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ImageGridAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ImageGridAdapterTumblr.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/InboxAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/InboxMessages.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/MarkAsReadService.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/MessageViewHolder.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ModeratorAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ModeratorPosts.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ModLogAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ModLogPosts.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/MoreChildItem.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/MoreCommentViewHolder.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/MultiredditAdapter.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/MultiredditPosts.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/NewsViewHolder.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/OfflineSubAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/ProfileCommentViewHolder.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SettingsSubAdapter.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SideArrayAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SlideInAnimator.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubChooseAdapter.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubmissionAdapter.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubmissionComments.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubmissionDisplay.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubmissionNewsAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubmissionViewHolder.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubredditAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubredditListingAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubredditNames.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubredditPosts.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubredditPostsRealm.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubredditSearchPosts.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/SubredditViewHolder.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Adapters/TumblrView.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Authentication.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Autocache/AutoCacheScheduler.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Autocache/CacheAll.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/BuildConfig.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/CaseInsensitiveArrayList.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/CheckInstall.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ClickableText.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ColorPreferences.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/CommentCacheAsync.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Constants.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ContentGrabber.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ContentType.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/CustomMovementMethod.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/DataBackup.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/DataShare.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Drafts.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/DragSort/DragSortRecycler.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/DragSort/ReorderSubreddits.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/FDroid.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/builder/Peek.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/builder/PeekViewOptions.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/callback/OnButtonUp.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/callback/OnPeek.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/callback/OnPop.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/callback/OnRemove.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/callback/SimpleOnPeek.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/PeekView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/PeekViewActivity.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/util/DensityUtils.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/util/GestureDetectorCompat.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/util/GestureListener.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ForceTouch/util/NavigationUtils.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/AlbumFull.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/AlbumFullComments.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/BlankFragment.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/CommentPage.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/ContributionsView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/DrawerItemsDialog.java");
//      ERROR spoon.Launcher - The element of class class spoon.support.reflect.declaration.CtTypeParameterImpl does not have CtRole.BOUND
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/FolderChooserDialogCreate.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/HistoryView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/Image.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/InboxPage.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/ManageOfflineContentFragment.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/MediaFragment.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/MediaFragmentComment.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/ModLog.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/ModPage.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/MultiredditView.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/NewsView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/OnFlingGestureListener.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/ReadLaterView.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SelftextFull.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsCommentsFragment.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsDataFragment.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsFontFragment.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsFragment.java");
//		[main] ERROR spoon.Launcher - The element of class class spoon.support.reflect.declaration.CtTypeParameterImpl does not have CtRole.BOUND
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsGeneralFragment.java");
//	    [main] ERROR spoon.Launcher - The SourcePosition of elements are not consistent
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsHandlingFragment.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsHistoryFragment.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsModerationFragment.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsRedditFragment.java");
//		[main] ERROR spoon.Launcher - The element of class class spoon.support.reflect.declaration.CtTypeParameterImpl does not have CtRole.BOUND
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SettingsThemeFragment.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SubmissionsView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/SubredditListView.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/TitleFull.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/TopFragment.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/TumblrFull.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Fragments/WikiPage.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/handler/TextViewLinkHandler.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/handler/ToolbarScrollHideHandler.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/HasSeen.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Hidden.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/ImageFlairs.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ImageLoaderUnescape.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ImageLoaderUtils.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ImgurAlbum/AlbumImage.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ImgurAlbum/AlbumUtils.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ImgurAlbum/Data.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ImgurAlbum/Image.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ImgurAlbum/SingleAlbumImage.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ImgurAlbum/SingleImage.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/LastComments.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Notifications/CheckForMail.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Notifications/CheckForMailSingle.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Notifications/ImageDownloadNotificationService.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Notifications/NotificationJobScheduler.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Notifications/NotificationPiggyback.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Notifications/StartOnBoot.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/OfflineSubreddit.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/OpenRedditLink.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/PostLoader.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/PostMatch.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/R.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/ReadLater.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Reddit.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SantitizeField.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SecretConstants.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SettingValues.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/SpoilerRobotoTextView.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/StackRemoteViewsFactory.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/StackWidgetService.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/StartupStrings.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SubmissionCache.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/SubmissionViews/HeaderImageLinkView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SubmissionViews/OpenVRedditTask.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/SubmissionViews/PopulateNewsViewHolder.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/SubmissionViews/PopulateShadowboxInfo.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/SubmissionViews/PopulateSubmissionViewHolder.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SwipeLayout/app/SwipeBackActivity.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SwipeLayout/app/SwipeBackActivityBase.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SwipeLayout/app/SwipeBackActivityHelper.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SwipeLayout/SwipeBackLayout.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SwipeLayout/Utils.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/SwipeLayout/ViewDragHelper.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/http/HttpClientFactory.java");
//		[main] ERROR spoon.Launcher - The SourcePosition of elements are not consistent
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/http/HttpPostTask.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/MySynccitReadTask.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/MySynccitUpdateTask.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/SynccitRead.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/SynccitReadTask.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/SynccitResponse.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/SynccitTask.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Synccit/SynccitUpdateTask.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/TimeUtils.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Toolbox/RemovalReasons.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Toolbox/Toolbox.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Toolbox/ToolboxConfig.java");
//		[main] ERROR spoon.Launcher - The SourcePosition of elements are not consistent
		l.addInputResource("src/test/resources/compilation4/redditslide/Toolbox/ToolboxUI.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Toolbox/Usernote.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Toolbox/Usernotes.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/AltSize.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Blog.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Blog_.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Example.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Meta.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/OriginalSize.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Photo.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Post.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Post_.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Reblog.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Response.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Theme.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/Trail.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/TumblrPost.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Tumblr/TumblrUtils.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/UserSubscriptions.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/UserTags.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/util/AdBlocker.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/Base64.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/Base64DecoderException.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/CacheUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/CustomTabsHelper.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/EditTextValidator.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/FileUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/GifCache.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/GifDecoder.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/util/GifUtils.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/HttpUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/IabException.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/util/IabHelper.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/IabResult.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/ImageUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/Inventory.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/LinkUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/LogUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/NetworkStateReceiver.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/NetworkUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/OkHttpImageDownloader.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/OnSingleClickListener.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/Purchase.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/Security.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/ShareUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/SkuDetails.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/SoftKeyboardStateWatcher.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/util/SortingUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/SubmissionParser.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/TitleExtractor.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/TwitterObject.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/util/UpgradeUtil.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/AnimateHelper.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/AutoHideFAB.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/AutoMarkupTextView.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/CacheDataSourceFactory.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/CanvasView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/CatchStaggeredGridLayoutManager.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/CommentOverflow.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/CreateCardView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/CustomQuoteSpan.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/DoEditorActions.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/ExpandablePanel.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/FadeInAnimation.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/GeneralSwipeRefreshLayout.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/HTMLLinkExtractor.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/ImageDecoder.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/ImageInsertEditText.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/ImageSource.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/MaxHeightImageView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/MediaVideoView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/MediaVideoViewOld.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/NestedWebView.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/PeekMediaView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/PinchZoomVideoView.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/PopMediaView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/PreCachingLayoutManager.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/PreCachingLayoutManagerComments.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/RapidImageDecoder.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/RapidImageRegionDecoder.java");
//		ERROR spoon.Launcher - Cannot compare this: [20931, 20942] with other: ["20930", "20937"]
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/RedditItemView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/RevealRelativeLayout.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/RoundedBackgroundSpan.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/RoundImageTriangleView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/SidebarLayout.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/SlideVideoControls.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/SubsamplingScaleImageView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/TitleTextView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/ToastHelpCreation.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/ToggleSwipeViewPager.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/ToolbarColorizeHelper.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/TransparentTagTextView.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/VerticalViewPager.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Views/WebViewOverScrollDecoratorAdapter.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Visuals/FontPreferences.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Visuals/GetClosestColor.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Visuals/Palette.java");
// 		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Vote.java");
//		java.lang.NullPointerException
		l.addInputResource("src/test/resources/compilation4/redditslide/Widget/ListViewRemoteViewsFactory.java");
		l.addInputResource("src/test/resources/compilation4/redditslide/Widget/ListViewWidgetService.java");
//		PARENT NOT INITIALIZED
		l.addInputResource("src/test/resources/compilation4/redditslide/Widget/SubredditWidgetProvider.java");
		l.setSourceOutputDirectory(path.toFile());
		SimpleProcessor2 simpleProcessor = new SimpleProcessor2();
		l.addProcessor(simpleProcessor);
		l.run();
	}

}
