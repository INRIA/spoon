package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Activities.Draw;
import me.ccrama.redditslide.util.SubmissionParser;
import java.util.ArrayList;
import me.ccrama.redditslide.*;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
/**
 * Created by carlo_000 on 10/18/2015.
 */
public class DoEditorActions {
    public static void doActions(final android.widget.EditText editText, final android.view.View baseView, final android.support.v4.app.FragmentManager fm, final android.app.Activity a, final java.lang.String oldComment, @android.support.annotation.Nullable
    final java.lang.String[] authors) {
        baseView.findViewById(me.ccrama.redditslide.R.id.bold).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (editText.hasSelection()) {
                    me.ccrama.redditslide.Views.DoEditorActions.wrapString("**", editText);// If the user has text selected, wrap that text in the symbols

                } else {
                    // If the user doesn't have text selected, put the symbols around the cursor's position
                    int pos = editText.getSelectionStart();
                    editText.getText().insert(pos, "**");
                    editText.getText().insert(pos + 1, "**");
                    editText.setSelection(pos + 2);// put the cursor between the symbols

                }
            }
        });
        if (baseView.findViewById(me.ccrama.redditslide.R.id.author) != null) {
            if ((authors != null) && (authors.length > 0)) {
                baseView.findViewById(me.ccrama.redditslide.R.id.author).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        if (authors.length == 1) {
                            java.lang.String author = "/u/" + authors[0];
                            me.ccrama.redditslide.Views.DoEditorActions.insertBefore(author, editText);
                        } else {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a).setTitle(me.ccrama.redditslide.R.string.authors_above).setItems(authors, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    java.lang.String author = "/u/" + authors[which];
                                    me.ccrama.redditslide.Views.DoEditorActions.insertBefore(author, editText);
                                }
                            }).setNeutralButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                        }
                    }
                });
            } else {
                baseView.findViewById(me.ccrama.redditslide.R.id.author).setVisibility(android.view.View.GONE);
            }
        }
        baseView.findViewById(me.ccrama.redditslide.R.id.italics).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (editText.hasSelection()) {
                    me.ccrama.redditslide.Views.DoEditorActions.wrapString("*", editText);// If the user has text selected, wrap that text in the symbols

                } else {
                    // If the user doesn't have text selected, put the symbols around the cursor's position
                    int pos = editText.getSelectionStart();
                    editText.getText().insert(pos, "*");
                    editText.getText().insert(pos + 1, "*");
                    editText.setSelection(pos + 1);// put the cursor between the symbols

                }
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.strike).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (editText.hasSelection()) {
                    me.ccrama.redditslide.Views.DoEditorActions.wrapString("~~", editText);// If the user has text selected, wrap that text in the symbols

                } else {
                    // If the user doesn't have text selected, put the symbols around the cursor's position
                    int pos = editText.getSelectionStart();
                    editText.getText().insert(pos, "~~");
                    editText.getText().insert(pos + 2, "~~");
                    editText.setSelection(pos + 2);// put the cursor between the symbols

                }
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.spoiler).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (editText.hasSelection()) {
                    me.ccrama.redditslide.Views.DoEditorActions.wrapString(">!", "!<", editText);// If the user has text selected, wrap that text in the symbols

                } else {
                    // If the user doesn't have text selected, put the symbols around the cursor's position
                    int pos = editText.getSelectionStart();
                    editText.getText().insert(pos, ">!");
                    editText.getText().insert(pos + 2, "!<");
                    editText.setSelection(pos + 2);// put the cursor between the symbols

                }
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.savedraft).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                me.ccrama.redditslide.Drafts.addDraft(editText.getText().toString());
                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(baseView.findViewById(me.ccrama.redditslide.R.id.savedraft), "Draft saved", android.support.design.widget.Snackbar.LENGTH_SHORT);
                android.view.View view = s.getView();
                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(android.graphics.Color.WHITE);
                s.setAction(me.ccrama.redditslide.R.string.btn_discard, new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View view) {
                        me.ccrama.redditslide.Drafts.deleteDraft(me.ccrama.redditslide.Drafts.getDrafts().size() - 1);
                    }
                });
                s.show();
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.draft).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                final java.util.ArrayList<java.lang.String> drafts = me.ccrama.redditslide.Drafts.getDrafts();
                java.util.Collections.reverse(drafts);
                final java.lang.String[] draftText = new java.lang.String[drafts.size()];
                for (int i = 0; i < drafts.size(); i++) {
                    draftText[i] = drafts.get(i);
                }
                if (drafts.isEmpty()) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a).setTitle(me.ccrama.redditslide.R.string.dialog_no_drafts).setMessage(me.ccrama.redditslide.R.string.dialog_no_drafts_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a).setTitle(me.ccrama.redditslide.R.string.choose_draft).setItems(draftText, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            editText.setText(editText.getText().toString() + draftText[which]);
                        }
                    }).setNeutralButton(me.ccrama.redditslide.R.string.btn_cancel, null).setPositiveButton(me.ccrama.redditslide.R.string.manage_drafts, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            final boolean[] selected = new boolean[drafts.size()];
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a).setTitle(me.ccrama.redditslide.R.string.choose_draft).setNeutralButton(me.ccrama.redditslide.R.string.btn_cancel, null).alwaysCallMultiChoiceCallback().setNegativeButton(me.ccrama.redditslide.R.string.btn_delete, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a).setTitle(me.ccrama.redditslide.R.string.really_delete_drafts).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            java.util.ArrayList<java.lang.String> draf = new java.util.ArrayList<>();
                                            for (int i = 0; i < draftText.length; i++) {
                                                if (!selected[i]) {
                                                    draf.add(draftText[i]);
                                                }
                                            }
                                            me.ccrama.redditslide.Drafts.save(draf);
                                        }
                                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
                                }
                            }).setMultiChoiceItems(draftText, selected, new android.content.DialogInterface.OnMultiChoiceClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                                    selected[which] = isChecked;
                                }
                            }).show();
                        }
                    }).show();
                }
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.imagerep).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                me.ccrama.redditslide.Views.DoEditorActions.e = editText.getText();
                me.ccrama.redditslide.Views.DoEditorActions.sStart = editText.getSelectionStart();
                me.ccrama.redditslide.Views.DoEditorActions.sEnd = editText.getSelectionEnd();
                gun0912.tedbottompicker.TedBottomPicker tedBottomPicker = new gun0912.tedbottompicker.TedBottomPicker.Builder(editText.getContext()).setOnImageSelectedListener(new gun0912.tedbottompicker.TedBottomPicker.OnImageSelectedListener() {
                    @java.lang.Override
                    public void onImageSelected(java.util.List<android.net.Uri> uri) {
                        me.ccrama.redditslide.Views.DoEditorActions.handleImageIntent(uri, editText, a);
                    }
                }).setLayoutResource(me.ccrama.redditslide.R.layout.image_sheet_dialog).setTitle("Choose a photo").create();
                tedBottomPicker.show(fm);
                android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (editText.getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.draw).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (me.ccrama.redditslide.SettingValues.isPro) {
                    me.ccrama.redditslide.Views.DoEditorActions.doDraw(a, editText, fm);
                } else {
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a).setTitle(me.ccrama.redditslide.R.string.general_cropdraw_ispro).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                            try {
                                a.startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + a.getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            } catch (android.content.ActivityNotFoundException e) {
                                a.startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=" + a.getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            }
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no_danks, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    if (me.ccrama.redditslide.SettingValues.previews > 0) {
                        b.setNeutralButton(a.getString(me.ccrama.redditslide.R.string.pro_previews, me.ccrama.redditslide.SettingValues.previews), new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, me.ccrama.redditslide.SettingValues.previews - 1).apply();
                                me.ccrama.redditslide.SettingValues.previews = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, 10);
                                me.ccrama.redditslide.Views.DoEditorActions.doDraw(a, editText, fm);
                            }
                        });
                    }
                    b.show();
                }
            }
        });
        /* todo baseView.findViewById(R.id.superscript).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        insertBefore("^", editText);
        }
        });
         */
        baseView.findViewById(me.ccrama.redditslide.R.id.size).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                me.ccrama.redditslide.Views.DoEditorActions.insertBefore("#", editText);
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.quote).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (oldComment != null) {
                    final android.widget.TextView showText = new android.widget.TextView(a);
                    showText.setText(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(oldComment));// text we get is escaped, we don't want that

                    showText.setTextIsSelectable(true);
                    int sixteen = me.ccrama.redditslide.Reddit.dpToPxVertical(24);
                    showText.setPadding(sixteen, 0, sixteen, 0);
                    com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(a);
                    builder.customView(showText, false).title(me.ccrama.redditslide.R.string.editor_actions_quote_comment).cancelable(true).positiveText(a.getString(me.ccrama.redditslide.R.string.btn_select)).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction which) {
                            java.lang.String selected = showText.getText().toString().substring(showText.getSelectionStart(), showText.getSelectionEnd());
                            if (selected.equals("")) {
                                selected = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(oldComment);
                            }
                            me.ccrama.redditslide.Views.DoEditorActions.insertBefore(("> " + selected.replaceAll("\n", "\n> ")) + "\n\n", editText);
                        }
                    }).negativeText(a.getString(me.ccrama.redditslide.R.string.btn_cancel)).show();
                    android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (editText.getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                } else {
                    me.ccrama.redditslide.Views.DoEditorActions.insertBefore("> ", editText);
                }
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.bulletlist).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();
                java.lang.String selected = editText.getText().toString().substring(java.lang.Math.min(start, end), java.lang.Math.max(start, end));
                if (!selected.equals("")) {
                    selected = selected.replaceFirst("^[^\n]", "* $0").replaceAll("\n", "\n* ");
                    editText.getText().replace(java.lang.Math.min(start, end), java.lang.Math.max(start, end), selected);
                } else {
                    me.ccrama.redditslide.Views.DoEditorActions.insertBefore("* ", editText);
                }
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.numlist).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();
                java.lang.String selected = editText.getText().toString().substring(java.lang.Math.min(start, end), java.lang.Math.max(start, end));
                if (!selected.equals("")) {
                    selected = selected.replaceFirst("^[^\n]", "1. $0").replaceAll("\n", "\n1. ");
                    editText.getText().replace(java.lang.Math.min(start, end), java.lang.Math.max(start, end), selected);
                } else {
                    me.ccrama.redditslide.Views.DoEditorActions.insertBefore("1. ", editText);
                }
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.preview).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                java.util.List<org.commonmark.Extension> extensions = java.util.Arrays.asList(org.commonmark.ext.gfm.tables.TablesExtension.create(), org.commonmark.ext.gfm.strikethrough.StrikethroughExtension.create());
                org.commonmark.parser.Parser parser = org.commonmark.parser.Parser.builder().extensions(extensions).build();
                org.commonmark.html.HtmlRenderer renderer = org.commonmark.html.HtmlRenderer.builder().extensions(extensions).build();
                org.commonmark.node.Node document = parser.parse(editText.getText().toString());
                java.lang.String html = renderer.render(document);
                android.view.LayoutInflater inflater = a.getLayoutInflater();
                final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.parent_comment_dialog, null);
                final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a);
                me.ccrama.redditslide.Views.DoEditorActions.setViews(html, "NO sub", ((me.ccrama.redditslide.SpoilerRobotoTextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.firstTextView))), ((me.ccrama.redditslide.Views.CommentOverflow) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.commentOverflow))));
                builder.setView(dialoglayout);
                builder.show();
            }
        });
        baseView.findViewById(me.ccrama.redditslide.R.id.link).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                final android.view.LayoutInflater inflater = android.view.LayoutInflater.from(a);
                final android.widget.LinearLayout layout = ((android.widget.LinearLayout) (inflater.inflate(me.ccrama.redditslide.R.layout.insert_link, null)));
                int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.fontColor };
                android.content.res.TypedArray ta = baseView.getContext().obtainStyledAttributes(new me.ccrama.redditslide.ColorPreferences(baseView.getContext()).getFontStyle().getBaseId(), attrs);
                ta.recycle();
                java.lang.String selectedText = "";
                // if the user highlighted text before inputting a URL, use that text for the descriptionBox
                if (editText.hasSelection()) {
                    final int startSelection = editText.getSelectionStart();
                    final int endSelection = editText.getSelectionEnd();
                    selectedText = editText.getText().toString().substring(startSelection, endSelection);
                }
                final boolean selectedTextNotEmpty = !selectedText.isEmpty();
                final com.afollestad.materialdialogs.MaterialDialog dialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(editText.getContext()).title(me.ccrama.redditslide.R.string.editor_title_link).customView(layout, false).positiveColorAttr(me.ccrama.redditslide.R.attr.tintColor).positiveText(me.ccrama.redditslide.R.string.editor_action_link).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction which) {
                        final android.widget.EditText urlBox = ((android.widget.EditText) (dialog.findViewById(me.ccrama.redditslide.R.id.url_box)));
                        final android.widget.EditText textBox = ((android.widget.EditText) (dialog.findViewById(me.ccrama.redditslide.R.id.text_box)));
                        dialog.dismiss();
                        final java.lang.String s = "[".concat(textBox.getText().toString()).concat("](").concat(urlBox.getText().toString()).concat(")");
                        int start = java.lang.Math.max(editText.getSelectionStart(), 0);
                        int end = java.lang.Math.max(editText.getSelectionEnd(), 0);
                        editText.getText().insert(java.lang.Math.max(start, end), s);
                        // delete the selected text to avoid duplication
                        if (selectedTextNotEmpty) {
                            editText.getText().delete(start, end);
                        }
                    }
                }).build();
                // Tint the hint text if the base theme is Sepia
                if (me.ccrama.redditslide.SettingValues.currentTheme == 5) {
                    ((android.widget.EditText) (dialog.findViewById(me.ccrama.redditslide.R.id.url_box))).setHintTextColor(android.support.v4.content.ContextCompat.getColor(dialog.getContext(), me.ccrama.redditslide.R.color.md_grey_600));
                    ((android.widget.EditText) (dialog.findViewById(me.ccrama.redditslide.R.id.text_box))).setHintTextColor(android.support.v4.content.ContextCompat.getColor(dialog.getContext(), me.ccrama.redditslide.R.color.md_grey_600));
                }
                // use the selected text as the text for the link
                if (!selectedText.isEmpty()) {
                    ((android.widget.EditText) (dialog.findViewById(me.ccrama.redditslide.R.id.text_box))).setText(selectedText);
                }
                dialog.show();
            }
        });
        try {
            ((me.ccrama.redditslide.Views.ImageInsertEditText) (editText)).setImageSelectedCallback(new me.ccrama.redditslide.Views.ImageInsertEditText.ImageSelectedCallback() {
                @java.lang.Override
                public void onImageSelected(final android.net.Uri content, java.lang.String mimeType) {
                    me.ccrama.redditslide.Views.DoEditorActions.e = editText.getText();
                    me.ccrama.redditslide.Views.DoEditorActions.sStart = editText.getSelectionStart();
                    me.ccrama.redditslide.Views.DoEditorActions.sEnd = editText.getSelectionEnd();
                    me.ccrama.redditslide.Views.DoEditorActions.handleImageIntent(new java.util.ArrayList<android.net.Uri>() {
                        {
                            add(content);
                        }
                    }, editText, a);
                }
            });
        } catch (java.lang.Exception e) {
            // if thrown, there is likely an issue implementing this on the user's version of Android. There shouldn't be an issue, but just in case
        }
    }

    public static android.text.Editable e;

    public static int sStart;

    public static int sEnd;

    public static void doDraw(final android.app.Activity a, final android.widget.EditText editText, final android.support.v4.app.FragmentManager fm) {
        final android.content.Intent intent = new android.content.Intent(a, me.ccrama.redditslide.Activities.Draw.class);
        android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (editText.getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        me.ccrama.redditslide.Views.DoEditorActions.e = editText.getText();
        gun0912.tedbottompicker.TedBottomPicker tedBottomPicker = new gun0912.tedbottompicker.TedBottomPicker.Builder(editText.getContext()).setOnImageSelectedListener(new gun0912.tedbottompicker.TedBottomPicker.OnImageSelectedListener() {
            @java.lang.Override
            public void onImageSelected(java.util.List<android.net.Uri> uri) {
                me.ccrama.redditslide.Activities.Draw.uri = uri.get(0);
                android.support.v4.app.Fragment auxiliary = new me.ccrama.redditslide.Views.DoEditorActions.AuxiliaryFragment();
                me.ccrama.redditslide.Views.DoEditorActions.sStart = editText.getSelectionStart();
                me.ccrama.redditslide.Views.DoEditorActions.sEnd = editText.getSelectionEnd();
                fm.beginTransaction().add(auxiliary, "IMAGE_UPLOAD").commit();
                fm.executePendingTransactions();
                auxiliary.startActivityForResult(intent, 3333);
            }
        }).setLayoutResource(me.ccrama.redditslide.R.layout.image_sheet_dialog).setTitle("Choose a photo").create();
        tedBottomPicker.show(fm);
    }

    public static class AuxiliaryFragment extends android.support.v4.app.Fragment {
        @java.lang.Override
        public void onActivityResult(int requestCode, int resultCode, final android.content.Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if ((data != null) && (data.getData() != null)) {
                me.ccrama.redditslide.Views.DoEditorActions.handleImageIntent(new java.util.ArrayList<android.net.Uri>() {
                    {
                        add(data.getData());
                    }
                }, me.ccrama.redditslide.Views.DoEditorActions.e, getContext());
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        }
    }

    public static java.lang.String getImageLink(android.graphics.Bitmap b) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        b.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, baos);// Not sure whether this should be jpeg or png, try both and see which works best

        return android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT);
    }

    public static void insertBefore(java.lang.String wrapText, android.widget.EditText editText) {
        int start = java.lang.Math.max(editText.getSelectionStart(), 0);
        int end = java.lang.Math.max(editText.getSelectionEnd(), 0);
        editText.getText().insert(java.lang.Math.min(start, end), wrapText);
    }

    /* not using this method anywhere ¯\_(ツ)_/¯ */
    // public static void wrapNewline(String wrapText, EditText editText) {
    // int start = Math.max(editText.getSelectionStart(), 0);
    // int end = Math.max(editText.getSelectionEnd(), 0);
    // String s = editText.getText().toString().substring(Math.min(start, end), Math.max(start, end));
    // s = s.replace("\n", "\n" + wrapText);
    // editText.getText().replace(Math.min(start, end), Math.max(start, end), s);
    // }
    /**
     * Wrap selected text in one or multiple characters, handling newlines and spaces properly for markdown
     *
     * @param wrapText
     * 		Character(s) to wrap the selected text in
     * @param editText
     * 		EditText
     */
    public static void wrapString(java.lang.String wrapText, android.widget.EditText editText) {
        me.ccrama.redditslide.Views.DoEditorActions.wrapString(wrapText, wrapText, editText);
    }

    /**
     * Wrap selected text in one or multiple characters, handling newlines, spaces, >s properly for markdown,
     * with different start and end text.
     *
     * @param startWrap
     * 		Character(s) to start wrapping with
     * @param endWrap
     * 		Character(s) to close wrapping with
     * @param editText
     * 		EditText
     */
    public static void wrapString(java.lang.String startWrap, java.lang.String endWrap, android.widget.EditText editText) {
        int start = java.lang.Math.max(editText.getSelectionStart(), 0);
        int end = java.lang.Math.max(editText.getSelectionEnd(), 0);
        java.lang.String selected = editText.getText().toString().substring(java.lang.Math.min(start, end), java.lang.Math.max(start, end));
        // insert the wrapping character inside any selected spaces and >s because they stop markdown formatting
        // we use replaceFirst because anchors (\A, \Z) aren't consumed
        selected = selected.replaceFirst("\\A[\\n> ]*", "$0" + startWrap).replaceFirst("[\\n> ]*\\Z", endWrap + "$0");
        // 2+ newlines stop formatting, so we do the formatting on each instance of text surrounded by 2+ newlines
        /* in case anyone needs to understand this in the future:
        ([^\n> ]) captures any character that isn't a newline, >, or space
        (\n[> ]*){2,} captures any number of two or more newlines with any combination of spaces or >s since markdown ignores those by themselves
        (?=[^\n> ]) performs a lookahead and ensures there's a character that isn't a newline, >, or space
         */
        selected = selected.replaceAll("([^\\n> ])(\\n[> ]*){2,}(?=[^\\n> ])", (("$1" + endWrap) + "$2") + startWrap);
        editText.getText().replace(start, end, selected);
    }

    private static void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subredditName);
            firstTextView.setLinkTextColor(new me.ccrama.redditslide.ColorPreferences(firstTextView.getContext()).getColor(subredditName));
            startIndex = 1;
        } else {
            firstTextView.setText("");
            firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subredditName);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
        } else {
            commentOverflow.removeAllViews();
        }
    }

    private static class UploadImgur extends android.os.AsyncTask<android.net.Uri, java.lang.Integer, org.json.JSONObject> {
        final android.content.Context c;

        private final com.afollestad.materialdialogs.MaterialDialog dialog;

        public android.graphics.Bitmap b;

        public UploadImgur(android.content.Context c) {
            this.c = c;
            dialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(c).title(c.getString(me.ccrama.redditslide.R.string.editor_uploading_image)).progress(false, 100).cancelable(false).show();
        }

        // Following methods sourced from https://github.com/Kennyc1012/Opengur, Code by Kenny Campagna
        public static java.io.File createFile(android.net.Uri uri, @android.support.annotation.NonNull
        android.content.Context context) {
            java.io.InputStream in;
            android.content.ContentResolver resolver = context.getContentResolver();
            java.lang.String type = resolver.getType(uri);
            java.lang.String extension;
            if ("image/png".equals(type)) {
                extension = ".gif";
            } else if ("image/png".equals(type)) {
                extension = ".png";
            } else {
                extension = ".jpg";
            }
            try {
                in = resolver.openInputStream(uri);
            } catch (java.io.FileNotFoundException e) {
                return null;
            }
            // Create files from a uri in our cache directory so they eventually get deleted
            java.lang.String timeStamp = java.lang.String.valueOf(java.lang.System.currentTimeMillis());
            java.io.File cacheDir = ((me.ccrama.redditslide.Reddit) (context.getApplicationContext())).getImageLoader().getDiskCache().getDirectory();
            java.io.File tempFile = new java.io.File(cacheDir, timeStamp + extension);
            if (me.ccrama.redditslide.Views.DoEditorActions.UploadImgur.writeInputStreamToFile(in, tempFile)) {
                return tempFile;
            } else {
                // If writeInputStreamToFile fails, delete the excess file
                tempFile.delete();
            }
            return null;
        }

        public static boolean writeInputStreamToFile(@android.support.annotation.NonNull
        java.io.InputStream in, @android.support.annotation.NonNull
        java.io.File file) {
            java.io.BufferedOutputStream buffer = null;
            boolean didFinish = false;
            try {
                buffer = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file));
                byte[] byt = new byte[1024];
                int i;
                for (long l = 0L; (i = in.read(byt)) != (-1); l += i) {
                    buffer.write(byt, 0, i);
                }
                buffer.flush();
                didFinish = true;
            } catch (java.io.IOException e) {
                didFinish = false;
            } finally {
                me.ccrama.redditslide.Views.DoEditorActions.UploadImgur.closeStream(in);
                me.ccrama.redditslide.Views.DoEditorActions.UploadImgur.closeStream(buffer);
            }
            return didFinish;
        }

        public static void closeStream(@android.support.annotation.Nullable
        java.io.Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (java.io.IOException ex) {
                }
            }
        }

        // End methods sourced from Opengur
        @java.lang.Override
        protected org.json.JSONObject doInBackground(android.net.Uri... sub) {
            java.io.File bitmap = me.ccrama.redditslide.Views.DoEditorActions.UploadImgur.createFile(sub[0], c);
            final okhttp3.OkHttpClient client = me.ccrama.redditslide.Reddit.client;
            try {
                okhttp3.RequestBody formBody = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM).addFormDataPart("image", bitmap.getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), bitmap)).build();
                me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody body = new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody(formBody, new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody.Listener() {
                    @java.lang.Override
                    public void onProgress(int progress) {
                        publishProgress(progress);
                    }
                });
                okhttp3.Request request = new okhttp3.Request.Builder().header("Authorization", "Client-ID " + me.ccrama.redditslide.Constants.IMGUR_MASHAPE_CLIENT_ID).header("X-Mashape-Key", me.ccrama.redditslide.SecretConstants.getImgurApiKey(c)).url("https://imgur-apiv3.p.mashape.com/3/image").post(body).build();
                okhttp3.Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new java.io.IOException("Unexpected code " + response);

                return new org.json.JSONObject(response.body().string());
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @java.lang.Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @java.lang.Override
        protected void onPostExecute(final org.json.JSONObject result) {
            dialog.dismiss();
            try {
                int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.fontColor };
                android.content.res.TypedArray ta = c.obtainStyledAttributes(new me.ccrama.redditslide.ColorPreferences(c).getFontStyle().getBaseId(), attrs);
                final java.lang.String url = result.getJSONObject("data").getString("link");
                android.widget.LinearLayout layout = new android.widget.LinearLayout(c);
                layout.setOrientation(android.widget.LinearLayout.VERTICAL);
                final android.widget.TextView titleBox = new android.widget.TextView(c);
                titleBox.setText(url);
                layout.addView(titleBox);
                titleBox.setEnabled(false);
                titleBox.setTextColor(ta.getColor(0, android.graphics.Color.WHITE));
                final android.widget.EditText descriptionBox = new android.widget.EditText(c);
                descriptionBox.setHint(me.ccrama.redditslide.R.string.editor_title);
                descriptionBox.setEnabled(true);
                descriptionBox.setTextColor(ta.getColor(0, android.graphics.Color.WHITE));
                final android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (c.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY);
                if (me.ccrama.redditslide.Views.DoEditorActions.e != null) {
                    descriptionBox.setText(me.ccrama.redditslide.Views.DoEditorActions.e.toString().substring(me.ccrama.redditslide.Views.DoEditorActions.sStart, me.ccrama.redditslide.Views.DoEditorActions.sEnd));
                }
                ta.recycle();
                int sixteen = me.ccrama.redditslide.Reddit.dpToPxVertical(16);
                layout.setPadding(sixteen, sixteen, sixteen, sixteen);
                layout.addView(descriptionBox);
                new com.afollestad.materialdialogs.MaterialDialog.Builder(c).title(me.ccrama.redditslide.R.string.editor_title_link).customView(layout, false).positiveText(me.ccrama.redditslide.R.string.editor_action_link).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction which) {
                        dialog.dismiss();
                        java.lang.String s = ((("[" + descriptionBox.getText().toString()) + "](") + url) + ")";
                        if (descriptionBox.getText().toString().trim().isEmpty()) {
                            s = url + " ";
                        }
                        int start = java.lang.Math.max(me.ccrama.redditslide.Views.DoEditorActions.sStart, 0);
                        int end = java.lang.Math.max(me.ccrama.redditslide.Views.DoEditorActions.sEnd, 0);
                        if (me.ccrama.redditslide.Views.DoEditorActions.e != null) {
                            me.ccrama.redditslide.Views.DoEditorActions.e.insert(java.lang.Math.max(start, end), s);
                            me.ccrama.redditslide.Views.DoEditorActions.e.delete(start, end);
                            me.ccrama.redditslide.Views.DoEditorActions.e = null;
                        }
                        me.ccrama.redditslide.Views.DoEditorActions.sStart = 0;
                        me.ccrama.redditslide.Views.DoEditorActions.sEnd = 0;
                    }
                }).canceledOnTouchOutside(false).show();
            } catch (java.lang.Exception e) {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.editor_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                    }
                }).show();
                e.printStackTrace();
            }
        }

        @java.lang.Override
        protected void onProgressUpdate(java.lang.Integer... values) {
            dialog.setProgress(values[0]);
            me.ccrama.redditslide.util.LogUtil.v("Progress:" + values[0]);
        }
    }

    private static class UploadImgurAlbum extends android.os.AsyncTask<android.net.Uri, java.lang.Integer, java.lang.String> {
        final android.content.Context c;

        private final com.afollestad.materialdialogs.MaterialDialog dialog;

        public android.graphics.Bitmap b;

        public UploadImgurAlbum(android.content.Context c) {
            this.c = c;
            dialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(c).title(c.getString(me.ccrama.redditslide.R.string.editor_uploading_image)).progress(false, 100).cancelable(false).show();
        }

        // Following methods sourced from https://github.com/Kennyc1012/Opengur, Code by Kenny Campagna
        public static java.io.File createFile(android.net.Uri uri, @android.support.annotation.NonNull
        android.content.Context context) {
            java.io.InputStream in;
            android.content.ContentResolver resolver = context.getContentResolver();
            java.lang.String type = resolver.getType(uri);
            java.lang.String extension;
            if ("image/png".equals(type)) {
                extension = ".gif";
            } else if ("image/png".equals(type)) {
                extension = ".png";
            } else {
                extension = ".jpg";
            }
            try {
                in = resolver.openInputStream(uri);
            } catch (java.io.FileNotFoundException e) {
                return null;
            }
            // Create files from a uri in our cache directory so they eventually get deleted
            java.lang.String timeStamp = java.lang.String.valueOf(java.lang.System.currentTimeMillis());
            java.io.File cacheDir = ((me.ccrama.redditslide.Reddit) (context.getApplicationContext())).getImageLoader().getDiskCache().getDirectory();
            java.io.File tempFile = new java.io.File(cacheDir, timeStamp + extension);
            if (me.ccrama.redditslide.Views.DoEditorActions.UploadImgurAlbum.writeInputStreamToFile(in, tempFile)) {
                return tempFile;
            } else {
                // If writeInputStreamToFile fails, delete the excess file
                tempFile.delete();
            }
            return null;
        }

        public static boolean writeInputStreamToFile(@android.support.annotation.NonNull
        java.io.InputStream in, @android.support.annotation.NonNull
        java.io.File file) {
            java.io.BufferedOutputStream buffer = null;
            boolean didFinish = false;
            try {
                buffer = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file));
                byte[] byt = new byte[1024];
                int i;
                for (long l = 0L; (i = in.read(byt)) != (-1); l += i) {
                    buffer.write(byt, 0, i);
                }
                buffer.flush();
                didFinish = true;
            } catch (java.io.IOException e) {
                didFinish = false;
            } finally {
                me.ccrama.redditslide.Views.DoEditorActions.UploadImgurAlbum.closeStream(in);
                me.ccrama.redditslide.Views.DoEditorActions.UploadImgurAlbum.closeStream(buffer);
            }
            return didFinish;
        }

        public static void closeStream(@android.support.annotation.Nullable
        java.io.Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (java.io.IOException ex) {
                }
            }
        }

        // End methods sourced from Opengur
        java.lang.String finalUrl;

        @java.lang.Override
        protected java.lang.String doInBackground(android.net.Uri... sub) {
            totalCount = sub.length;
            final okhttp3.OkHttpClient client = me.ccrama.redditslide.Reddit.client;
            java.lang.String albumurl;
            {
                okhttp3.Request request = new okhttp3.Request.Builder().header("Authorization", "Client-ID " + me.ccrama.redditslide.Constants.IMGUR_MASHAPE_CLIENT_ID).header("X-Mashape-Key", me.ccrama.redditslide.SecretConstants.getImgurApiKey(c)).url("https://imgur-apiv3.p.mashape.com/3/album").post(new okhttp3.RequestBody() {
                    @java.lang.Override
                    public okhttp3.MediaType contentType() {
                        return null;
                    }

                    @java.lang.Override
                    public void writeTo(okio.BufferedSink sink) {
                    }
                }).build();
                okhttp3.Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new java.io.IOException("Unexpected code " + response);
                    }
                    org.json.JSONObject album = new org.json.JSONObject(response.body().string());
                    albumurl = album.getJSONObject("data").getString("deletehash");
                    finalUrl = "http://imgur.com/a/" + album.getJSONObject("data").getString("id");
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            try {
                okhttp3.MultipartBody.Builder formBodyBuilder = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);
                for (android.net.Uri uri : sub) {
                    java.io.File bitmap = me.ccrama.redditslide.Views.DoEditorActions.UploadImgurAlbum.createFile(uri, c);
                    formBodyBuilder.addFormDataPart("image", bitmap.getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), bitmap));
                    formBodyBuilder.addFormDataPart("album", albumurl);
                    okhttp3.MultipartBody formBody = formBodyBuilder.build();
                    me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody body = new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody(formBody, new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody.Listener() {
                        @java.lang.Override
                        public void onProgress(int progress) {
                            publishProgress(progress);
                        }
                    });
                    okhttp3.Request request = new okhttp3.Request.Builder().header("Authorization", "Client-ID " + me.ccrama.redditslide.Constants.IMGUR_MASHAPE_CLIENT_ID).header("X-Mashape-Key", me.ccrama.redditslide.SecretConstants.getImgurApiKey(c)).url("https://imgur-apiv3.p.mashape.com/3/image").post(body).build();
                    okhttp3.Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new java.io.IOException("Unexpected code " + response);
                    }
                }
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @java.lang.Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @java.lang.Override
        protected void onPostExecute(final java.lang.String result) {
            dialog.dismiss();
            try {
                int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.fontColor };
                android.content.res.TypedArray ta = c.obtainStyledAttributes(new me.ccrama.redditslide.ColorPreferences(c).getFontStyle().getBaseId(), attrs);
                android.widget.LinearLayout layout = new android.widget.LinearLayout(c);
                layout.setOrientation(android.widget.LinearLayout.VERTICAL);
                final android.widget.TextView titleBox = new android.widget.TextView(c);
                titleBox.setText(finalUrl);
                layout.addView(titleBox);
                titleBox.setEnabled(false);
                titleBox.setTextColor(ta.getColor(0, android.graphics.Color.WHITE));
                final android.widget.EditText descriptionBox = new android.widget.EditText(c);
                descriptionBox.setHint(me.ccrama.redditslide.R.string.editor_title);
                descriptionBox.setEnabled(true);
                descriptionBox.setTextColor(ta.getColor(0, android.graphics.Color.WHITE));
                if (me.ccrama.redditslide.Views.DoEditorActions.e != null) {
                    descriptionBox.setText(me.ccrama.redditslide.Views.DoEditorActions.e.toString().substring(me.ccrama.redditslide.Views.DoEditorActions.sStart, me.ccrama.redditslide.Views.DoEditorActions.sEnd));
                }
                ta.recycle();
                int sixteen = me.ccrama.redditslide.Reddit.dpToPxVertical(16);
                layout.setPadding(sixteen, sixteen, sixteen, sixteen);
                layout.addView(descriptionBox);
                new com.afollestad.materialdialogs.MaterialDialog.Builder(c).title(me.ccrama.redditslide.R.string.editor_title_link).customView(layout, false).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                        dialog.dismiss();
                        java.lang.String s = ((("[" + descriptionBox.getText().toString()) + "](") + finalUrl) + ")";
                        int start = java.lang.Math.max(me.ccrama.redditslide.Views.DoEditorActions.sStart, 0);
                        int end = java.lang.Math.max(me.ccrama.redditslide.Views.DoEditorActions.sEnd, 0);
                        me.ccrama.redditslide.Views.DoEditorActions.e.insert(java.lang.Math.max(start, end), s);
                        me.ccrama.redditslide.Views.DoEditorActions.e.delete(start, end);
                        me.ccrama.redditslide.Views.DoEditorActions.e = null;
                        me.ccrama.redditslide.Views.DoEditorActions.sStart = 0;
                        me.ccrama.redditslide.Views.DoEditorActions.sEnd = 0;
                    }
                }).positiveText(me.ccrama.redditslide.R.string.editor_action_link).canceledOnTouchOutside(false).show();
            } catch (java.lang.Exception e) {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.editor_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                    }
                }).show();
                e.printStackTrace();
            }
        }

        int uploadCount;

        int totalCount;

        @java.lang.Override
        protected void onProgressUpdate(java.lang.Integer... values) {
            int progress = values[0];
            if ((progress < dialog.getCurrentProgress()) || (uploadCount == 0)) {
                uploadCount += 1;
            }
            dialog.setContent((("Image " + uploadCount) + "/") + totalCount);
            dialog.setProgress(progress);
        }
    }

    public static void handleImageIntent(java.util.List<android.net.Uri> uris, android.widget.EditText ed, android.content.Context c) {
        me.ccrama.redditslide.Views.DoEditorActions.handleImageIntent(uris, ed.getText(), c);
    }

    public static void handleImageIntent(java.util.List<android.net.Uri> uris, android.text.Editable ed, android.content.Context c) {
        if (uris.size() == 1) {
            // Get the Image from data (single image)
            try {
                new me.ccrama.redditslide.Views.DoEditorActions.UploadImgur(c).execute(uris.get(0));
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        } else {
            // Multiple images
            try {
                new me.ccrama.redditslide.Views.DoEditorActions.UploadImgurAlbum(c).execute(uris.toArray(new android.net.Uri[uris.size()]));
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ProgressRequestBody extends okhttp3.RequestBody {
        protected okhttp3.RequestBody mDelegate;

        protected me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody.Listener mListener;

        protected me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody.CountingSink mCountingSink;

        public ProgressRequestBody(okhttp3.RequestBody delegate, me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody.Listener listener) {
            mDelegate = delegate;
            mListener = listener;
        }

        @java.lang.Override
        public okhttp3.MediaType contentType() {
            return mDelegate.contentType();
        }

        @java.lang.Override
        public long contentLength() {
            try {
                return mDelegate.contentLength();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @java.lang.Override
        public void writeTo(okio.BufferedSink sink) throws java.io.IOException {
            mCountingSink = new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody.CountingSink(sink);
            okio.BufferedSink bufferedSink = okio.Okio.buffer(mCountingSink);
            mDelegate.writeTo(bufferedSink);
            bufferedSink.flush();
        }

        protected final class CountingSink extends okio.ForwardingSink {
            private long bytesWritten = 0;

            public CountingSink(okio.Sink delegate) {
                super(delegate);
            }

            @java.lang.Override
            public void write(okio.Buffer source, long byteCount) throws java.io.IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                mListener.onProgress(((int) ((100.0F * bytesWritten) / contentLength())));
            }
        }

        public interface Listener {
            void onProgress(int progress);
        }
    }
}