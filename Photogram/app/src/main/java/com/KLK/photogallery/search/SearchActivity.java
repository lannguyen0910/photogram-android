package com.KLK.photogallery.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.EndlessScrollListener;
import com.KLK.photogallery.helper.GridSearchImageAdapter;
import com.KLK.photogallery.helper.SearchUtils;
import com.KLK.photogallery.model.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

/** search for images with given text **/
public class SearchActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "SearchActivity";

    private static SearchActivity instance;

    private static final int ACTIVITY_NUM = 1;
    private final Context context = SearchActivity.this;

    private GridSearchImageAdapter imageGridAdapter;
    private List<String> imageList;

    private String searchString;
    private TextView emptyTextView;
    private View loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "Start onCreate()!");

        imageList = new ArrayList<>(100);

        //set up gridView and it's empty case view
        GridView gridView = (GridView) findViewById(R.id.gridview);
        emptyTextView = (TextView) findViewById(R.id.empty);
        gridView.setEmptyView(emptyTextView);

        loadingIndicator = findViewById(R.id.loading_indicator_main_grid);

        imageGridAdapter = new GridSearchImageAdapter(SearchActivity.this, imageList);

        gridView.setAdapter(imageGridAdapter);

        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadNextDataFromApi(page);
                return true;
            }
        });

        //listener for each item in gridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ((!SearchUtils.checkImageResource(SearchActivity.this, (ImageView) view.findViewById(R.id.grid_item_image), R.drawable.ic_image_error))
                        && ((ProgressBar)view.findViewById(R.id.grid_item_loading_indicator)).getVisibility()==View.INVISIBLE) {

                    Intent intent = new Intent(SearchActivity.this, ImageActivity.class);
                    intent.putExtra("imageuri", imageList.get(position));
                    startActivity(intent);

                } else if (SearchUtils.checkImageResource(SearchActivity.this, (ImageView) view.findViewById(R.id.grid_item_image), R.drawable.ic_image_error)) {
                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(SearchActivity.this,getResources().getString(R.string.image_loading), Toast.LENGTH_SHORT).show();
                }

            }
        });

        configBottomNavigationView();
    }

    private void configBottomNavigationView(){
        Log.d(TAG, "Config Bottom Navigation View!");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewUtils.configBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewUtils.navigating(context, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    // This method probably sends out a network request and appends new data items to adapter.
    public void loadNextDataFromApi(int offset) {
        Uri.Builder uriBuilder = SearchUtils.getUri(getSearchString());
        uriBuilder.appendQueryParameter("start", "" + offset);
        volleyRequest(uriBuilder.toString(), 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        // Set current activity as searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    // Receive query from searchWidget
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    // Handle query from searchWidget
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
            loadingIndicator.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
            volleyRequest(getUri(), 0);

        }
    }

    //Volley request for json string
    public void volleyRequest(String volleySearchString, final int addFlag) {

        /** @params {Request type, url to be searched, responseHandler, errorHandler} **/

        StringRequest stringRequest = new StringRequest(Request.Method.GET, volleySearchString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingIndicator.setVisibility(View.GONE);
                        updateUIPostExecute(SearchUtils.extractImages(response), addFlag);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingIndicator.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                imageGridAdapter.clear();
                emptyTextView.setVisibility(View.VISIBLE);

                String message = null;
                if (error instanceof NetworkError) {
                    message = getResources().getString(R.string.connection_error);
                } else if (error instanceof ServerError) {
                    message = getResources().getString(R.string.server_error);
                } else if (error instanceof AuthFailureError) {
                    message = getResources().getString(R.string.connection_error);
                } else if (error instanceof ParseError) {
                    message = getResources().getString(R.string.parse_error);
                } else if (error instanceof TimeoutError) {
                    message = getResources().getString(R.string.timeout_error);
                }

                emptyTextView.setText(message);
            }
        });

        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void updateUIPostExecute(List<String> response, int addFlag) {

        if (addFlag == 0) {
            imageGridAdapter.clear();
            imageList.clear();
        }

        imageList.addAll(response);
        imageGridAdapter.notifyDataSetChanged();

    }


    public String getSearchString() {
        return searchString;
    }

    public String getUri() {
        return SearchUtils.getUri(getSearchString()).toString();
    }

}
