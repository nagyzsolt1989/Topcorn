package com.nagy.zsolt.topcorn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nagy.zsolt.topcorn.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by Zsolti on 2018.03.07..
 */

public class MovieAdapter extends BaseAdapter {

    private final Context mContext;
    private final String[] moviePosterPath;

    public MovieAdapter(Context context, String[] moviePosterPath){
        this.mContext=context;
        this.moviePosterPath=moviePosterPath;
    }

    @Override
    public int getCount() {
        return moviePosterPath.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        int posterlWidth = 300;
        int posterHeight = 450;

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }

        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_movie_poster);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+moviePosterPath[position]).into(imageView);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(posterlWidth, posterHeight));

        return convertView;
    }
}
