package grexClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lorenzo on 2/13/2017.
 *
 */
public class LocalDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "OfflineRooms";
    private static final int DATABASE_VERSION = 1;

    public LocalDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE rooms (room BLOB)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS rooms");
        onCreate(db);
    }

    public boolean saveRoom(Room room) {
        String roomString = GrexSocket.gson.toJson(room);
        Cursor cursor = getRoom(roomString);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("room", roomString);

        long result;
        if (cursor.getCount() == 0) { // Record does not exist
            result = db.insert("rooms", null, contentValues);
        } else { // Record exists
            result = db.update("rooms", contentValues, "room=?", new String[]{roomString});
        }

        return result != -1;
    }

    public Cursor getRoom(String roomString) {

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM rooms WHERE room=?";

        return db.rawQuery(sql, new String[]{roomString});
    }

    public void deleteUser(String roomString) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("rooms", "room=?", new String[]{roomString});
    }
}
