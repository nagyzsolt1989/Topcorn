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
 * Created by Zsolti on 2018.03.26..
 */

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.MyViewHolder> {

    private String[] mProfilePath, names, characters;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, character;
        public ImageView profilePicture;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            character = (TextView) view.findViewById(R.id.character);
            profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
//            character = (TextView) view.findViewById(R.id.genre);
        }
    }


    public CreditsAdapter(Context context, String[] mProfilePath, String[] names, String[] characters) {
        this.mProfilePath = mProfilePath;
        this.names = names;
        this.characters = characters;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.credit_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(names[position]);
        holder.character.setText(characters[position]);
        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w780/" + mProfilePath[position])
                .transform(new CropCircleTransformation())
                .into(holder.profilePicture);
    }

    @Override
    public int getItemCount() {
        return mProfilePath.length;
    }
}