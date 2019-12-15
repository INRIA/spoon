package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.Legacy;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.content.Context;
import android.os.Parcel;

public class Input {
    public void cursorError1(SQLiteDatabase db, long route_id) {
        Cursor cursor = db.query("TABLE_TRIPS",
                new String[]{"KEY_TRIP_ID"},
                "ROUTE_ID=?",
                new String[]{Long.toString(route_id)},
                null, null, null);
    }

    void testProviderQueries(Uri uri, ContentProvider provider, ContentResolver resolver,
                             ContentProviderClient client) throws RemoteException {
        Cursor query = provider.query(uri, null, null, null, null);
        Cursor query2 = resolver.query(uri, null, null, null, null);
        Cursor query3 = client.query(uri, null, null, null, null);
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
        }

        public void wrong2(AttributeSet attrs, int defStyle) {
            final TypedArray a = getContext().obtainStyledAttributes(new int[0]);
        }

        // ---- Check recycling VelocityTracker ----

        public void tracker() {
            VelocityTracker tracker = VelocityTracker.obtain();
        }

        // ---- Check recycling Message ----

        public void message() {
            Message message1 = getHandler().obtainMessage();
            Message message2 = Message.obtain();
        }

        // ---- Check recycling MotionEvent ----

        public void motionEvent() {
            MotionEvent event1 = MotionEvent.obtain(null);
            MotionEvent event2 = MotionEvent.obtainNoHistory(null);
        }

        public void motionEvent2() {
            MotionEvent event1 = MotionEvent.obtain(null); // OK
            MotionEvent event2 = MotionEvent.obtainNoHistory(null); // Not recycled
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
        }
    }
    public class ContentProviderClientTest {
        public void error1(ContentResolver resolver) {
            ContentProviderClient client = resolver.acquireContentProviderClient("test");
        }
    }
}