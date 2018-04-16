package com.nagy.zsolt.topcorn;

/**
 * Created by Zsolti on 2018.03.19..
 */

        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Parcelable;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.GridView;
        import com.nagy.zsolt.topcorn.api.FetchDataListener;
        import com.nagy.zsolt.topcorn.api.GETAPIRequest;
        import com.nagy.zsolt.topcorn.api.RequestQueueService;
        import com.nagy.zsolt.topcorn.utils.MovieAdapter;
        import org.json.JSONArray;
        import org.json.JSONObject;


public class TopRatedMovies extends Fragment {

    String[] moviePosterPath;
    JSONArray moviesJsonArray;
    Context mContext;
    GridView gridView;
    Parcelable state, restore;

    public TopRatedMovies() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        getTopRatedMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_top_rated_movies, container, false);
        gridView = layout.findViewById(R.id.top_rated_gridview);
        getTopRatedMovies();
        return layout;
    }

    public void getTopRatedMovies() {

        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            String url =  getString(R.string.movieDbApi) + getString(R.string.topRated) + getString(R.string.apiKeyParameter)+mContext.getString(R.string.movie_db_api_key);
            System.out.println(url);
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(getContext(), fetchGetResultListener, url);
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
                    moviesJsonArray = data.getJSONArray(getString(R.string.results));
                    moviePosterPath = new String[moviesJsonArray.length()];
                    for (int i = 0; i < moviesJsonArray.length(); i++) {
                        JSONObject obj = moviesJsonArray.getJSONObject(i);
                        moviePosterPath[i] = obj.optString(getString(R.string.posterPath));
                    }
                    MovieAdapter movieAdapter = new MovieAdapter(mContext, moviePosterPath);
                    gridView.setAdapter(movieAdapter);
                    if(restore != null){
                        gridView.onRestoreInstanceState(restore);
                    }
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            launchDetailActivity(position);
                        }
                    });
                } else {
                    RequestQueueService.showAlert(getString(R.string.noDataAlert), getActivity());
                }
            } catch (
                    Exception e) {
                RequestQueueService.showAlert(getString(R.string.exceptionAlert), getActivity());
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.cancelProgressDialog();
            //Show if any error message is there called from GETAPIRequest class
            RequestQueueService.showAlert(msg, getActivity());
        }

        @Override
        public void onFetchStart() {
            //Start showing progressbar or any loader you have
            RequestQueueService.showProgressDialog(getActivity());
        }
    };

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        intent.putExtra(DetailActivity.EXTRA_JSONARRAY, moviesJsonArray.toString());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_out);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        state = gridView.onSaveInstanceState();
        outState.putParcelable("STATE", state);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            restore = savedInstanceState.getParcelable("STATE");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getTopRatedMovies();
    }

}
