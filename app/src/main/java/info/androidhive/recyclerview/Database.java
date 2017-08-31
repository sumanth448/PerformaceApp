package info.androidhive.recyclerview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by sumanth.reddy on 23/02/17.
 */
public class Database extends SQLiteOpenHelper {
    public static String DB_NAME = "performance.db";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase mOlaDb = null;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private final String DB_APP_PERF_TABLE = "app_performance_table";
    private final String COLUMN_NAME_PACKAGENAME = "package_name";
    private final String COLUMN_NAME_UUID = "app_uuid";
    private final String COLUMN_NAME_MEMORY = "app_memory";
    private final String COLUMN_NAME_DATAUSAGE_SEND = "data_usage_sent";
    private final String COLUMN_NAME_DATAUSAGE_RECEIVE = "data_usage_receive";
    private final String COLUMN_NAME_updated_time = "updated_at";
    private final String ID = "id";
    private static Database kDatabase = null;

    public static Database getInstance(Context context) {
        if (kDatabase == null) {
            synchronized (context) {
                if (kDatabase == null) {
                    kDatabase = new Database(context);
                }
            }
        }
        return kDatabase;
    }
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mOlaDb = getWritableDatabase();
        mOlaDb.setVersion(DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         String SQL_CREATE_ENTRIES = "CREATE TABLE "
                + DB_APP_PERF_TABLE + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + COLUMN_NAME_PACKAGENAME
                + TEXT_TYPE + COMMA_SEP + COLUMN_NAME_UUID
                + TEXT_TYPE + COMMA_SEP + COLUMN_NAME_MEMORY
                + TEXT_TYPE + COMMA_SEP + COLUMN_NAME_DATAUSAGE_SEND
                + TEXT_TYPE + COMMA_SEP + COLUMN_NAME_DATAUSAGE_RECEIVE
                + TEXT_TYPE + COMMA_SEP + COLUMN_NAME_updated_time + " )";
          db.beginTransaction();
          db.execSQL(SQL_CREATE_ENTRIES);
          db.setTransactionSuccessful();
          db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // STATUS UPDATE TABLE
    public synchronized void insertStatusUpdate(
            ContentValues values) {
        mOlaDb.insert(DB_APP_PERF_TABLE, null, values);
        // DLogger.d("DataBase", "StatusUpdate insert " + id);

    }

    // Delete success status update from DB
    public synchronized void deleteStatusUpdateRequest(String whereClause) {
        // String whereClause = STATUS_UPDATE_TIME_STAMP + "=?";
        // String[] mSelectionArgs = { String.valueOf(timestamp) };

        int count = mOlaDb.delete(DB_APP_PERF_TABLE, whereClause, null);
    }

    public ArrayList<ServiceData> getservicedata(String startdate , String enddate){
        ArrayList<ServiceData> syncApis = new ArrayList<>();
        Cursor cursor;
        ServiceData servciedata = null;
        String selection = COLUMN_NAME_updated_time + "between " + startdate + " and " + enddate;
        cursor = mOlaDb.query(DB_APP_PERF_TABLE,
                null,
                selection,
                null , null, null,
                ID+ " ASC",
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                servciedata = new ServiceData();
                servciedata.setPackage_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PACKAGENAME)));
                servciedata.setApp_uuid(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UUID)));
                servciedata.setApp_battery(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MEMORY)));
                servciedata.setData_usage_sent(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DATAUSAGE_SEND)));
                servciedata.setData_usage_receive(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DATAUSAGE_RECEIVE)));
                servciedata.setUpdated_at(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_updated_time)));
                syncApis.add(servciedata);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        System.out.println(" getOfflineSyncApis count:"+syncApis.size());
        return syncApis;
    }
}
