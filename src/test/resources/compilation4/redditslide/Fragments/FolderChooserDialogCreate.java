package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Collections;
/**
 *
 *
 * @author Aidan Follestad (afollestad)
 */
public class FolderChooserDialogCreate extends android.support.v4.app.DialogFragment implements com.afollestad.materialdialogs.MaterialDialog.ListCallback {
    private static final java.lang.String DEFAULT_TAG = "[MD_FOLDER_SELECTOR]";

    private java.io.File parentFolder;

    private java.io.File[] parentContents;

    private boolean canGoUp = true;

    private me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback mCallback;

    java.lang.String createdFile;

    public interface FolderCallback {
        void onFolderSelection(@android.support.annotation.NonNull
        me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog, @android.support.annotation.NonNull
        java.io.File folder);
    }

    public FolderChooserDialogCreate() {
    }

    java.lang.String[] getContentsArray() {
        if (parentContents == null)
            return new java.lang.String[]{  };

        java.lang.String[] results = new java.lang.String[parentContents.length + (canGoUp ? 1 : 0)];
        if (canGoUp)
            results[0] = "...";

        for (int i = 0; i < parentContents.length; i++)
            results[canGoUp ? i + 1 : i] = parentContents[i].getName();

        return results;
    }

    java.io.File[] listFiles() {
        java.io.File[] contents = parentFolder.listFiles();
        java.util.List<java.io.File> results = new java.util.ArrayList<>();
        if (contents != null) {
            for (java.io.File fi : contents) {
                if (fi.isDirectory())
                    results.add(fi);

            }
            java.util.Collections.sort(results, new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderSorter());
            return results.toArray(new java.io.File[results.size()]);
        }
        return null;
    }

    @java.lang.SuppressWarnings("ConstantConditions")
    @android.support.annotation.NonNull
    @java.lang.Override
    public android.app.Dialog onCreateDialog(android.os.Bundle savedInstanceState) {
        if ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) && (android.support.v4.app.ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED)) {
            return new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(com.afollestad.materialdialogs.commons.R.string.md_error_label).content(com.afollestad.materialdialogs.commons.R.string.md_storage_perm_error).positiveText(android.R.string.ok).build();
        }
        if ((getArguments() == null) || (!getArguments().containsKey("builder")))
            throw new java.lang.IllegalStateException("You must create a FolderChooserDialog using the Builder.");

        if (!getArguments().containsKey("current_path"))
            getArguments().putString("current_path", getBuilder().mInitialPath);

        parentFolder = new java.io.File(getArguments().getString("current_path"));
        parentContents = listFiles();
        return new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(parentFolder.getAbsolutePath()).items(getContentsArray()).itemsCallback(this).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(@android.support.annotation.NonNull
            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
            com.afollestad.materialdialogs.DialogAction which) {
                dialog.dismiss();
                mCallback.onFolderSelection(me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.this, parentFolder);
            }
        }).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(@android.support.annotation.NonNull
            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
            com.afollestad.materialdialogs.DialogAction which) {
                dialog.dismiss();
            }
        }).onNeutral(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(@android.support.annotation.NonNull
            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
            com.afollestad.materialdialogs.DialogAction which) {
                dialog.dismiss();
                new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(me.ccrama.redditslide.R.string.create_folder).inputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD).input(getContext().getString(me.ccrama.redditslide.R.string.folder_name), "", false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                    @java.lang.Override
                    public void onInput(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                        createdFile = input.toString();
                    }
                }).alwaysCallInputCallback().negativeText(getBuilder().mCancelButton).positiveText(me.ccrama.redditslide.R.string.btn_create).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction which) {
                        dialog.dismiss();
                    }
                }).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction which) {
                        java.io.File toCreate = new java.io.File((parentFolder.getPath() + java.io.File.separator) + createdFile);
                        toCreate.mkdir();
                        dialog.dismiss();
                        mCallback.onFolderSelection(me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.this, toCreate);
                    }
                }).show();
            }
        }).autoDismiss(false).positiveText(getBuilder().mChooseButton).negativeText(getBuilder().mCancelButton).neutralText(me.ccrama.redditslide.R.string.create_folder).build();
    }

    @java.lang.Override
    public void onSelection(com.afollestad.materialdialogs.MaterialDialog materialDialog, android.view.View view, int i, java.lang.CharSequence s) {
        if (canGoUp && (i == 0)) {
            parentFolder = parentFolder.getParentFile();
            if (parentFolder.getAbsolutePath().equals("/storage/emulated"))
                parentFolder = parentFolder.getParentFile();

            canGoUp = parentFolder.getParent() != null;
        } else {
            parentFolder = parentContents[canGoUp ? i - 1 : i];
            canGoUp = true;
            if (parentFolder.getAbsolutePath().equals("/storage/emulated"))
                parentFolder = android.os.Environment.getExternalStorageDirectory();

        }
        parentContents = listFiles();
        com.afollestad.materialdialogs.MaterialDialog dialog = ((com.afollestad.materialdialogs.MaterialDialog) (getDialog()));
        dialog.setTitle(parentFolder.getAbsolutePath());
        getArguments().putString("current_path", parentFolder.getAbsolutePath());
        dialog.setItems(getContentsArray());
    }

    @java.lang.Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        mCallback = ((me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback) (activity));
    }

    public void show(android.support.v4.app.FragmentActivity context) {
        final java.lang.String tag = getBuilder().mTag;
        android.support.v4.app.Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null) {
            ((android.support.v4.app.DialogFragment) (frag)).dismiss();
            context.getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }
        show(context.getSupportFragmentManager(), tag);
    }

    public static class Builder implements java.io.Serializable {
        @android.support.annotation.NonNull
        protected final transient android.support.v7.app.AppCompatActivity mContext;

        @android.support.annotation.StringRes
        protected int mChooseButton;

        @android.support.annotation.StringRes
        protected int mCancelButton;

        protected java.lang.String mInitialPath;

        protected java.lang.String mTag;

        public <ActivityType extends android.support.v7.app.AppCompatActivity & me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback> Builder(@android.support.annotation.NonNull
        ActivityType context) {
            mContext = context;
            mChooseButton = com.afollestad.materialdialogs.commons.R.string.md_choose_label;
            mCancelButton = android.R.string.cancel;
            mInitialPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        @android.support.annotation.NonNull
        public me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder chooseButton(@android.support.annotation.StringRes
        int text) {
            mChooseButton = text;
            return this;
        }

        @android.support.annotation.NonNull
        public me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder cancelButton(@android.support.annotation.StringRes
        int text) {
            mCancelButton = text;
            return this;
        }

        @android.support.annotation.NonNull
        public me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder initialPath(@android.support.annotation.Nullable
        java.lang.String initialPath) {
            if (initialPath == null)
                initialPath = java.io.File.separator;

            mInitialPath = initialPath;
            return this;
        }

        @android.support.annotation.NonNull
        public me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder tag(@android.support.annotation.Nullable
        java.lang.String tag) {
            if (tag == null)
                tag = me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.DEFAULT_TAG;

            mTag = tag;
            return this;
        }

        @android.support.annotation.NonNull
        public me.ccrama.redditslide.Fragments.FolderChooserDialogCreate build() {
            me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog = new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate();
            android.os.Bundle args = new android.os.Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @android.support.annotation.NonNull
        public me.ccrama.redditslide.Fragments.FolderChooserDialogCreate show() {
            me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog = build();
            dialog.show(mContext);
            return dialog;
        }
    }

    @java.lang.SuppressWarnings("ConstantConditions")
    @android.support.annotation.NonNull
    private me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder getBuilder() {
        return ((me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder) (getArguments().getSerializable("builder")));
    }

    private static class FolderSorter implements java.util.Comparator<java.io.File> {
        @java.lang.Override
        public int compare(java.io.File lhs, java.io.File rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}