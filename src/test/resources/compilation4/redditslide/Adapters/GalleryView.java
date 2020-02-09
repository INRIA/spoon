package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Activities.CommentsScreen;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Activities.Tumblr;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.MediaView;
import me.ccrama.redditslide.Activities.AlbumPager;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Activities.Album;
import me.ccrama.redditslide.Activities.Gallery;
import me.ccrama.redditslide.Activities.TumblrPager;
import me.ccrama.redditslide.Activities.FullscreenVideo;
public class GalleryView extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> {
    private final me.ccrama.redditslide.Activities.Gallery main;

    public boolean paddingBottom;

    public java.util.ArrayList<net.dean.jraw.models.Submission> posts;

    public java.lang.String subreddit;

    public GalleryView(final me.ccrama.redditslide.Activities.Gallery context, java.util.ArrayList<net.dean.jraw.models.Submission> displayer, java.lang.String subreddit) {
        main = context;
        this.posts = displayer;
        this.subreddit = subreddit;
    }

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(me.ccrama.redditslide.R.layout.gallery_image, parent, false);
        return new me.ccrama.redditslide.Adapters.GalleryView.AlbumViewHolder(v);
    }

    public double getHeightFromAspectRatio(int imageHeight, int imageWidth, int viewWidth) {
        double ratio = ((double) (imageHeight)) / ((double) (imageWidth));
        return viewWidth * ratio;
    }

    @java.lang.Override
    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder2, final int i) {
        if (holder2 instanceof me.ccrama.redditslide.Adapters.GalleryView.AlbumViewHolder) {
            final me.ccrama.redditslide.Adapters.GalleryView.AlbumViewHolder holder = ((me.ccrama.redditslide.Adapters.GalleryView.AlbumViewHolder) (holder2));
            final net.dean.jraw.models.Submission submission = posts.get(i);
            if ((submission.getThumbnails() != null) && (submission.getThumbnails().getSource() != null)) {
                ((me.ccrama.redditslide.Reddit) (main.getApplicationContext())).getImageLoader().displayImage(submission.getThumbnails().getSource().getUrl(), holder.image, me.ccrama.redditslide.Adapters.ImageGridAdapter.options);
            } else {
                ((me.ccrama.redditslide.Reddit) (main.getApplicationContext())).getImageLoader().displayImage(submission.getUrl(), holder.image, me.ccrama.redditslide.Adapters.ImageGridAdapter.options);
            }
            double h = 0;
            int height = 0;
            if (submission.getThumbnails() != null) {
                net.dean.jraw.models.Thumbnails.Image source = submission.getThumbnails().getSource();
                if (source != null) {
                    h = getHeightFromAspectRatio(source.getHeight(), source.getWidth(), holder.image.getWidth());
                    height = source.getHeight();
                }
            }
            holder.type.setVisibility(android.view.View.VISIBLE);
            switch (me.ccrama.redditslide.ContentType.getContentType(submission)) {
                case ALBUM :
                    holder.type.setImageResource(me.ccrama.redditslide.R.drawable.album);
                    break;
                case EXTERNAL :
                case LINK :
                case REDDIT :
                    holder.type.setImageResource(me.ccrama.redditslide.R.drawable.world);
                    break;
                case SELF :
                    holder.type.setImageResource(me.ccrama.redditslide.R.drawable.fontsizedarker);
                    break;
                case EMBEDDED :
                case GIF :
                case STREAMABLE :
                case VIDEO :
                case VID_ME :
                    holder.type.setImageResource(me.ccrama.redditslide.R.drawable.play);
                    break;
                default :
                    holder.type.setVisibility(android.view.View.GONE);
                    break;
            }
            if (h != 0) {
                if (h > 3200) {
                    holder.image.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 3200));
                } else {
                    holder.image.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, ((int) (h))));
                }
            } else if (height > 3200) {
                holder.image.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 3200));
            } else {
                holder.image.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT));
            }
            holder.comments.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
                    android.content.Intent i2 = new android.content.Intent(main, me.ccrama.redditslide.Activities.CommentsScreen.class);
                    i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_PAGE, main.subredditPosts.getPosts().indexOf(submission));
                    i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_SUBREDDIT, subreddit);
                    i2.putExtra("fullname", submission.getFullName());
                    main.startActivity(i2);
                }
            });
            holder.image.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View v) {
                    if (main != null) {
                        com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(main).title(submission.getUrl()).grid();
                        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
                        android.content.res.TypedArray ta = main.obtainStyledAttributes(attrs);
                        int color = ta.getColor(0, android.graphics.Color.WHITE);
                        android.graphics.drawable.Drawable open = main.getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_open_in_browser);
                        open.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                        android.graphics.drawable.Drawable share = main.getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_share);
                        share.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                        android.graphics.drawable.Drawable copy = main.getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_content_copy);
                        copy.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                        ta.recycle();
                        b.sheet(me.ccrama.redditslide.R.id.open_link, open, main.getResources().getString(me.ccrama.redditslide.R.string.submission_link_extern));
                        b.sheet(me.ccrama.redditslide.R.id.share_link, share, main.getResources().getString(me.ccrama.redditslide.R.string.share_link));
                        b.sheet(me.ccrama.redditslide.R.id.copy_link, copy, main.getResources().getString(me.ccrama.redditslide.R.string.submission_link_copy));
                        b.listener(new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                switch (which) {
                                    case me.ccrama.redditslide.R.id.open_link :
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                        break;
                                    case me.ccrama.redditslide.R.id.share_link :
                                        me.ccrama.redditslide.Reddit.defaultShareText("", submission.getUrl(), main);
                                        break;
                                    case me.ccrama.redditslide.R.id.copy_link :
                                        me.ccrama.redditslide.util.LinkUtil.copyUrl(submission.getUrl(), main);
                                        break;
                                }
                            }
                        }).show();
                        return true;
                    }
                    return true;
                }
            });
            holder.image.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(submission);
                    if ((!me.ccrama.redditslide.PostMatch.openExternal(submission.getUrl())) || (type == me.ccrama.redditslide.ContentType.Type.VIDEO)) {
                        switch (type) {
                            case VID_ME :
                            case STREAMABLE :
                                if (me.ccrama.redditslide.SettingValues.video) {
                                    android.content.Intent myIntent = new android.content.Intent(main, me.ccrama.redditslide.Activities.MediaView.class);
                                    myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, subreddit);
                                    myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, submission.getUrl());
                                    main.startActivity(myIntent);
                                } else {
                                    me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                }
                                break;
                            case IMGUR :
                                me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openImage(type, main, submission, null, holder.getAdapterPosition());
                                break;
                            case EMBEDDED :
                                if (me.ccrama.redditslide.SettingValues.video) {
                                    java.lang.String data = android.text.Html.fromHtml(submission.getDataNode().get("media_embed").get("content").asText()).toString();
                                    {
                                        android.content.Intent i = new android.content.Intent(main, me.ccrama.redditslide.Activities.FullscreenVideo.class);
                                        i.putExtra(me.ccrama.redditslide.Activities.FullscreenVideo.EXTRA_HTML, data);
                                        main.startActivity(i);
                                    }
                                } else {
                                    me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                }
                                break;
                            case REDDIT :
                                me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openRedditContent(submission.getUrl(), main);
                                break;
                            case LINK :
                                me.ccrama.redditslide.util.LinkUtil.openUrl(submission.getUrl(), me.ccrama.redditslide.Visuals.Palette.getColor(submission.getSubredditName()), main);
                                break;
                            case ALBUM :
                                if (me.ccrama.redditslide.SettingValues.album) {
                                    if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                                        android.content.Intent i = new android.content.Intent(main, me.ccrama.redditslide.Activities.AlbumPager.class);
                                        i.putExtra(me.ccrama.redditslide.Activities.AlbumPager.SUBREDDIT, subreddit);
                                        i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, submission.getUrl());
                                        main.startActivity(i);
                                    } else {
                                        android.content.Intent i = new android.content.Intent(main, me.ccrama.redditslide.Activities.Album.class);
                                        i.putExtra(me.ccrama.redditslide.Activities.Album.SUBREDDIT, subreddit);
                                        i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, submission.getUrl());
                                        main.startActivity(i);
                                    }
                                } else {
                                    me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                }
                                break;
                            case TUMBLR :
                                if (me.ccrama.redditslide.SettingValues.image) {
                                    if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                                        android.content.Intent i = new android.content.Intent(main, me.ccrama.redditslide.Activities.TumblrPager.class);
                                        i.putExtra(me.ccrama.redditslide.Activities.TumblrPager.SUBREDDIT, subreddit);
                                        i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, submission.getUrl());
                                        main.startActivity(i);
                                    } else {
                                        android.content.Intent i = new android.content.Intent(main, me.ccrama.redditslide.Activities.Tumblr.class);
                                        i.putExtra(me.ccrama.redditslide.Activities.Tumblr.SUBREDDIT, subreddit);
                                        i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, submission.getUrl());
                                        main.startActivity(i);
                                    }
                                } else {
                                    me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                }
                                break;
                            case DEVIANTART :
                            case XKCD :
                            case IMAGE :
                                me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openImage(type, main, submission, null, holder.getAdapterPosition());
                                break;
                            case GIF :
                                me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openGif(main, submission, holder.getAdapterPosition());
                                break;
                            case NONE :
                            case SELF :
                                holder.comments.callOnClick();
                                break;
                            case VIDEO :
                                if (!me.ccrama.redditslide.util.LinkUtil.tryOpenWithVideoPlugin(submission.getUrl())) {
                                    me.ccrama.redditslide.util.LinkUtil.openUrl(submission.getUrl(), me.ccrama.redditslide.Visuals.Palette.getStatusBarColor(), main);
                                }
                                break;
                        }
                    } else {
                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                    }
                }
            });
        }
    }

    @java.lang.Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SpacerViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    public static class AlbumViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        final android.widget.ImageView image;

        final android.widget.ImageView type;

        final android.view.View comments;

        public AlbumViewHolder(android.view.View itemView) {
            super(itemView);
            comments = itemView.findViewById(me.ccrama.redditslide.R.id.comments);
            image = itemView.findViewById(me.ccrama.redditslide.R.id.image);
            type = itemView.findViewById(me.ccrama.redditslide.R.id.type);
        }
    }
}