package io.josu.josu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.josu.josu.data.JosuContract.CourseEntry;
import io.josu.josu.data.JosuContract.ContentEntry;

/**
 * Created by PELLO_ALTADILL on 06/09/2016.
 */
public class JosuDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "josudb.db";

    public JosuDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME + " (" +
                CourseEntry._ID + " INTEGER PRIMARY KEY," +
                CourseEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                CourseEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_DATETIME + " REAL NOT NULL, " +
                " );";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + ContentEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                ContentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ContentEntry.COLUMN_ORDER + " INTEGER NOT NULL, " +
                ContentEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ContentEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ContentEntry.COLUMN_TYPE + " INTEGER NOT NULL," +
                ContentEntry.COLUMN_COURSE_KEY + " INTEGER NOT NULL," +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ContentEntry.COLUMN_COURSE_KEY + ") REFERENCES " +
                CourseEntry.TABLE_NAME + " (" + CourseEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + ContentEntry._ID + ", " +
                CourseEntry._ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContentEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
