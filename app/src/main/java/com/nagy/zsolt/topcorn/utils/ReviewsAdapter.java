package com.nagy.zsolt.topcorn.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagy.zsolt.topcorn.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by Zsolti on 2018.04.05..
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {

    private String[] ids, authors, contents;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView author, content;

        public MyViewHolder(View view) {
            super(view);
            author = (TextView) view.findViewById(R.id.review_author);
            content = (TextView) view.findViewById(R.id.review_content);
        }
    }


    public ReviewsAdapter(Context context, String[] ids, String[] authors, String[] contents) {
        this.ids = ids;
        this.authors = authors;
        this.contents = contents;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.author.setText(authors[position]);
        holder.content.setText(contents[position]);
    }

    @Override
    public int getItemCount() {
        return authors.length;
    }
}
