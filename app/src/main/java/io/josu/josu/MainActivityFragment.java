package io.josu.josu;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mJosuListAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            String[] data = {
                    "Dummy0 - Dummy data 0",
                    "Dummy1 - Dummy data 1",
            };
            List<String> josuList = new ArrayList<String>(Arrays.asList(data));
        mJosuListAdapter =
                    new ArrayAdapter<String>(
                            (MainActivity)getActivity(), // The current context (this activity)
                            R.layout.list_item_josus, // The name of the layout ID.
                            R.id.list_item_josu_textview, // The ID of the textview to populate.
                            josuList);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_courses);

            // Alternate way:
            // (ListView) getView().findViewById(R.id.listview_forecast);
            // getView() only works in fragments in onCreateViewMethod.

            Log.d("Josu", josuList.toString());
            listView.setAdapter(mJosuListAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    String forecast = mJosuListAdapter.getItem(position);
                    //Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
                 /*   Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, forecast);
                    startActivity(intent);*/
                    Toast.makeText(getActivity(),"loading Josu", Toast.LENGTH_SHORT).show();
                }
            });

            Log.d("Shunshine", "Refresh clicked");
            FetchLatestTask latestTask = new FetchLatestTask(((MainActivity)this.getActivity()), mJosuListAdapter);
            latestTask.execute("94043");

            return rootView;
        }
    }


