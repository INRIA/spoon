package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.Legacy;


import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;




public class Input {
    public void cursorError1(SQLiteDatabase db, long route_id) {
        Cursor cursor = db.query("TABLE_TRIPS",
                new String[]{"KEY_TRIP_ID"},
                "ROUTE_ID=?",
                new String[]{Long.toString(route_id)},
                null, null, null);
        if (cursor != null) {
            cursor.close();
        }
    }

    void testProviderQueries(Uri uri, ContentProvider provider, ContentResolver resolver,
                             ContentProviderClient client) throws RemoteException {
        Cursor query = provider.query(uri, null, null, null, null);
        if (query != null) {
            query.close();
        }
        Cursor query2 = resolver.query(uri, null, null, null, null);
        if (query2 != null) {
            query2.close();
        }
        Cursor query3 = client.query(uri, null, null, null, null);
        if (query3 != null) {
            query3.close();
        }
    }

    public int ok(SQLiteDatabase db, long route_id, String table, String whereClause, String id) {
        int total_deletions = 0;
        Cursor cursor = db.query("TABLE_TRIPS",
                new String[]{
                        "KEY_TRIP_ID"},
                "ROUTE_ID" + "=?",
                new String[]{Long.toString(route_id)},
                null, null, null);

        while (cursor.moveToNext()) {
            total_deletions += db.delete(table, whereClause + "=?",
                    new String[]{Long.toString(cursor.getLong(0))});
        }
        if (cursor != null) {
            cursor.close();
        }

        return total_deletions;
    }

    public Cursor getCursor(SQLiteDatabase db) {
        Cursor cursor = db.query("TABLE_TRIPS",
                new String[]{
                        "KEY_TRIP_ID"},
                "ROUTE_ID" + "=?",
                new String[]{Long.toString(5)},
                null, null, null);

        return cursor;
    }

    public void testMultipleAssignment(Uri uri, ContentProvider provider){
        Cursor query = provider.query(uri, null, null, null, null);
        query.getLong(0);
        if (query != null) {
            query.close();
        }
        query = provider.query(uri, null, null, null, null);
        query.close();

    }

    public class RecycleTest extends View {
        public RecycleTest(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public void wrong1(AttributeSet attrs, int defStyle) {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{0}, defStyle, 0);
            String example = a.getString(0);
            if (a != null) {
                a.recycle();
            }
        }

        public void wrong2(AttributeSet attrs, int defStyle) {
            final TypedArray a = getContext().obtainStyledAttributes(new int[0]);
            if (a != null) {
                a.recycle();
            }
        }

        // ---- Check recycling VelocityTracker ----

        public void tracker() {
            VelocityTracker tracker = VelocityTracker.obtain();
            if (tracker != null) {
                tracker.recycle();
            }
        }

        // ---- Check recycling Message ----

        public void message() {
            Message message1 = getHandler().obtainMessage();
            if (message1 != null) {
                message1.recycle();
            }
            Message message2 = Message.obtain();
            if (message2 != null) {
                message2.recycle();
            }
        }

        // ---- Check recycling MotionEvent ----

        public void motionEvent() {
            MotionEvent event1 = MotionEvent.obtain(null);
            if (event1 != null) {
                event1.recycle();
            }
            MotionEvent event2 = MotionEvent.obtainNoHistory(null);
            if (event2 != null) {
                event2.recycle();
            }
        }

        public void motionEvent2() {
            MotionEvent event1 = MotionEvent.obtain(null); // OK
            MotionEvent event2 = MotionEvent.obtainNoHistory(null); // Not recycled
            if (event2 != null) {
                event2.recycle();
            }
            event1.recycle();

        }

        public void motionEvent3() {
            MotionEvent event1 = MotionEvent.obtain(null);  // Not recycled
            MotionEvent event2 = MotionEvent.obtain(event1);
            event2.recycle();

        }

        // ---- Check recycling Parcel ----
        public void parcelMissing() {
            Parcel myparcel = Parcel.obtain();
            myparcel.createBinderArray();
            if (myparcel != null) {
                myparcel.recycle();
            }
        }
    }
    public class ContentProviderClientTest {
        public void error1(ContentResolver resolver) {
            ContentProviderClient client = resolver.acquireContentProviderClient("test");
            if (client != null) {
                client.release();
            }
        }
    }
}