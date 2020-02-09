package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Adapters.CommentUrlObject;
import me.ccrama.redditslide.ContentType;
import java.util.HashMap;
import me.ccrama.redditslide.Fragments.AlbumFullComments;
import java.util.ArrayList;
import me.ccrama.redditslide.PostLoader;
import me.ccrama.redditslide.Fragments.MediaFragment;
import me.ccrama.redditslide.Adapters.SubmissionDisplay;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Adapters.MultiredditPosts;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Fragments.MediaFragmentComment;
import me.ccrama.redditslide.Fragments.SelftextFull;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Fragments.AlbumFull;
import me.ccrama.redditslide.Fragments.TitleFull;
import me.ccrama.redditslide.Authentication;
import java.util.List;
import me.ccrama.redditslide.LastComments;
import me.ccrama.redditslide.Adapters.SubredditPosts;
/**
 * Created by ccrama on 9/17/2015.
 */
public class ShadowboxComments extends me.ccrama.redditslide.Activities.FullScreenActivity {
    public static java.util.ArrayList<me.ccrama.redditslide.Adapters.CommentUrlObject> comments;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        if ((me.ccrama.redditslide.Activities.ShadowboxComments.comments == null) || me.ccrama.redditslide.Activities.ShadowboxComments.comments.isEmpty()) {
            finish();
        }
        applyDarkColorTheme(me.ccrama.redditslide.Activities.ShadowboxComments.comments.get(0).comment.getComment().getSubredditName());
        super.onCreate(savedInstance);
        setContentView(me.ccrama.redditslide.R.layout.activity_slide);
        android.support.v4.view.ViewPager pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        commentPager = new me.ccrama.redditslide.Activities.ShadowboxComments.OverviewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(commentPager);
    }

    me.ccrama.redditslide.Activities.ShadowboxComments.OverviewPagerAdapter commentPager;

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment f = null;
            net.dean.jraw.models.Comment comment = me.ccrama.redditslide.Activities.ShadowboxComments.comments.get(i).comment.getComment();
            java.lang.String url = me.ccrama.redditslide.Activities.ShadowboxComments.comments.get(i).url;
            me.ccrama.redditslide.ContentType.Type t = me.ccrama.redditslide.ContentType.getContentType(url);
            switch (t) {
                case GIF :
                case IMAGE :
                case IMGUR :
                case REDDIT :
                case EXTERNAL :
                case XKCD :
                case SPOILER :
                case DEVIANTART :
                case EMBEDDED :
                case LINK :
                case VID_ME :
                case STREAMABLE :
                case VIDEO :
                    {
                        f = new me.ccrama.redditslide.Fragments.MediaFragmentComment();
                        android.os.Bundle args = new android.os.Bundle();
                        args.putString("contentUrl", url);
                        args.putString("firstUrl", url);
                        args.putInt("page", i);
                        args.putString("sub", comment.getSubredditName());
                        f.setArguments(args);
                    }
                    break;
                case ALBUM :
                    {
                        f = new me.ccrama.redditslide.Fragments.AlbumFullComments();
                        android.os.Bundle args = new android.os.Bundle();
                        args.putInt("page", i);
                        f.setArguments(args);
                    }
                    break;
            }
            return f;
        }

        @java.lang.Override
        public int getCount() {
            return me.ccrama.redditslide.Activities.ShadowboxComments.comments.size();
        }
    }
}