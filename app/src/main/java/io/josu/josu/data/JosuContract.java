package io.josu.josu.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by PELLO_ALTADILL on 06/09/2016.
 */
public class JosuContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "io.josu.josu.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // PATH to Tables API
    public static final String PATH_COURSE = "course";
    public static final String PATH_CONTENT = "content";


    /* Inner class that defines the table contents of the course table */
    public static final class CourseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;

        // Table name
        public static final String TABLE_NAME = "course";

        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_DATETIME = "course_datetime";

        // Human readable location string, provided by the API.  Because for styling,
        // "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_NAME = "course_name";
        public static final String COLUMN_DESCRIPTION = "course_description";


        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the course table */
    public static final class ContentEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTENT;

        // Table name
        public static final String TABLE_NAME = "content";

        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_ORDER = "content_order";

        // Human readable location string, provided by the API.  Because for styling,
        // "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_NAME = "content_name";
        public static final String COLUMN_DESCRIPTION = "content_description";
        public static final String COLUMN_TYPE = "content_type";
        public static final String COLUMN_COURSE_KEY = "content_course_key";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
