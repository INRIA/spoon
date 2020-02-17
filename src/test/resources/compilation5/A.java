/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import java.util.LinkedList;
import java.util.Locale;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import org.apache.commons.lang3.StringUtils;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Views.RoundedBackgroundSpan;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.UserTags;
public class A extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> implements android.widget.Filterable {
    private final android.content.Context mContext;

    private final java.util.List<net.dean.jraw.models.CommentNode> originalDataSet;

    private java.lang.String search = "";

    // /... other methods
    private java.util.List<net.dean.jraw.models.CommentNode> dataSet;

    public A(android.content.Context mContext, java.util.List<net.dean.jraw.models.CommentNode> dataSet) {
        this.mContext = mContext;
        this.originalDataSet = dataSet;
    }

    public void setResult(java.lang.String result) {
        search = result;
    }

    @java.lang.Override
    public android.widget.Filter getFilter() {
        return new me.ccrama.redditslide.Adapters.A.UserFilter(this, originalDataSet);
    }

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup viewGroup, int i) {
        android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.comment, viewGroup, false);
        return new me.ccrama.redditslide.Adapters.CommentViewHolder(v);
    }

    public void doScoreText(me.ccrama.redditslide.Adapters.CommentViewHolder holder, net.dean.jraw.models.Comment comment, int offset) {
        java.lang.String spacer = (" " + mContext.getString(me.ccrama.redditslide.R.string.submission_properties_seperator_comments)) + " ";
        android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
        android.text.SpannableStringBuilder author = new android.text.SpannableStringBuilder(comment.getAuthor());
        final int authorcolor = me.ccrama.redditslide.Visuals.Palette.getFontColorUser(comment.getAuthor());
        author.setSpan(new android.text.style.TypefaceSpan("sans-serif-condensed"), 0, author.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        author.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, author.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (comment.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.ADMIN) {
            author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
            author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_red_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (comment.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.SPECIAL) {
            author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
            author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_red_500, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (comment.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.MODERATOR) {
            author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
            author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_green_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if ((me.ccrama.redditslide.Authentication.name != null) && comment.getAuthor().toLowerCase(java.util.Locale.ENGLISH).equals(me.ccrama.redditslide.Authentication.name.toLowerCase(java.util.Locale.ENGLISH))) {
            author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
            author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (authorcolor != 0) {
            author.setSpan(new android.text.style.ForegroundColorSpan(authorcolor), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        titleString.append(author);
        titleString.append(spacer);
        java.lang.String scoreText;
        if (comment.isScoreHidden()) {
            scoreText = ("[" + mContext.getString(me.ccrama.redditslide.R.string.misc_score_hidden).toUpperCase()) + "]";
        } else {
            scoreText = java.lang.String.format(java.util.Locale.getDefault(), "%d", comment.getScore() + offset);
        }
        android.text.SpannableStringBuilder score = new android.text.SpannableStringBuilder(scoreText);
        titleString.append(score);
        if (!scoreText.contains("[")) {
            titleString.append(mContext.getResources().getQuantityString(me.ccrama.redditslide.R.plurals.points, comment.getScore()));
        }
        titleString.append(comment.isControversial() ? " †" : "");
        titleString.append(spacer);
        java.lang.String timeAgo = me.ccrama.redditslide.TimeUtils.getTimeAgo(comment.getCreated().getTime(), mContext);
        titleString.append((timeAgo == null) || timeAgo.isEmpty() ? "just now" : timeAgo);// some users were crashing here

        titleString.append(comment.getEditDate() != null ? (" (edit " + me.ccrama.redditslide.TimeUtils.getTimeAgo(comment.getEditDate().getTime(), mContext)) + ")" : "");
        titleString.append("  ");
        if (comment.getDataNode().get("stickied").asBoolean()) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + mContext.getString(me.ccrama.redditslide.R.string.submission_stickied).toUpperCase()) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_green_300, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
            titleString.append(" ");
        }
        if (me.ccrama.redditslide.UserTags.isUserTagged(comment.getAuthor())) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + me.ccrama.redditslide.UserTags.getUserTag(comment.getAuthor())) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_blue_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
            titleString.append(" ");
        }
        if (((comment.getTimesSilvered() > 0) || (comment.getTimesGilded() > 0)) || (comment.getTimesPlatinized() > 0)) {
            android.content.res.TypedArray a = mContext.obtainStyledAttributes(new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getPostFontStyle().getResId(), me.ccrama.redditslide.R.styleable.FontStyle);
            int fontsize = ((int) (a.getDimensionPixelSize(me.ccrama.redditslide.R.styleable.FontStyle_font_cardtitle, -1) * 0.75));
            a.recycle();
            // Add silver, gold, platinum icons and counts in that order
            if (comment.getTimesSilvered() > 0) {
                final java.lang.String timesSilvered = (comment.getTimesSilvered() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesSilvered());
                android.text.SpannableStringBuilder silvered = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesSilvered) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.silver);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                silvered.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                silvered.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, silvered.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(silvered);
                titleString.append(" ");
            }
            if (comment.getTimesGilded() > 0) {
                final java.lang.String timesGilded = (comment.getTimesGilded() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesGilded());
                android.text.SpannableStringBuilder gilded = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesGilded) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.gold);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                gilded.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                gilded.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, gilded.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(gilded);
                titleString.append(" ");
            }
            if (comment.getTimesPlatinized() > 0) {
                final java.lang.String timesPlatinized = (comment.getTimesPlatinized() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesPlatinized());
                android.text.SpannableStringBuilder platinized = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesPlatinized) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.platinum);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                platinized.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                platinized.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, platinized.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(platinized);
                titleString.append(" ");
            }
        }
        if (me.ccrama.redditslide.UserSubscriptions.friends.contains(comment.getAuthor())) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + mContext.getString(me.ccrama.redditslide.R.string.profile_friend)) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
            titleString.append(" ");
        }
        if (((comment.getAuthorFlair() != null) && (comment.getAuthorFlair().getText() != null)) && (!comment.getAuthorFlair().getText().isEmpty())) {
            android.util.TypedValue typedValue = new android.util.TypedValue();
            android.content.res.Resources.Theme theme = mContext.getTheme();
            theme.resolveAttribute(me.ccrama.redditslide.R.attr.activity_background, typedValue, true);
            int color = typedValue.data;
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + android.text.Html.fromHtml(comment.getAuthorFlair().getText())) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(holder.firstTextView.getCurrentTextColor(), color, false, mContext), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
            titleString.append(" ");
        }
        holder.content.setText(titleString);
    }

    @java.lang.Override
    public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder firstHolder, int pos) {
        final me.ccrama.redditslide.Adapters.CommentViewHolder holder = ((me.ccrama.redditslide.Adapters.CommentViewHolder) (firstHolder));
        final net.dean.jraw.models.CommentNode baseNode = dataSet.get(pos);
        final net.dean.jraw.models.Comment comment = baseNode.getComment();
        doScoreText(holder, comment, 0);
        if (baseNode.isTopLevel()) {
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.next).setVisibility(android.view.View.VISIBLE);
        } else {
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.next).setVisibility(android.view.View.GONE);
        }
        java.lang.String body = comment.getDataNode().get("body_html").asText();
        if ((!search.isEmpty()) && org.apache.commons.lang3.StringUtils.isAlphanumericSpace(search)) {
            body = body.replaceAll(search, ("[[h[" + search) + "]h]]");
        }
        int type = new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getFontTypeComment().getTypeface();
        android.graphics.Typeface typeface;
        if (type >= 0) {
            typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(mContext, type);
        } else {
            typeface = android.graphics.Typeface.DEFAULT;
        }
        holder.firstTextView.setTypeface(typeface);
        setViews(body, comment.getSubredditName(), holder);
        holder.childrenNumber.setVisibility(android.view.View.GONE);
        holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.os.Bundle conData = new android.os.Bundle();
                conData.putString("fullname", comment.getFullName());
                android.content.Intent intent = new android.content.Intent();
                intent.putExtras(conData);
                ((android.app.Activity) (mContext)).setResult(android.app.Activity.RESULT_OK, intent);
                ((android.app.Activity) (mContext)).finish();
            }
        });
        holder.firstTextView.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.os.Bundle conData = new android.os.Bundle();
                conData.putString("fullname", comment.getFullName());
                android.content.Intent intent = new android.content.Intent();
                intent.putExtras(conData);
                ((android.app.Activity) (mContext)).setResult(android.app.Activity.RESULT_OK, intent);
                ((android.app.Activity) (mContext)).finish();
            }
        });
        holder.itemView.findViewById(me.ccrama.redditslide.R.id.dot).setVisibility(android.view.View.VISIBLE);
        if ((baseNode.getDepth() - 1) > 0) {
            android.view.View v = holder.itemView.findViewById(me.ccrama.redditslide.R.id.dot);
            int i22 = baseNode.getDepth() - 2;
            if ((i22 % 5) == 0) {
                holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_500));
            } else if ((i22 % 4) == 0) {
                holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_green_500));
            } else if ((i22 % 3) == 0) {
                holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_yellow_500));
            } else if ((i22 % 2) == 0) {
                holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_500));
            } else {
                holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_red_500));
            }
        } else {
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.dot).setVisibility(android.view.View.GONE);
        }
    }

    /**
     * Set the text for the corresponding views
     *
     * @param rawHTML
     *
     * @param subredditName
     *
     * @param holder
     *
     */
    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.Adapters.CommentViewHolder holder) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            holder.firstTextView.setVisibility(android.view.View.VISIBLE);
            holder.firstTextView.setTextHtml(blocks.get(0), subredditName);
            startIndex = 1;
        } else {
            holder.firstTextView.setText("");
            holder.firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                holder.commentOverflow.setViews(blocks, subredditName);
            } else {
                holder.commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
        } else {
            holder.commentOverflow.removeAllViews();
        }
    }

    @java.lang.Override
    public int getItemCount() {
        if (dataSet == null) {
            return 0;
        }
        return dataSet.size();
    }

    private class UserFilter extends android.widget.Filter {
        private final me.ccrama.redditslide.Adapters.A adapter;

        private final java.util.List<net.dean.jraw.models.CommentNode> originalList;

        private final java.util.List<net.dean.jraw.models.CommentNode> filteredList;

        private UserFilter(me.ccrama.redditslide.Adapters.A adapter, java.util.List<net.dean.jraw.models.CommentNode> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new java.util.LinkedList<>(originalList);
            this.filteredList = new java.util.ArrayList<>();
        }

        @java.lang.Override
        protected android.widget.Filter.FilterResults performFiltering(java.lang.CharSequence constraint) {
            filteredList.clear();
            final android.widget.Filter.FilterResults results = new android.widget.Filter.FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final java.lang.String filterPattern = constraint.toString().toLowerCase(java.util.Locale.ENGLISH).trim();
                for (final net.dean.jraw.models.CommentNode user : originalList) {
                    if (org.apache.commons.text.StringEscapeUtils.unescapeHtml4(user.getComment().getBody().toLowerCase(java.util.Locale.ENGLISH)).contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @java.lang.Override
        protected void publishResults(java.lang.CharSequence constraint, android.widget.Filter.FilterResults results) {
            adapter.dataSet = new java.util.ArrayList<>();
            adapter.dataSet.addAll(((java.util.ArrayList<net.dean.jraw.models.CommentNode>) (results.values)));
            adapter.notifyDataSetChanged();
        }
    }
}