package me.ccrama.redditslide.Adapters;
import java.net.MalformedURLException;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.ImgurAlbum.Image;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.MediaView;
import java.net.URL;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Activities.Album;
import java.util.List;
public class AlbumView extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> {
    private final java.util.List<me.ccrama.redditslide.ImgurAlbum.Image> users;

    private final android.app.Activity main;

    public boolean paddingBottom;

    public int height;

    public java.lang.String subreddit;

    public AlbumView(final android.app.Activity context, final java.util.List<me.ccrama.redditslide.ImgurAlbum.Image> users, int height, java.lang.String subreddit) {
        this.height = height;
        main = context;
        this.users = users;
        this.subreddit = subreddit;
        paddingBottom = main.findViewById(me.ccrama.redditslide.R.id.toolbar) == null;
        if (context.findViewById(me.ccrama.redditslide.R.id.grid) != null)
            context.findViewById(me.ccrama.redditslide.R.id.grid).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.view.LayoutInflater l = context.getLayoutInflater();
                    android.view.View body = l.inflate(me.ccrama.redditslide.R.layout.album_grid_dialog, null, false);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
                    android.widget.GridView gridview = body.findViewById(me.ccrama.redditslide.R.id.images);
                    gridview.setAdapter(new me.ccrama.redditslide.Adapters.ImageGridAdapter(context, users));
                    b.setView(body);
                    final android.app.Dialog d = b.create();
                    gridview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                        public void onItemClick(android.widget.AdapterView<?> parent, android.view.View v, int position, long id) {
                            if (context instanceof me.ccrama.redditslide.Activities.Album) {
                                ((android.support.v7.widget.LinearLayoutManager) (((me.ccrama.redditslide.Activities.Album) (context)).album.album.recyclerView.getLayoutManager())).scrollToPositionWithOffset(position + 1, context.findViewById(me.ccrama.redditslide.R.id.toolbar).getHeight());
                            } else {
                                ((android.support.v7.widget.LinearLayoutManager) (((android.support.v7.widget.RecyclerView) (context.findViewById(me.ccrama.redditslide.R.id.images))).getLayoutManager())).scrollToPositionWithOffset(position + 1, context.findViewById(me.ccrama.redditslide.R.id.toolbar).getHeight());
                            }
                            d.dismiss();
                        }
                    });
                    d.show();
                }
            });

    }

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        if (viewType == 1) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(me.ccrama.redditslide.R.layout.album_image, parent, false);
            return new me.ccrama.redditslide.Adapters.AlbumView.AlbumViewHolder(v);
        } else {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, parent, false);
            return new me.ccrama.redditslide.Adapters.AlbumView.SpacerViewHolder(v);
        }
    }

    public double getHeightFromAspectRatio(int imageHeight, int imageWidth, int viewWidth) {
        double ratio = ((double) (imageHeight)) / ((double) (imageWidth));
        return viewWidth * ratio;
    }

    @java.lang.Override
    public int getItemViewType(int position) {
        int SPACER = 6;
        if ((!paddingBottom) && (position == 0)) {
            return SPACER;
        } else if (paddingBottom && (position == (getItemCount() - 1))) {
            return SPACER;
        } else {
            return 1;
        }
    }

    @java.lang.Override
    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder2, int i) {
        if (holder2 instanceof me.ccrama.redditslide.Adapters.AlbumView.AlbumViewHolder) {
            final int position = (paddingBottom) ? i : i - 1;
            me.ccrama.redditslide.Adapters.AlbumView.AlbumViewHolder holder = ((me.ccrama.redditslide.Adapters.AlbumView.AlbumViewHolder) (holder2));
            final me.ccrama.redditslide.ImgurAlbum.Image user = users.get(position);
            ((me.ccrama.redditslide.Reddit) (main.getApplicationContext())).getImageLoader().displayImage(user.getImageUrl(), holder.image, me.ccrama.redditslide.Adapters.ImageGridAdapter.options);
            holder.body.setVisibility(android.view.View.VISIBLE);
            holder.text.setVisibility(android.view.View.VISIBLE);
            android.view.View imageView = holder.image;
            if (imageView.getWidth() == 0) {
                holder.image.setLayoutParams(new android.widget.LinearLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT));
            } else {
                holder.image.setLayoutParams(new android.widget.LinearLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, ((int) (getHeightFromAspectRatio(user.getHeight(), user.getWidth(), imageView.getWidth())))));
            }
            {
                int type = new me.ccrama.redditslide.Visuals.FontPreferences(holder.body.getContext()).getFontTypeComment().getTypeface();
                android.graphics.Typeface typeface;
                if (type >= 0) {
                    typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(holder.body.getContext(), type);
                } else {
                    typeface = android.graphics.Typeface.DEFAULT;
                }
                holder.body.setTypeface(typeface);
            }
            {
                int type = new me.ccrama.redditslide.Visuals.FontPreferences(holder.body.getContext()).getFontTypeTitle().getTypeface();
                android.graphics.Typeface typeface;
                if (type >= 0) {
                    typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(holder.body.getContext(), type);
                } else {
                    typeface = android.graphics.Typeface.DEFAULT;
                }
                holder.text.setTypeface(typeface);
            }
            {
                if (user.getTitle() != null) {
                    java.util.List<java.lang.String> text = me.ccrama.redditslide.util.SubmissionParser.getBlocks(user.getTitle());
                    me.ccrama.redditslide.Adapters.AlbumView.setTextWithLinks(text.get(0), holder.text);
                    if (holder.text.getText().toString().isEmpty()) {
                        holder.text.setVisibility(android.view.View.GONE);
                    }
                } else {
                    holder.text.setVisibility(android.view.View.GONE);
                }
            }
            {
                if (user.getDescription() != null) {
                    java.util.List<java.lang.String> text = me.ccrama.redditslide.util.SubmissionParser.getBlocks(user.getDescription());
                    me.ccrama.redditslide.Adapters.AlbumView.setTextWithLinks(text.get(0), holder.body);
                    if (holder.body.getText().toString().isEmpty()) {
                        holder.body.setVisibility(android.view.View.GONE);
                    }
                } else {
                    holder.body.setVisibility(android.view.View.GONE);
                }
            }
            android.view.View.OnClickListener onGifImageClickListener = new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View view) {
                    if ((me.ccrama.redditslide.SettingValues.image && (!user.isAnimated())) || (me.ccrama.redditslide.SettingValues.gif && user.isAnimated())) {
                        android.content.Intent myIntent = new android.content.Intent(main, me.ccrama.redditslide.Activities.MediaView.class);
                        myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, user.getImageUrl());
                        myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, subreddit);
                        main.startActivity(myIntent);
                    } else {
                        me.ccrama.redditslide.util.LinkUtil.openExternally(user.getImageUrl());
                    }
                }
            };
            if (user.isAnimated()) {
                holder.body.setVisibility(android.view.View.VISIBLE);
                holder.body.setSingleLine(false);
                holder.body.setTextHtml(holder.text.getText() + main.getString(me.ccrama.redditslide.R.string.submission_tap_gif).toUpperCase());// got rid of the \n thing, because it didnt parse and it was already a new line so...

                holder.body.setOnClickListener(onGifImageClickListener);
            }
            holder.itemView.setOnClickListener(onGifImageClickListener);
        } else if (holder2 instanceof me.ccrama.redditslide.Adapters.AlbumView.SpacerViewHolder) {
            holder2.itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(holder2.itemView.getWidth(), paddingBottom ? height : main.findViewById(me.ccrama.redditslide.R.id.toolbar).getHeight()));
        }
    }

    public static void setTextWithLinks(java.lang.String s, me.ccrama.redditslide.SpoilerRobotoTextView text) {
        java.lang.String[] parts = s.split("\\s+");
        java.lang.StringBuilder b = new java.lang.StringBuilder();
        for (java.lang.String item : parts)
            try {
                java.net.URL url = new java.net.URL(item);
                b.append(" <a href=\"").append(url).append("\">").append(url).append("</a>");
            } catch (java.net.MalformedURLException e) {
                b.append(" ").append(item);
            }

        text.setTextHtml(b.toString(), "no sub");
    }

    @java.lang.Override
    public int getItemCount() {
        return users == null ? 0 : users.size() + 1;
    }

    public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SpacerViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    public static class AlbumViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        final me.ccrama.redditslide.SpoilerRobotoTextView text;

        final me.ccrama.redditslide.SpoilerRobotoTextView body;

        final android.widget.ImageView image;

        public AlbumViewHolder(android.view.View itemView) {
            super(itemView);
            text = itemView.findViewById(me.ccrama.redditslide.R.id.imagetitle);
            body = itemView.findViewById(me.ccrama.redditslide.R.id.imageCaption);
            image = itemView.findViewById(me.ccrama.redditslide.R.id.image);
        }
    }
}