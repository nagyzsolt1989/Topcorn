package com.nagy.zsolt.topcorn;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nagy.zsolt.topcorn.api.FetchDataListener;
import com.nagy.zsolt.topcorn.api.GETAPIRequest;
import com.nagy.zsolt.topcorn.api.RequestQueueService;
import com.nagy.zsolt.topcorn.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    final String KEY_URL_TMDB = "http://image.tmdb.org/t/p/.";
    final String KEY_PSTR_SIZE = "w185";
    String[] movieTitles;
    JSONArray moviesJsonArray;
    Context mContext;

    ArrayList<String> list = new ArrayList<String>();
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridview);

        mContext = getApplicationContext();
        getMoviesFromServer();
    }

    public void getMoviesFromServer() {

        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(MainActivity.this, fetchGetResultListener);
            Toast.makeText(MainActivity.this, "GET API called", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Implementing interfaces of FetchDataListener for GET api request
    FetchDataListener fetchGetResultListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            //Fetch Complete. Now stop progress bar  or loader
            //you started in onFetchStart
            RequestQueueService.cancelProgressDialog();
            try {
                //Check result sent by our GETAPIRequest class
                if (data != null) {
                    moviesJsonArray = data.getJSONArray("results");
                    movieTitles = new String[moviesJsonArray.length()];
                    for (int i = 0; i < moviesJsonArray.length(); i++) {
                        JSONObject obj = moviesJsonArray.getJSONObject(i);
                        movieTitles[i] = obj.optString("title");
                    }
                    MovieAdapter movieAdapter = new MovieAdapter(mContext, movieTitles);
                    gridView.setAdapter(movieAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            launchDetailActivity(position);
                        }
                    });

                } else {
                    RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
                }
            } catch (
                    Exception e) {
                RequestQueueService.showAlert("Something went wrong", MainActivity.this);
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.cancelProgressDialog();
            //Show if any error message is there called from GETAPIRequest class
            RequestQueueService.showAlert(msg, MainActivity.this);
        }

        @Override
        public void onFetchStart() {
            //Start showing progressbar or any loader you have
            RequestQueueService.showProgressDialog(MainActivity.this);
        }
    };
    
    private void launchDetailActivity(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        intent.putExtra(DetailActivity.EXTRA_JSONARRAY, moviesJsonArray.toString());
        startActivity(intent);
    }
}
