package io.josu.josu;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by PELLO_ALTADILL on 06/09/2016.
 */
public class FetchLatestTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchLatestTask.class.getSimpleName();
    private AppCompatActivity mainActivity;
    private ArrayAdapter<String> mJosuListAdapter;

    public FetchLatestTask(AppCompatActivity mainActivity, ArrayAdapter<String> mJosuListAdapter) {
        this.mainActivity = mainActivity;
        this.mJosuListAdapter = mJosuListAdapter;
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }


    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getLatestDataFromJson(String serverJsonString)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String JSON_LIST = "josus";
        final String JSON_ID = "id";
        final String JSON_NAME = "name";
        final String JSON_DESCRIPTION = "description";
        final String JSON_ICON = "icon";
        final String JSON_PUBLIC = "public";

        JSONObject josusJson = new JSONObject(serverJsonString);
        JSONArray josusArray = josusJson.getJSONArray(JSON_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[10];

       /* SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(mainActivity);
        String unitType = sharedPrefs.getString("unit","c");
        */
              //  getString(R.string.pref_units_key),
              //  getString(R.string.pref_units_metric));

        for(int i = 0; i < josusArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String id;
            String name;
            String description;
            String icon;
            Boolean isPublic;


            // Get the JSON object representing the day
            JSONObject josuJSON = josusArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
          /*  long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);
*/
            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.

            name = josuJSON.getString(JSON_NAME);
            description= josuJSON.getString(JSON_DESCRIPTION);

            resultStrs[i] = name + " - " + description;
        }
        return resultStrs;

    }
    @Override
    protected String[] doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String serverJsonString = null;

        String format = "json";
        String units = "metric";
        int numDays = 7;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String JOSU_IO_URL =
                    "http://192.168.17.133/josu/josus.list.json";
            final String DATA_PARAM = "cnt";

            Uri builtUri = Uri.parse(JOSU_IO_URL).buildUpon()
                    .appendQueryParameter(DATA_PARAM, "josus.list.json")
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            serverJsonString = buffer.toString();
            Log.d("Josu", "Parsing..." + serverJsonString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getLatestDataFromJson(serverJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            mJosuListAdapter.clear();
            for(String josuString : result) {
                mJosuListAdapter.add(josuString);
            }
            // New data is back from the server.  Hooray!
        }
    }
}