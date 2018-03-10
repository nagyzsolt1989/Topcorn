package com.nagy.zsolt.topcorn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagy.zsolt.topcorn.model.Movie;

/**
 * Created by Zsolti on 2018.03.07..
 */

public class MovieAdapter extends BaseAdapter {

    private final Context mContext;
    private final String[] movies;

    public MovieAdapter(Context context, String[] movies){
        this.mContext=context;
        this.movies=movies;
    }

    @Override
    public int getCount() {
        return movies.length;
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

        final String movie = movies[position];

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_book_name);

        // 4
//        imageView.setImageResource(movie.getPosterPath());
        nameTextView.setText(movies[position]);

        return convertView;
    }
}
