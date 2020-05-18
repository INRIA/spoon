package spoon.smpl;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.smpl.*;
import static spoon.smpl.TestUtils.*;

public class EndToEndTests {
    @Test
    public void testAppendContextBranch() {
        // contract: a patch should be able to append elements below a context branch

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        if (true) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "        int y = 1;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "  if (true) {\n" +
                                         "      int x = 0;\n" +
                                         "  }\n" +
                                         "+ int y = 1;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testAppendToContext() {
        // contract: a patch should be able to append elements to a context statement

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        int x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        int y;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int x;\n" +
                                                  "        int appended1;\n" +
                                                  "        int appended2;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        int y;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "  int x;\n" +
                                         "+ int appended1;\n" +
                                         "+ int appended2;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testBasicDots() {
        // contract: dots are able to match any number of arbitrary paths

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int sgn(int input) {\n" +
                                               "        int x;\n" +
                                               "        \n" +
                                               "        if (input > 0) {\n" +
                                               "            x = 1;\n" +
                                               "        } else if (input == 0) {\n" +
                                               "            x = 0;\n" +
                                               "        } else {\n" +
                                               "            x = 2;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int sgn(int input) {\n" +
                                                  "        int x;\n" +
                                                  "        \n" +
                                                  "        if (input > 0) {\n" +
                                                  "            x = 1 + 1;\n" +
                                                  "        } else if (input == 0) {\n" +
                                                  "            x = 0 + 1;\n" +
                                                  "        } else {\n" +
                                                  "            x = 2 + 1;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        return x;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "  int v1;\n" +
                                         "  ...\n" +
                                         "- v1 = C;\n" +
                                         "+ v1 = C + 1;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testBasicPatternDisjunction() {
        // contract: matching of pattern disjunction including clause-order priority

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void a() {}\n" +
                                               "    void b() {}\n" +
                                               "    void c() {}\n" +
                                               "    \n" +
                                               "    void m1() {\n" +
                                               "        a();\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        b();\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m3() {\n" +
                                               "        c();\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m4() {\n" +
                                               "        a();\n" +
                                               "        b();\n" +
                                               "        c();\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m5() {\n" +
                                               "        c();\n" +
                                               "        b();\n" +
                                               "        a();\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void a() {}\n" +
                                                  "    void b() {}\n" +
                                                  "    void c() {}\n" +
                                                  "    \n" +
                                                  "    void m1() {\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m3() {\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m4() {\n" +
                                                  "        b();\n" +
                                                  "        c();\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m5() {\n" +
                                                  "        c();\n" +
                                                  "        b();\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "(\n" +
                                         "- a();\n" +
                                         "|\n" +
                                         "- b();\n" +
                                         "|\n" +
                                         "- c();\n" +
                                         ")\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testSendStickyBroadcast() {
        // contract: correct application of the \"sticky_broadcasts\" patch from the c4j paper

        CtClass<?> input = Launcher.parseClass("public class VariousMethods {\n" +
                                               "    void dontTouchThese() {\n" +
                                               "        int x = 0;\n" +
                                               "        sendStickyBroadcast(x);\n" +
                                               "        removeStickyBroadcast(x);\n" +
                                               "    }\n" +
                                               "    private void sendBroadcastUploadsAdded() {\n" +
                                               "        Intent start = new Intent(getUploadsAddedMessage());\n" +
                                               "        start.setPackage(getPackageName());\n" +
                                               "        sendStickyBroadcast(start);\n" +
                                               "    }\n" +
                                               "    private void sendBroadcastUploadStarted(UploadFileOperation upload) {\n" +
                                               "        Intent start = new Intent(getUploadStartMessage());\n" +
                                               "        start.putExtra(EXTRA_REMOTE_PATH, upload.getRemotePath());\n" +
                                               "        start.putExtra(EXTRA_OLD_FILE_PATH, upload.getOriginalStoragePath());\n" +
                                               "        start.putExtra(ACCOUNT_NAME, upload.getAccount().name);\n" +
                                               "        start.setPackage(getPackageName());\n" +
                                               "        sendStickyBroadcast(start);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    private void sendBroadcastUploadFinished(\n" +
                                               "            UploadFileOperation upload,\n" +
                                               "            RemoteOperationResult uploadResult,\n" +
                                               "            String unlinkedFromRemotePath) {\n" +
                                               "        Intent end = new Intent(getUploadFinishMessage());\n" +
                                               "        end.putExtra(EXTRA_REMOTE_PATH, upload.getRemotePath());\n" +
                                               "        if (upload.wasRenamed()) {\n" +
                                               "            end.putExtra(EXTRA_OLD_REMOTE_PATH, upload.getOldFile().getRemotePath());\n" +
                                               "        }\n" +
                                               "        end.putExtra(EXTRA_OLD_FILE_PATH, upload.getOriginalStoragePath());\n" +
                                               "        end.putExtra(ACCOUNT_NAME, upload.getAccount().name);\n" +
                                               "        end.putExtra(EXTRA_UPLOAD_RESULT, uploadResult.isSuccess());\n" +
                                               "        if (unlinkedFromRemotePath != null) {\n" +
                                               "            end.putExtra(EXTRA_LINKED_TO_PATH, unlinkedFromRemotePath);\n" +
                                               "        }\n" +
                                               "        end.setPackage(getPackageName());\n" +
                                               "        sendStickyBroadcast(end);\n" +
                                               "    }\n" +
                                               "    private void sendBroadcastDownloadFinished(\n" +
                                               "            DownloadFileOperation download,\n" +
                                               "            RemoteOperationResult downloadResult,\n" +
                                               "            String unlinkedFromRemotePath) {\n" +
                                               "        Intent end = new Intent(getDownloadFinishMessage());\n" +
                                               "        end.putExtra(EXTRA_DOWNLOAD_RESULT, downloadResult.isSuccess());\n" +
                                               "        end.putExtra(ACCOUNT_NAME, download.getAccount().name);\n" +
                                               "        end.putExtra(EXTRA_REMOTE_PATH, download.getRemotePath());\n" +
                                               "        end.putExtra(EXTRA_FILE_PATH, download.getSavePath());\n" +
                                               "        end.putExtra(OCFileListFragment.DOWNLOAD_BEHAVIOUR, download.getBehaviour());\n" +
                                               "        end.putExtra(SendShareDialog.ACTIVITY_NAME, download.getActivityName());\n" +
                                               "        end.putExtra(SendShareDialog.PACKAGE_NAME, download.getPackageName());\n" +
                                               "        if (unlinkedFromRemotePath != null) {\n" +
                                               "            end.putExtra(EXTRA_LINKED_TO_PATH, unlinkedFromRemotePath);\n" +
                                               "        }\n" +
                                               "        end.setPackage(getPackageName());\n" +
                                               "        sendStickyBroadcast(end);\n" +
                                               "    }\n" +
                                               "    private void sendBroadcastNewDownload(DownloadFileOperation download,\n" +
                                               "                                          String linkedToRemotePath) {\n" +
                                               "        Intent added = new Intent(getDownloadAddedMessage());\n" +
                                               "        added.putExtra(ACCOUNT_NAME, download.getAccount().name);\n" +
                                               "        added.putExtra(EXTRA_REMOTE_PATH, download.getRemotePath());\n" +
                                               "        added.putExtra(EXTRA_FILE_PATH, download.getSavePath());\n" +
                                               "        added.putExtra(EXTRA_LINKED_TO_PATH, linkedToRemotePath);\n" +
                                               "        added.setPackage(getPackageName());\n" +
                                               "        sendStickyBroadcast(added);\n" +
                                               "    }\n" +
                                               "    public void onReceive1(Context context, Intent intent) {\n" +
                                               "        String accountName = intent.getStringExtra(FileDownloader.ACCOUNT_NAME);\n" +
                                               "        String downloadedRemotePath = intent.getStringExtra(FileDownloader.EXTRA_REMOTE_PATH);\n" +
                                               "        if (getAccount().name.equals(accountName) && \n" +
                                               "                downloadedRemotePath != null) {\n" +
                                               "            OCFile file = getStorageManager().getFileByPath(downloadedRemotePath);\n" +
                                               "            int position = mPreviewImagePagerAdapter.getFilePosition(file);\n" +
                                               "            boolean downloadWasFine = intent.getBooleanExtra(\n" +
                                               "                    FileDownloader.EXTRA_DOWNLOAD_RESULT, false);\n" +
                                               "            \n" +
                                               "            if (position >= 0 &&\n" +
                                               "                    intent.getAction().equals(FileDownloader.getDownloadFinishMessage())) {\n" +
                                               "                if (downloadWasFine) {\n" +
                                               "                    mPreviewImagePagerAdapter.updateFile(position, file);   \n" +
                                               "                    \n" +
                                               "                } else {\n" +
                                               "                    mPreviewImagePagerAdapter.updateWithDownloadError(position);\n" +
                                               "                }\n" +
                                               "                mPreviewImagePagerAdapter.notifyDataSetChanged();\n" +
                                               "            } else {\n" +
                                               "                Log_OC.d(TAG, \"Download finished, but the fragment is offscreen\");\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "        removeStickyBroadcast(intent);\n" +
                                               "    }\n" +
                                               "    /*public void onReceive2(Context context, Intent intent) {\n" +
                                               "        try {\n" +
                                               "            String event = intent.getAction();\n" +
                                               "            Log_OC.d(TAG, \"Received broadcast \" + event);\n" +
                                               "            String accountName = intent.getStringExtra(FileSyncAdapter.EXTRA_ACCOUNT_NAME);\n" +
                                               "            String synchFolderRemotePath =\n" +
                                               "                    intent.getStringExtra(FileSyncAdapter.EXTRA_FOLDER_PATH);\n" +
                                               "            RemoteOperationResult synchResult = (RemoteOperationResult)\n" +
                                               "                    DataHolderUtil.getInstance().retrieve(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                               "            boolean sameAccount = getAccount() != null &&\n" +
                                               "                    accountName.equals(getAccount().name) && getStorageManager() != null;\n" +
                                               "            if (sameAccount) {\n" +
                                               "                if (FileSyncAdapter.EVENT_FULL_SYNC_START.equals(event)) {\n" +
                                               "                    mSyncInProgress = true;\n" +
                                               "                } else {\n" +
                                               "                    OCFile currentFile = (getFile() == null) ? null :\n" +
                                               "                            getStorageManager().getFileByPath(getFile().getRemotePath());\n" +
                                               "                    OCFile currentDir = (getCurrentDir() == null) ? null :\n" +
                                               "                            getStorageManager().getFileByPath(getCurrentDir().getRemotePath());\n" +
                                               "                    if (currentDir == null) {\n" +
                                               "                        DisplayUtils.showSnackMessage(\n" +
                                               "                                getActivity(),\n" +
                                               "                                R.string.sync_current_folder_was_removed,\n" +
                                               "                                synchFolderRemotePath\n" +
                                               "                        );\n" +
                                               "                        browseToRoot();\n" +
                                               "                    } else {\n" +
                                               "                        if (currentFile == null && !getFile().isFolder()) {\n" +
                                               "                            cleanSecondFragment();\n" +
                                               "                            currentFile = currentDir;\n" +
                                               "                        }\n" +
                                               "                        if (currentDir.getRemotePath().equals(synchFolderRemotePath)) {\n" +
                                               "                            OCFileListFragment fileListFragment = getListOfFilesFragment();\n" +
                                               "                            if (fileListFragment != null) {\n" +
                                               "                                fileListFragment.listDirectory(currentDir, MainApp.isOnlyOnDevice(), false);\n" +
                                               "                            }\n" +
                                               "                        }\n" +
                                               "                        setFile(currentFile);\n" +
                                               "                    }\n" +
                                               "                    mSyncInProgress = !FileSyncAdapter.EVENT_FULL_SYNC_END.equals(event) &&\n" +
                                               "                            !RefreshFolderOperation.EVENT_SINGLE_FOLDER_SHARES_SYNCED.equals(event);\n" +
                                               "                    if (RefreshFolderOperation.EVENT_SINGLE_FOLDER_CONTENTS_SYNCED.equals(event) &&\n" +
                                               "                            synchResult != null) {\n" +
                                               "                        if (synchResult.isSuccess()) {\n" +
                                               "                            hideInfoBox();\n" +
                                               "                        } else {\n" +
                                               "                            if (checkForRemoteOperationError(synchResult)) {\n" +
                                               "                                requestCredentialsUpdate(context);\n" +
                                               "                            } else if (RemoteOperationResult.ResultCode.SSL_RECOVERABLE_PEER_UNVERIFIED.equals(\n" +
                                               "                                    synchResult.getCode())) {\n" +
                                               "                                showUntrustedCertDialog(synchResult);\n" +
                                               "                            } else if (ResultCode.MAINTENANCE_MODE.equals(synchResult.getCode())) {\n" +
                                               "                                showInfoBox(R.string.maintenance_mode);\n" +
                                               "                            } else if (ResultCode.NO_NETWORK_CONNECTION.equals(synchResult.getCode()) ||\n" +
                                               "                                    ResultCode.HOST_NOT_AVAILABLE.equals(synchResult.getCode())) {\n" +
                                               "                                showInfoBox(R.string.offline_mode);\n" +
                                               "                            }\n" +
                                               "                        }\n" +
                                               "                    }\n" +
                                               "                    removeStickyBroadcast(intent);\n" +
                                               "                    DataHolderUtil.getInstance().delete(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                               "                    Log_OC.d(TAG, \"Setting progress visibility to \" + mSyncInProgress);\n" +
                                               "                    setIndeterminate(mSyncInProgress);\n" +
                                               "                    setBackgroundText();\n" +
                                               "                }\n" +
                                               "            }\n" +
                                               "            if (synchResult != null && synchResult.getCode().equals(\n" +
                                               "                    RemoteOperationResult.ResultCode.SSL_RECOVERABLE_PEER_UNVERIFIED)) {\n" +
                                               "                mLastSslUntrustedServerResult = synchResult;\n" +
                                               "            }\n" +
                                               "        } catch (RuntimeException e) {\n" +
                                               "            removeStickyBroadcast(intent);\n" +
                                               "            try {\n" +
                                               "                DataHolderUtil.getInstance().delete(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                               "            } catch (RuntimeException re) {\n" +
                                               "                Log_OC.i(TAG, \"Ignoring error deleting data\");\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    public void onReceive3(Context context, Intent intent) {\n" +
                                               "        try {\n" +
                                               "            String uploadedRemotePath = intent.getStringExtra(FileUploader.EXTRA_REMOTE_PATH);\n" +
                                               "            String accountName = intent.getStringExtra(FileUploader.ACCOUNT_NAME);\n" +
                                               "            boolean sameAccount = getAccount() != null && accountName.equals(getAccount().name);\n" +
                                               "            OCFile currentDir = getCurrentDir();\n" +
                                               "            boolean isDescendant = currentDir != null && uploadedRemotePath != null &&\n" +
                                               "                    uploadedRemotePath.startsWith(currentDir.getRemotePath());\n" +
                                               "            if (sameAccount && isDescendant) {\n" +
                                               "                String linkedToRemotePath =\n" +
                                               "                        intent.getStringExtra(FileUploader.EXTRA_LINKED_TO_PATH);\n" +
                                               "                if (linkedToRemotePath == null || isAscendant(linkedToRemotePath)) {\n" +
                                               "                    refreshListOfFilesFragment(false);\n" +
                                               "                }\n" +
                                               "            }\n" +
                                               "            boolean uploadWasFine = intent.getBooleanExtra(\n" +
                                               "                    FileUploader.EXTRA_UPLOAD_RESULT,\n" +
                                               "                    false);\n" +
                                               "            boolean renamedInUpload = getFile().getRemotePath().\n" +
                                               "                    equals(intent.getStringExtra(FileUploader.EXTRA_OLD_REMOTE_PATH));\n" +
                                               "            boolean sameFile = getFile().getRemotePath().equals(uploadedRemotePath) ||\n" +
                                               "                    renamedInUpload;\n" +
                                               "            FileFragment details = getSecondFragment();\n" +
                                               "            if (sameAccount && sameFile && details instanceof FileDetailFragment) {\n" +
                                               "                if (uploadWasFine) {\n" +
                                               "                    setFile(getStorageManager().getFileByPath(uploadedRemotePath));\n" +
                                               "                } else {\n" +
                                               "                    Log_OC.d(TAG, \"Remove upload progress bar after upload failed\");\n" +
                                               "                }\n" +
                                               "                if (renamedInUpload) {\n" +
                                               "                    String newName = new File(uploadedRemotePath).getName();\n" +
                                               "                    DisplayUtils.showSnackMessage(\n" +
                                               "                            getActivity(),\n" +
                                               "                            R.string.filedetails_renamed_in_upload_msg,\n" +
                                               "                            newName\n" +
                                               "                    );\n" +
                                               "                }\n" +
                                               "                if (uploadWasFine || getFile().fileExists()) {\n" +
                                               "                    ((FileDetailFragment) details).updateFileDetails(false, true);\n" +
                                               "                } else {\n" +
                                               "                    cleanSecondFragment();\n" +
                                               "                }\n" +
                                               "                if (uploadWasFine) {\n" +
                                               "                    OCFile ocFile = getFile();\n" +
                                               "                    if (PreviewImageFragment.canBePreviewed(ocFile)) {\n" +
                                               "                        startImagePreview(getFile(), true);\n" +
                                               "                    } else if (PreviewTextFragment.canBePreviewed(ocFile)) {\n" +
                                               "                        startTextPreview(ocFile, true);\n" +
                                               "                    }\n" +
                                               "                }\n" +
                                               "            }\n" +
                                               "            setIndeterminate(false);\n" +
                                               "        } finally {\n" +
                                               "            if (intent != null) {\n" +
                                               "                removeStickyBroadcast(intent);\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    public void onReceive4(Context context, Intent intent) {\n" +
                                               "        try {\n" +
                                               "            String event = intent.getAction();\n" +
                                               "            Log_OC.d(TAG, \"Received broadcast \" + event);\n" +
                                               "            String accountName = intent.getStringExtra(FileSyncAdapter.EXTRA_ACCOUNT_NAME);\n" +
                                               "            String syncFolderRemotePath = intent.getStringExtra(FileSyncAdapter.EXTRA_FOLDER_PATH);\n" +
                                               "            RemoteOperationResult syncResult = (RemoteOperationResult)\n" +
                                               "                    DataHolderUtil.getInstance().retrieve(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                               "            boolean sameAccount = getAccount() != null && accountName.equals(getAccount().name)\n" +
                                               "                    && getStorageManager() != null;\n" +
                                               "            if (sameAccount) {\n" +
                                               "                if (FileSyncAdapter.EVENT_FULL_SYNC_START.equals(event)) {\n" +
                                               "                    mSyncInProgress = true;\n" +
                                               "                } else {\n" +
                                               "                    OCFile currentFile = (getFile() == null) ? null :\n" +
                                               "                            getStorageManager().getFileByPath(getFile().getRemotePath());\n" +
                                               "                    OCFile currentDir = (getCurrentFolder() == null) ? null : \n" +
                                               "                        getStorageManager().getFileByPath(getCurrentFolder().getRemotePath());\n" +
                                               "                    if (currentDir == null) {\n" +
                                               "                        DisplayUtils.showSnackMessage(getActivity(), R.string.sync_current_folder_was_removed,\n" +
                                               "                                getCurrentFolder().getFileName());\n" +
                                               "                        browseToRoot();\n" +
                                               "                    } else {\n" +
                                               "                        if (currentFile == null && !getFile().isFolder()) {\n" +
                                               "                            currentFile = currentDir;\n" +
                                               "                        }\n" +
                                               "                        if (currentDir.getRemotePath().equals(syncFolderRemotePath)) {\n" +
                                               "                            OCFileListFragment fileListFragment = getListOfFilesFragment();\n" +
                                               "                            if (fileListFragment != null) {\n" +
                                               "                                fileListFragment.listDirectory(currentDir, false, false);\n" +
                                               "                            }\n" +
                                               "                        }\n" +
                                               "                        setFile(currentFile);\n" +
                                               "                    }\n" +
                                               "                    \n" +
                                               "                    mSyncInProgress = (!FileSyncAdapter.EVENT_FULL_SYNC_END.equals(event) && \n" +
                                               "                            !RefreshFolderOperation.EVENT_SINGLE_FOLDER_SHARES_SYNCED.equals(event));\n" +
                                               "                    if (RefreshFolderOperation.EVENT_SINGLE_FOLDER_CONTENTS_SYNCED.equals(event) &&\n" +
                                               "                            syncResult != null && !syncResult.isSuccess()) {\n" +
                                               "                        if (ResultCode.UNAUTHORIZED.equals(syncResult.getCode()) || (syncResult.isException()\n" +
                                               "                                && syncResult.getException() instanceof AuthenticatorException)) {\n" +
                                               "                            requestCredentialsUpdate(context);\n" +
                                               "                        } else if (RemoteOperationResult.ResultCode.SSL_RECOVERABLE_PEER_UNVERIFIED\n" +
                                               "                                .equals(syncResult.getCode())) {\n" +
                                               "                            showUntrustedCertDialog(syncResult);\n" +
                                               "                        }\n" +
                                               "                    }\n" +
                                               "                }\n" +
                                               "                removeStickyBroadcast(intent);\n" +
                                               "                DataHolderUtil.getInstance().delete(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                               "                Log_OC.d(TAG, \"Setting progress visibility to \" + mSyncInProgress);\n" +
                                               "                setIndeterminate(mSyncInProgress);\n" +
                                               "                setBackgroundText();\n" +
                                               "            }\n" +
                                               "            \n" +
                                               "        } catch (RuntimeException e) {\n" +
                                               "            removeStickyBroadcast(intent);\n" +
                                               "            DataHolderUtil.getInstance().delete(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                               "        }\n" +
                                               "    }*/\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("public class VariousMethods {\n" +
                                                  "    void dontTouchThese() {\n" +
                                                  "        int x = 0;\n" +
                                                  "        sendStickyBroadcast(x);\n" +
                                                  "        removeStickyBroadcast(x);\n" +
                                                  "    }\n" +
                                                  "    private void sendBroadcastUploadsAdded() {\n" +
                                                  "        Intent start = new Intent(getUploadsAddedMessage());\n" +
                                                  "        start.setPackage(getPackageName());\n" +
                                                  "        sendBroadcast(start);\n" +
                                                  "    }\n" +
                                                  "    private void sendBroadcastUploadStarted(UploadFileOperation upload) {\n" +
                                                  "        Intent start = new Intent(getUploadStartMessage());\n" +
                                                  "        start.putExtra(EXTRA_REMOTE_PATH, upload.getRemotePath());\n" +
                                                  "        start.putExtra(EXTRA_OLD_FILE_PATH, upload.getOriginalStoragePath());\n" +
                                                  "        start.putExtra(ACCOUNT_NAME, upload.getAccount().name);\n" +
                                                  "        start.setPackage(getPackageName());\n" +
                                                  "        sendBroadcast(start);\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    private void sendBroadcastUploadFinished(\n" +
                                                  "            UploadFileOperation upload,\n" +
                                                  "            RemoteOperationResult uploadResult,\n" +
                                                  "            String unlinkedFromRemotePath) {\n" +
                                                  "        Intent end = new Intent(getUploadFinishMessage());\n" +
                                                  "        end.putExtra(EXTRA_REMOTE_PATH, upload.getRemotePath());\n" +
                                                  "        if (upload.wasRenamed()) {\n" +
                                                  "            end.putExtra(EXTRA_OLD_REMOTE_PATH, upload.getOldFile().getRemotePath());\n" +
                                                  "        }\n" +
                                                  "        end.putExtra(EXTRA_OLD_FILE_PATH, upload.getOriginalStoragePath());\n" +
                                                  "        end.putExtra(ACCOUNT_NAME, upload.getAccount().name);\n" +
                                                  "        end.putExtra(EXTRA_UPLOAD_RESULT, uploadResult.isSuccess());\n" +
                                                  "        if (unlinkedFromRemotePath != null) {\n" +
                                                  "            end.putExtra(EXTRA_LINKED_TO_PATH, unlinkedFromRemotePath);\n" +
                                                  "        }\n" +
                                                  "        end.setPackage(getPackageName());\n" +
                                                  "        sendBroadcast(end);\n" +
                                                  "    }\n" +
                                                  "    private void sendBroadcastDownloadFinished(\n" +
                                                  "            DownloadFileOperation download,\n" +
                                                  "            RemoteOperationResult downloadResult,\n" +
                                                  "            String unlinkedFromRemotePath) {\n" +
                                                  "        Intent end = new Intent(getDownloadFinishMessage());\n" +
                                                  "        end.putExtra(EXTRA_DOWNLOAD_RESULT, downloadResult.isSuccess());\n" +
                                                  "        end.putExtra(ACCOUNT_NAME, download.getAccount().name);\n" +
                                                  "        end.putExtra(EXTRA_REMOTE_PATH, download.getRemotePath());\n" +
                                                  "        end.putExtra(EXTRA_FILE_PATH, download.getSavePath());\n" +
                                                  "        end.putExtra(OCFileListFragment.DOWNLOAD_BEHAVIOUR, download.getBehaviour());\n" +
                                                  "        end.putExtra(SendShareDialog.ACTIVITY_NAME, download.getActivityName());\n" +
                                                  "        end.putExtra(SendShareDialog.PACKAGE_NAME, download.getPackageName());\n" +
                                                  "        if (unlinkedFromRemotePath != null) {\n" +
                                                  "            end.putExtra(EXTRA_LINKED_TO_PATH, unlinkedFromRemotePath);\n" +
                                                  "        }\n" +
                                                  "        end.setPackage(getPackageName());\n" +
                                                  "        sendBroadcast(end);\n" +
                                                  "    }\n" +
                                                  "    private void sendBroadcastNewDownload(DownloadFileOperation download,\n" +
                                                  "                                          String linkedToRemotePath) {\n" +
                                                  "        Intent added = new Intent(getDownloadAddedMessage());\n" +
                                                  "        added.putExtra(ACCOUNT_NAME, download.getAccount().name);\n" +
                                                  "        added.putExtra(EXTRA_REMOTE_PATH, download.getRemotePath());\n" +
                                                  "        added.putExtra(EXTRA_FILE_PATH, download.getSavePath());\n" +
                                                  "        added.putExtra(EXTRA_LINKED_TO_PATH, linkedToRemotePath);\n" +
                                                  "        added.setPackage(getPackageName());\n" +
                                                  "        sendBroadcast(added);\n" +
                                                  "    }\n" +
                                                  "    public void onReceive1(Context context, Intent intent) {\n" +
                                                  "        String accountName = intent.getStringExtra(FileDownloader.ACCOUNT_NAME);\n" +
                                                  "        String downloadedRemotePath = intent.getStringExtra(FileDownloader.EXTRA_REMOTE_PATH);\n" +
                                                  "        if (getAccount().name.equals(accountName) && \n" +
                                                  "                downloadedRemotePath != null) {\n" +
                                                  "            OCFile file = getStorageManager().getFileByPath(downloadedRemotePath);\n" +
                                                  "            int position = mPreviewImagePagerAdapter.getFilePosition(file);\n" +
                                                  "            boolean downloadWasFine = intent.getBooleanExtra(\n" +
                                                  "                    FileDownloader.EXTRA_DOWNLOAD_RESULT, false);\n" +
                                                  "            \n" +
                                                  "            if (position >= 0 &&\n" +
                                                  "                    intent.getAction().equals(FileDownloader.getDownloadFinishMessage())) {\n" +
                                                  "                if (downloadWasFine) {\n" +
                                                  "                    mPreviewImagePagerAdapter.updateFile(position, file);   \n" +
                                                  "                    \n" +
                                                  "                } else {\n" +
                                                  "                    mPreviewImagePagerAdapter.updateWithDownloadError(position);\n" +
                                                  "                }\n" +
                                                  "                mPreviewImagePagerAdapter.notifyDataSetChanged();\n" +
                                                  "            } else {\n" +
                                                  "                Log_OC.d(TAG, \"Download finished, but the fragment is offscreen\");\n" +
                                                  "            }\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    /*public void onReceive2(Context context, Intent intent) {\n" +
                                                  "        try {\n" +
                                                  "            String event = intent.getAction();\n" +
                                                  "            Log_OC.d(TAG, \"Received broadcast \" + event);\n" +
                                                  "            String accountName = intent.getStringExtra(FileSyncAdapter.EXTRA_ACCOUNT_NAME);\n" +
                                                  "            String synchFolderRemotePath =\n" +
                                                  "                    intent.getStringExtra(FileSyncAdapter.EXTRA_FOLDER_PATH);\n" +
                                                  "            RemoteOperationResult synchResult = (RemoteOperationResult)\n" +
                                                  "                    DataHolderUtil.getInstance().retrieve(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                                  "            boolean sameAccount = getAccount() != null &&\n" +
                                                  "                    accountName.equals(getAccount().name) && getStorageManager() != null;\n" +
                                                  "            if (sameAccount) {\n" +
                                                  "                if (FileSyncAdapter.EVENT_FULL_SYNC_START.equals(event)) {\n" +
                                                  "                    mSyncInProgress = true;\n" +
                                                  "                } else {\n" +
                                                  "                    OCFile currentFile = (getFile() == null) ? null :\n" +
                                                  "                            getStorageManager().getFileByPath(getFile().getRemotePath());\n" +
                                                  "                    OCFile currentDir = (getCurrentDir() == null) ? null :\n" +
                                                  "                            getStorageManager().getFileByPath(getCurrentDir().getRemotePath());\n" +
                                                  "                    if (currentDir == null) {\n" +
                                                  "                        DisplayUtils.showSnackMessage(\n" +
                                                  "                                getActivity(),\n" +
                                                  "                                R.string.sync_current_folder_was_removed,\n" +
                                                  "                                synchFolderRemotePath\n" +
                                                  "                        );\n" +
                                                  "                        browseToRoot();\n" +
                                                  "                    } else {\n" +
                                                  "                        if (currentFile == null && !getFile().isFolder()) {\n" +
                                                  "                            cleanSecondFragment();\n" +
                                                  "                            currentFile = currentDir;\n" +
                                                  "                        }\n" +
                                                  "                        if (currentDir.getRemotePath().equals(synchFolderRemotePath)) {\n" +
                                                  "                            OCFileListFragment fileListFragment = getListOfFilesFragment();\n" +
                                                  "                            if (fileListFragment != null) {\n" +
                                                  "                                fileListFragment.listDirectory(currentDir, MainApp.isOnlyOnDevice(), false);\n" +
                                                  "                            }\n" +
                                                  "                        }\n" +
                                                  "                        setFile(currentFile);\n" +
                                                  "                    }\n" +
                                                  "                    mSyncInProgress = !FileSyncAdapter.EVENT_FULL_SYNC_END.equals(event) &&\n" +
                                                  "                            !RefreshFolderOperation.EVENT_SINGLE_FOLDER_SHARES_SYNCED.equals(event);\n" +
                                                  "                    if (RefreshFolderOperation.EVENT_SINGLE_FOLDER_CONTENTS_SYNCED.equals(event) &&\n" +
                                                  "                            synchResult != null) {\n" +
                                                  "                        if (synchResult.isSuccess()) {\n" +
                                                  "                            hideInfoBox();\n" +
                                                  "                        } else {\n" +
                                                  "                            if (checkForRemoteOperationError(synchResult)) {\n" +
                                                  "                                requestCredentialsUpdate(context);\n" +
                                                  "                            } else if (RemoteOperationResult.ResultCode.SSL_RECOVERABLE_PEER_UNVERIFIED.equals(\n" +
                                                  "                                    synchResult.getCode())) {\n" +
                                                  "                                showUntrustedCertDialog(synchResult);\n" +
                                                  "                            } else if (ResultCode.MAINTENANCE_MODE.equals(synchResult.getCode())) {\n" +
                                                  "                                showInfoBox(R.string.maintenance_mode);\n" +
                                                  "                            } else if (ResultCode.NO_NETWORK_CONNECTION.equals(synchResult.getCode()) ||\n" +
                                                  "                                    ResultCode.HOST_NOT_AVAILABLE.equals(synchResult.getCode())) {\n" +
                                                  "                                showInfoBox(R.string.offline_mode);\n" +
                                                  "                            }\n" +
                                                  "                        }\n" +
                                                  "                    }\n" +
                                                  "                    removeStickyBroadcast(intent);\n" +
                                                  "                    DataHolderUtil.getInstance().delete(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                                  "                    Log_OC.d(TAG, \"Setting progress visibility to \" + mSyncInProgress);\n" +
                                                  "                    setIndeterminate(mSyncInProgress);\n" +
                                                  "                    setBackgroundText();\n" +
                                                  "                }\n" +
                                                  "            }\n" +
                                                  "            if (synchResult != null && synchResult.getCode().equals(\n" +
                                                  "                    RemoteOperationResult.ResultCode.SSL_RECOVERABLE_PEER_UNVERIFIED)) {\n" +
                                                  "                mLastSslUntrustedServerResult = synchResult;\n" +
                                                  "            }\n" +
                                                  "        } catch (RuntimeException e) {\n" +
                                                  "            removeStickyBroadcast(intent);\n" +
                                                  "            try {\n" +
                                                  "                DataHolderUtil.getInstance().delete(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                                  "            } catch (RuntimeException re) {\n" +
                                                  "                Log_OC.i(TAG, \"Ignoring error deleting data\");\n" +
                                                  "            }\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    public void onReceive3(Context context, Intent intent) {\n" +
                                                  "        try {\n" +
                                                  "            String uploadedRemotePath = intent.getStringExtra(FileUploader.EXTRA_REMOTE_PATH);\n" +
                                                  "            String accountName = intent.getStringExtra(FileUploader.ACCOUNT_NAME);\n" +
                                                  "            boolean sameAccount = getAccount() != null && accountName.equals(getAccount().name);\n" +
                                                  "            OCFile currentDir = getCurrentDir();\n" +
                                                  "            boolean isDescendant = currentDir != null && uploadedRemotePath != null &&\n" +
                                                  "                    uploadedRemotePath.startsWith(currentDir.getRemotePath());\n" +
                                                  "            if (sameAccount && isDescendant) {\n" +
                                                  "                String linkedToRemotePath =\n" +
                                                  "                        intent.getStringExtra(FileUploader.EXTRA_LINKED_TO_PATH);\n" +
                                                  "                if (linkedToRemotePath == null || isAscendant(linkedToRemotePath)) {\n" +
                                                  "                    refreshListOfFilesFragment(false);\n" +
                                                  "                }\n" +
                                                  "            }\n" +
                                                  "            boolean uploadWasFine = intent.getBooleanExtra(\n" +
                                                  "                    FileUploader.EXTRA_UPLOAD_RESULT,\n" +
                                                  "                    false);\n" +
                                                  "            boolean renamedInUpload = getFile().getRemotePath().\n" +
                                                  "                    equals(intent.getStringExtra(FileUploader.EXTRA_OLD_REMOTE_PATH));\n" +
                                                  "            boolean sameFile = getFile().getRemotePath().equals(uploadedRemotePath) ||\n" +
                                                  "                    renamedInUpload;\n" +
                                                  "            FileFragment details = getSecondFragment();\n" +
                                                  "            if (sameAccount && sameFile && details instanceof FileDetailFragment) {\n" +
                                                  "                if (uploadWasFine) {\n" +
                                                  "                    setFile(getStorageManager().getFileByPath(uploadedRemotePath));\n" +
                                                  "                } else {\n" +
                                                  "                    Log_OC.d(TAG, \"Remove upload progress bar after upload failed\");\n" +
                                                  "                }\n" +
                                                  "                if (renamedInUpload) {\n" +
                                                  "                    String newName = new File(uploadedRemotePath).getName();\n" +
                                                  "                    DisplayUtils.showSnackMessage(\n" +
                                                  "                            getActivity(),\n" +
                                                  "                            R.string.filedetails_renamed_in_upload_msg,\n" +
                                                  "                            newName\n" +
                                                  "                    );\n" +
                                                  "                }\n" +
                                                  "                if (uploadWasFine || getFile().fileExists()) {\n" +
                                                  "                    ((FileDetailFragment) details).updateFileDetails(false, true);\n" +
                                                  "                } else {\n" +
                                                  "                    cleanSecondFragment();\n" +
                                                  "                }\n" +
                                                  "                if (uploadWasFine) {\n" +
                                                  "                    OCFile ocFile = getFile();\n" +
                                                  "                    if (PreviewImageFragment.canBePreviewed(ocFile)) {\n" +
                                                  "                        startImagePreview(getFile(), true);\n" +
                                                  "                    } else if (PreviewTextFragment.canBePreviewed(ocFile)) {\n" +
                                                  "                        startTextPreview(ocFile, true);\n" +
                                                  "                    }\n" +
                                                  "                }\n" +
                                                  "            }\n" +
                                                  "            setIndeterminate(false);\n" +
                                                  "        } finally {\n" +
                                                  "            if (intent != null) {\n" +
                                                  "                removeStickyBroadcast(intent);\n" +
                                                  "            }\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    public void onReceive4(Context context, Intent intent) {\n" +
                                                  "        try {\n" +
                                                  "            String event = intent.getAction();\n" +
                                                  "            Log_OC.d(TAG, \"Received broadcast \" + event);\n" +
                                                  "            String accountName = intent.getStringExtra(FileSyncAdapter.EXTRA_ACCOUNT_NAME);\n" +
                                                  "            String syncFolderRemotePath = intent.getStringExtra(FileSyncAdapter.EXTRA_FOLDER_PATH);\n" +
                                                  "            RemoteOperationResult syncResult = (RemoteOperationResult)\n" +
                                                  "                    DataHolderUtil.getInstance().retrieve(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                                  "            boolean sameAccount = getAccount() != null && accountName.equals(getAccount().name)\n" +
                                                  "                    && getStorageManager() != null;\n" +
                                                  "            if (sameAccount) {\n" +
                                                  "                if (FileSyncAdapter.EVENT_FULL_SYNC_START.equals(event)) {\n" +
                                                  "                    mSyncInProgress = true;\n" +
                                                  "                } else {\n" +
                                                  "                    OCFile currentFile = (getFile() == null) ? null :\n" +
                                                  "                            getStorageManager().getFileByPath(getFile().getRemotePath());\n" +
                                                  "                    OCFile currentDir = (getCurrentFolder() == null) ? null : \n" +
                                                  "                        getStorageManager().getFileByPath(getCurrentFolder().getRemotePath());\n" +
                                                  "                    if (currentDir == null) {\n" +
                                                  "                        DisplayUtils.showSnackMessage(getActivity(), R.string.sync_current_folder_was_removed,\n" +
                                                  "                                getCurrentFolder().getFileName());\n" +
                                                  "                        browseToRoot();\n" +
                                                  "                    } else {\n" +
                                                  "                        if (currentFile == null && !getFile().isFolder()) {\n" +
                                                  "                            currentFile = currentDir;\n" +
                                                  "                        }\n" +
                                                  "                        if (currentDir.getRemotePath().equals(syncFolderRemotePath)) {\n" +
                                                  "                            OCFileListFragment fileListFragment = getListOfFilesFragment();\n" +
                                                  "                            if (fileListFragment != null) {\n" +
                                                  "                                fileListFragment.listDirectory(currentDir, false, false);\n" +
                                                  "                            }\n" +
                                                  "                        }\n" +
                                                  "                        setFile(currentFile);\n" +
                                                  "                    }\n" +
                                                  "                    \n" +
                                                  "                    mSyncInProgress = (!FileSyncAdapter.EVENT_FULL_SYNC_END.equals(event) && \n" +
                                                  "                            !RefreshFolderOperation.EVENT_SINGLE_FOLDER_SHARES_SYNCED.equals(event));\n" +
                                                  "                    if (RefreshFolderOperation.EVENT_SINGLE_FOLDER_CONTENTS_SYNCED.equals(event) &&\n" +
                                                  "                            syncResult != null && !syncResult.isSuccess()) {\n" +
                                                  "                        if (ResultCode.UNAUTHORIZED.equals(syncResult.getCode()) || (syncResult.isException()\n" +
                                                  "                                && syncResult.getException() instanceof AuthenticatorException)) {\n" +
                                                  "                            requestCredentialsUpdate(context);\n" +
                                                  "                        } else if (RemoteOperationResult.ResultCode.SSL_RECOVERABLE_PEER_UNVERIFIED\n" +
                                                  "                                .equals(syncResult.getCode())) {\n" +
                                                  "                            showUntrustedCertDialog(syncResult);\n" +
                                                  "                        }\n" +
                                                  "                    }\n" +
                                                  "                }\n" +
                                                  "                removeStickyBroadcast(intent);\n" +
                                                  "                DataHolderUtil.getInstance().delete(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                                  "                Log_OC.d(TAG, \"Setting progress visibility to \" + mSyncInProgress);\n" +
                                                  "                setIndeterminate(mSyncInProgress);\n" +
                                                  "                setBackgroundText();\n" +
                                                  "            }\n" +
                                                  "            \n" +
                                                  "        } catch (RuntimeException e) {\n" +
                                                  "            removeStickyBroadcast(intent);\n" +
                                                  "            DataHolderUtil.getInstance().delete(intent.getStringExtra(FileSyncAdapter.EXTRA_RESULT));\n" +
                                                  "        }\n" +
                                                  "    }*/\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "Intent intent;\n" +
                                         "@@\n" +
                                         "(\n" +
                                         "- sendStickyBroadcast(intent);\n" +
                                         "+ sendBroadcast(intent);\n" +
                                         "|\n" +
                                         "- removeStickyBroadcast(intent);\n" +
                                         ")\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteBranch() {
        // contract: a patch should be able to delete a complete branch statement

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- if (true) {\n" +
                                         "-     int x = 0;\n" +
                                         "- }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteBranchInBranch() {
        // contract: a patch should be able to delete a complete branch statement nested inside another branch

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void before() {}\n" +
                                               "    void after() {}\n" +
                                               "    \n" +
                                               "    void m1() {\n" +
                                               "        boolean somevariable = Math.random() < 0.5;\n" +
                                               "        \n" +
                                               "        if (somevariable) {\n" +
                                               "            before();\n" +
                                               "            \n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "            \n" +
                                               "            after();\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void before() {}\n" +
                                                  "    void after() {}\n" +
                                                  "    \n" +
                                                  "    void m1() {\n" +
                                                  "        boolean somevariable = Math.random() < 0.5;\n" +
                                                  "        \n" +
                                                  "        if (somevariable) {\n" +
                                                  "            before();\n" +
                                                  "            after();\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- if (true) {\n" +
                                         "-     int x = 0;\n" +
                                         "- }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteEnclosingBranch() {
        // contract: a patch should be able to delete an enclosing branch statement while keeping inner context

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        if (true) {\n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m3() {\n" +
                                               "        if (false) {\n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int x = 0;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        if (true) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m3() {\n" +
                                                  "        if (false) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- if (true) {\n" +
                                         "      int x = 0;\n" +
                                         "- }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteEnclosingBranchDots() {
        // contract: a patch should be able to delete an enclosing branch statement while keeping inner context

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        if (true) {\n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m3() {\n" +
                                               "        if (false) {\n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m4() {\n" +
                                               "        if (true) {\n" +
                                               "            if (false) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int x = 0;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        if (true) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m3() {\n" +
                                                  "        if (false) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m4() {\n" +
                                                  "        if (false) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- if (true) {\n" +
                                         "      ...\n" +
                                         "- }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteStmAfterBranch() {
        // contract: only the statement below the branch should be removed

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int positive(int input) {\n" +
                                               "        int ans = 0;\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        if (input > 0) {\n" +
                                               "            ans = 1;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        return ans;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int positive(int input) {\n" +
                                                  "        int ans = 0;\n" +
                                                  "        \n" +
                                                  "        ans = 1;\n" +
                                                  "        \n" +
                                                  "        if (input > 0) {\n" +
                                                  "            ans = 1;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        return ans;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "  if (input > 0) {\n" +
                                         "  ...\n" +
                                         "  }\n" +
                                         "- v1 = C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteStmBeforeBranch() {
        // contract: only the statement above the branch should be removed

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int positive(int input) {\n" +
                                               "        int ans = 0;\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        if (input > 0) {\n" +
                                               "            ans = 1;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        return ans;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int positive(int input) {\n" +
                                                  "        int ans = 0;\n" +
                                                  "        \n" +
                                                  "        if (input > 0) {\n" +
                                                  "            ans = 1;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        ans = 1;\n" +
                                                  "        \n" +
                                                  "        return ans;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- v1 = C;\n" +
                                         "  if (input > 0) {\n" +
                                         "  ...\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteStmInBranch() {
        // contract: only the statement inside the branch should be removed

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int positive(int input) {\n" +
                                               "        int ans = 0;\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        if (input > 0) {\n" +
                                               "            ans = 1;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        return ans;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int positive(int input) {\n" +
                                                  "        int ans = 0;\n" +
                                                  "        \n" +
                                                  "        ans = 1;\n" +
                                                  "        \n" +
                                                  "        if (input > 0) {\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        ans = 1;\n" +
                                                  "        \n" +
                                                  "        return ans;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "  if (input > 0) {\n" +
                                         "-     v1 = C;\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDotsShortestPath() {
        // contract: dots by default should only match the shortest path between enclosing anchors (if any)

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void foo(Object x) {}\n" +
                                               "    void bar(Object x) {}\n" +
                                               "    \n" +
                                               "    void m1(Object x) {\n" +
                                               "        foo(x);\n" +
                                               "        foo(x);\n" +
                                               "        bar(x);\n" +
                                               "        bar(x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void foo(Object x) {}\n" +
                                                  "    void bar(Object x) {}\n" +
                                                  "    \n" +
                                                  "    void m1(Object x) {\n" +
                                                  "        foo(x);\n" +
                                                  "        bar(x);\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- foo(x);\n" +
                                         "  ...\n" +
                                         "- bar(x);\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDotsWhenAny() {
        // contract: dots shortest path restriction is lifted by using when any

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void foo(Object x) {}\n" +
                                               "    void bar(Object x) {}\n" +
                                               "    \n" +
                                               "    void m1(Object x) {\n" +
                                               "        foo(x);\n" +
                                               "        foo(x);\n" +
                                               "        bar(x);\n" +
                                               "        bar(x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void foo(Object x) {}\n" +
                                                  "    void bar(Object x) {}\n" +
                                                  "    \n" +
                                                  "    void m1(Object x) {\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- foo(x);\n" +
                                         "  ... when any\n" +
                                         "- bar(x);\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testEncloseInBranch() {
        // contract: a patch should be able to add a branch statement enclosing context

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void anchor() {}\n" +
                                               "    void foo() {}\n" +
                                               "    \n" +
                                               "    void m1() {\n" +
                                               "        anchor();\n" +
                                               "        foo();\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void anchor() {}\n" +
                                                  "    void foo() {}\n" +
                                                  "    \n" +
                                                  "    void m1() {\n" +
                                                  "        boolean debug = Math.random() < 0.5;\n" +
                                                  "        anchor();\n" +
                                                  "        if (debug) {\n" +
                                                  "            foo();\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "+ boolean debug = Math.random() < 0.5;\n" +
                                         "  anchor();\n" +
                                         "+ if (debug) {\n" +
                                         "      foo();\n" +
                                         "+ }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testFieldReads() {
        // contract: correct matching of field reads

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class Point { Integer x,y; public Point(Integer x, Integer y) {} }\n" +
                                               "    class Logger { public void log(Object x) {} }\n" +
                                               "    \n" +
                                               "    void m1() {\n" +
                                               "        Point point = new Point(1,2);\n" +
                                               "        Logger logger = new Logger();\n" +
                                               "        logger.log(point);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        Point point = new Point(1,2);\n" +
                                               "        Logger logger = new Logger();\n" +
                                               "        logger.log(point.x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class Point { Integer x,y; public Point(Integer x, Integer y) {} }\n" +
                                                  "    class Logger { public void log(Object x) {} }\n" +
                                                  "    \n" +
                                                  "    void m1() {\n" +
                                                  "        Point point = new Point(1,2);\n" +
                                                  "        Logger logger = new Logger();\n" +
                                                  "        logger.log(point);\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        Point point = new Point(1,2);\n" +
                                                  "        Logger logger = new Logger();\n" +
                                                  "        logger.log(\"The X coordinate is \" + point.x.toString());\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "Point p;\n" +
                                         "@@\n" +
                                         "- logger.log(p.x);\n" +
                                         "+ logger.log(\"The X coordinate is \" + p.x.toString());\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testHelloWorld() {
        // contract: hello world template test

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void foo() {\n" +
                                               "        int x = 1;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void foo() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "@@\n" +
                                         "- int v1 = 1;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testMatchAnyType() {
        // contract: a 'type' metavariable should match any type

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class ASpecificType {}\n" +
                                               "    \n" +
                                               "    void foo() {\n" +
                                               "        int x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void bar() {\n" +
                                               "        float x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void baz() {\n" +
                                               "        ASpecificType x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class ASpecificType {}\n" +
                                                  "    \n" +
                                                  "    void foo() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void bar() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void baz() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "@@\n" +
                                         "- T x;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testMatchSpecificType() {
        // contract: a concretely given type in SmPL should match that precise type

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class ASpecificType {}\n" +
                                               "    \n" +
                                               "    void foo() {\n" +
                                               "        int x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void bar() {\n" +
                                               "        float x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void baz() {\n" +
                                               "        ASpecificType x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class ASpecificType {}\n" +
                                                  "    \n" +
                                                  "    void foo() {\n" +
                                                  "        int x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void bar() {\n" +
                                                  "        float x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void baz() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- ASpecificType x;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testMethodHeaderBinding() {
        // contract: binding metavariables on the method header

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int square(int x) {\n" +
                                               "        return x*x;\n" +
                                               "    }\n" +
                                               "    float square(float x) {\n" +
                                               "        return x*x;\n" +
                                               "    }\n" +
                                               "    int square(double x) {\n" +
                                               "        return x*x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int square(int x) {\n" +
                                                  "        int y = 0;\n" +
                                                  "        return x*x;\n" +
                                                  "    }\n" +
                                                  "    float square(float x) {\n" +
                                                  "        float y = 0;\n" +
                                                  "        return x*x;\n" +
                                                  "    }\n" +
                                                  "    int square(double x) {\n" +
                                                  "        return x*x;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T1;\n" +
                                         "expression E;\n" +
                                         "@@\n" +
                                         "  T1 square(T1 x) {\n" +
                                         "+     T1 y = 0;\n" +
                                         "      return E;\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testMethodHeaderDots() {
        // contract: using dots to match arbitrary sequences of parameters in method header

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void drawCircle(Point origin, float radius) {\n" +
                                               "        log(\"Coordinates: \" + origin.x.toString() + \", \" + origin.y.toString());\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void drawRectangle(float width, float height, Point topLeftCorner) {\n" +
                                               "        log(\"Coordinates: \" + topLeftCorner.x.toString() + \", \" + topLeftCorner.y.toString());\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void drawCircle(Point origin, float radius) {\n" +
                                                  "        log(\"Point: \" + origin.toString());\n" +
                                                  "    }\n" +
                                                  "    void drawRectangle(float width, float height, Point topLeftCorner) {\n" +
                                                  "        log(\"Point: \" + topLeftCorner.toString());\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier fn, pt;\n" +
                                         "@@\n" +
                                         "  T fn(..., Point pt, ...) {\n" +
                                         "      ...\n" +
                                         "-     log(\"Coordinates: \" + pt.x.toString() + \", \" + pt.y.toString());\n" +
                                         "+     log(\"Point: \" + pt.toString());\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testMethodHeaderLiteralMatch() {
        // contract: literal matching on the method header

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int square(int x) {\n" +
                                               "        return x*x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    int cube(int x) {\n" +
                                               "        return x*x*x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int square(int x) {\n" +
                                                  "        log(\"square called\");\n" +
                                                  "        return x*x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    int cube(int x) {\n" +
                                                  "        return x*x*x;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "expression E;\n" +
                                         "@@\n" +
                                         "  int square(int x) {\n" +
                                         "+     log(\"square called\");\n" +
                                         "      return E;\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testPrependContextBranch() {
        // contract: a patch should be able to prepend elements above a context branch

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int y = 1;\n" +
                                                  "        if (true) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "+ int y = 1;\n" +
                                         "  if (true) {\n" +
                                         "      int x = 0;\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testPrependToContext() {
        // contract: a patch should be able to prepend elements to a context statement

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        int x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        int y;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int prepended1;\n" +
                                                  "        int prepended2;\n" +
                                                  "        int x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        int y;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "+ int prepended1;\n" +
                                         "+ int prepended2;\n" +
                                         "  int x;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstants001() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    float square(float x) { return x*x; }\n" +
                                               "    void print(Object x) { System.out.println(x); }\n" +
                                               "    \n" +
                                               "    int m1() {\n" +
                                               "        int x = 0;\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    int m1b() {\n" +
                                               "        int x = 0;\n" +
                                               "        x = x + 1;\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    float m2() {\n" +
                                               "        float x = 3.0f;\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    float m2b() {\n" +
                                               "        float x = 3.0f;\n" +
                                               "        float y = square(x);\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    String m3() {\n" +
                                               "        String x = \"Hello, World!\";\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    String m3b() {\n" +
                                               "        String x = \"Hello, World!\";\n" +
                                               "        print(x);\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    float square(float x) { return x*x; }\n" +
                                                  "    void print(Object x) { System.out.println(x); }\n" +
                                                  "    \n" +
                                                  "    int m1() {\n" +
                                                  "        return 0;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    int m1b() {\n" +
                                                  "        int x = 0;\n" +
                                                  "        x = x + 1;\n" +
                                                  "        return x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    float m2() {\n" +
                                                  "        return 3.0f;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    float m2b() {\n" +
                                                  "        float x = 3.0f;\n" +
                                                  "        float y = square(x);\n" +
                                                  "        return x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    String m3() {\n" +
                                                  "        return \"Hello, World!\";\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    String m3b() {\n" +
                                                  "        String x = \"Hello, World!\";\n" +
                                                  "        print(x);\n" +
                                                  "        return x;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranch() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo(boolean x)\n" +
                                               "    {\n" +
                                               "        int ret = 42;\n" +
                                               "        \n" +
                                               "        if (x == true)\n" +
                                               "        {\n" +
                                               "            return ret;\n" +
                                               "        }\n" +
                                               "        else\n" +
                                               "        {\n" +
                                               "            return ret;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo(boolean x)\n" +
                                                  "    {\n" +
                                                  "        if (x == true)\n" +
                                                  "        {\n" +
                                                  "            return 42;\n" +
                                                  "        }\n" +
                                                  "        else\n" +
                                                  "        {\n" +
                                                  "            return 42;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranchMultiple() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo(int n)\n" +
                                               "    {\n" +
                                               "        int a = 123;\n" +
                                               "        int b = 234;\n" +
                                               "        int c = 345;\n" +
                                               "        \n" +
                                               "        if (n == 0)\n" +
                                               "        {\n" +
                                               "            return a;\n" +
                                               "        }\n" +
                                               "        else if (n == 1)\n" +
                                               "        {\n" +
                                               "            return b;\n" +
                                               "        }\n" +
                                               "        else\n" +
                                               "        {\n" +
                                               "            return c;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo(int n)\n" +
                                                  "    {\n" +
                                                  "        int a = 123;\n" +
                                                  "        int b = 234;\n" +
                                                  "        int c = 345;\n" +
                                                  "        \n" +
                                                  "        if (n == 0)\n" +
                                                  "        {\n" +
                                                  "            return a;\n" +
                                                  "        }\n" +
                                                  "        else if (n == 1)\n" +
                                                  "        {\n" +
                                                  "            return b;\n" +
                                                  "        }\n" +
                                                  "        else\n" +
                                                  "        {\n" +
                                                  "            return c;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranchMultipleWhenExists() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo(int n)\n" +
                                               "    {\n" +
                                               "        int a = 123;\n" +
                                               "        int b = 234;\n" +
                                               "        int c = 345;\n" +
                                               "        \n" +
                                               "        if (n == 0)\n" +
                                               "        {\n" +
                                               "            return a;\n" +
                                               "        }\n" +
                                               "        else if (n == 1)\n" +
                                               "        {\n" +
                                               "            return b;\n" +
                                               "        }\n" +
                                               "        else\n" +
                                               "        {\n" +
                                               "            return c;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo(int n)\n" +
                                                  "    {\n" +
                                                  "        if (n == 0)\n" +
                                                  "        {\n" +
                                                  "            return 123;\n" +
                                                  "        }\n" +
                                                  "        else if (n == 1)\n" +
                                                  "        {\n" +
                                                  "            return 234;\n" +
                                                  "        }\n" +
                                                  "        else\n" +
                                                  "        {\n" +
                                                  "            return 345;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "      when exists\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsElselessBranch() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo(boolean x)\n" +
                                               "    {\n" +
                                               "        int ret = 42;\n" +
                                               "        \n" +
                                               "        if (x == true)\n" +
                                               "        {\n" +
                                               "            return ret;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        return ret;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo(boolean x)\n" +
                                                  "    {\n" +
                                                  "        if (x == true)\n" +
                                                  "        {\n" +
                                                  "            return 42;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        return 42;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsExpressionlessReturnBug() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public void foo(boolean x)\n" +
                                               "    {\n" +
                                               "        int ret = 42;\n" +
                                               "        return;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public void foo(boolean x)\n" +
                                                  "    {\n" +
                                                  "        int ret = 42;\n" +
                                                  "        return;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsRejectUsageInBranchCondition() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo()\n" +
                                               "    {\n" +
                                               "        int y = 42;\n" +
                                               "        \n" +
                                               "        if (y > 0)\n" +
                                               "        {\n" +
                                               "            return y;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        return y;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo()\n" +
                                                  "    {\n" +
                                                  "        int y = 42;\n" +
                                                  "        \n" +
                                                  "        if (y > 0)\n" +
                                                  "        {\n" +
                                                  "            return y;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        return y;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testTypedIdentifierMetavariables1() {
        // contract: correct bindings of explicitly typed identifier metavariables

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class ASpecificType {}\n" +
                                               "    void log(Object x) { System.out.println(x.toString()); }\n" +
                                               "    \n" +
                                               "    void foo() {\n" +
                                               "        int x = 0;\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void bar() {\n" +
                                               "        float x = 0.0f;\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void baz() {\n" +
                                               "        ASpecificType x = new ASpecificType();\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class ASpecificType {}\n" +
                                                  "    void log(Object x) { System.out.println(x.toString()); }\n" +
                                                  "    \n" +
                                                  "    void foo() {\n" +
                                                  "        int x = 0;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void bar() {\n" +
                                                  "        float x = 0.0f;\n" +
                                                  "        log(x);\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void baz() {\n" +
                                                  "        ASpecificType x = new ASpecificType();\n" +
                                                  "        log(x);\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "int x;\n" +
                                         "@@\n" +
                                         "- log(x);\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testTypedIdentifierMetavariables2() {
        // contract: correct bindings of explicitly typed identifier metavariables

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class ASpecificType {}\n" +
                                               "    void log(Object x) { System.out.println(x.toString()); }\n" +
                                               "    \n" +
                                               "    void foo() {\n" +
                                               "        int x = 0;\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void bar() {\n" +
                                               "        float x = 0.0f;\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void baz() {\n" +
                                               "        ASpecificType x = new ASpecificType();\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class ASpecificType {}\n" +
                                                  "    void log(Object x) { System.out.println(x.toString()); }\n" +
                                                  "    \n" +
                                                  "    void foo() {\n" +
                                                  "        int x = 0;\n" +
                                                  "        log(x);\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void bar() {\n" +
                                                  "        float x = 0.0f;\n" +
                                                  "        log(x);\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void baz() {\n" +
                                                  "        ASpecificType x = new ASpecificType();\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "ASpecificType x;\n" +
                                         "@@\n" +
                                         "- log(x);\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
}
