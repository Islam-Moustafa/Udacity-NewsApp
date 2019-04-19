package com.example.islam.udacity_news;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// LoaderCallbacks interface to can override three methods onCreatLoader, onLoadFinished, onLoaderReset
public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>> {

    /** define global variables used in this class. **/

    // ProgressBar display when wait loading data
    private static ProgressBar loadingIndicator;

    // this query to pass to onCreateLoader method to can change content of loader
    String query;

    // specify integer ID for loader, to distinguish between loaders
    private static final int NEWS_LOADER_ID = 1;

    // listView to handle list of news in UI
    ListView newsListView;
    // adapter to setting list of news
    private NewsAdapter mAdapter;

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    Button tNews, bNews, sNews;

    // bundle is bundle information about this activity
    // execute of activity start from onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // reference to ProgressBar loading_indicator in layout
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        // reference to list ListView in layout
        newsListView = (ListView) findViewById(R.id.list);

        // reference to empty_view in the layout, to hold empty state when no books in the list
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the ListView to display elements in UI
        newsListView.setAdapter(mAdapter);

        // click on each NEW in listView to open this in new activity by intent
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current NEW that was clicked on
                News currentEarthquake = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the NEW URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // LoaderManager to control in Loader
        final LoaderManager loaderManager = getLoaderManager();


        /**  check internet connection after each click   **/

        // reference to CONNECTIVITY_SERVICE by ConnectivityManager to check state network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // NetworkInfo represent current state of network connection
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if(networkInfo != null && networkInfo.isConnected()){
            // to default display in ListView before any button click
            query =  "https://content.guardianapis.com/search?q=debates&api-key=test";
            loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
        }
        else{
            // show emptyView instead of listView elements, so first must clear adapter
            // mAdapter.clear();
            mEmptyStateTextView.setText("No Internet Connection");
            newsListView.setEmptyView(mEmptyStateTextView);
        }


    }


    // onCreateLoader to create and return a new loader
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(MainActivity.this, query);
    }

    // onLoadFinished() called when loader is finished loading data to update UI by loader result
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> earthquakes) {

        // Hide loading indicator because the data has been loaded
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // if there is a valid list of books then add to adapter to update bookListView
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    // onLoaderReset method to reset(clear) loader when fetching error data
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}

