package com.example.dinasaad.dailyrecipesapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dinasaad.dailyrecipesapp.data.DatabaseContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by DinaSaad on 27/08/2017.
 */

public class RecipeListAdaper extends BaseAdapter {
    ArrayList<String> results = new ArrayList<String>();
    ArrayList<String> imageslist = new ArrayList<String>();
    Context context;
    private LayoutInflater mInflater;

    public RecipeListAdaper(Context context, ArrayList<RecipeObject> Data) {
        for (int i = 0; i < Data.size(); i++) {
            results.add(Data.get(i).getName());
            imageslist.add(Data.get(i).getImageURL());
        }

        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public RecipeListAdaper(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (results == null) return 0;
        else return results.size();
    }

    public void updateReceipesList(Cursor cursor) {
        results.clear();
        imageslist.clear();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String rec_name = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_Movie_ID));
            String rec_imgurl = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_ImageURL));

            results.add(rec_name);
            imageslist.add(rec_imgurl);
            cursor.moveToNext();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.list_item, null);
        TextView Recipe_name = (TextView) convertView.findViewById(R.id.Recipe_name); // title
        Recipe_name.setText(results.get(position));
        if (!imageslist.get(position).isEmpty()) {
            ImageView imageview_detail = (ImageView) convertView.findViewById(R.id.RecipeImage);
            Picasso.with(context).load(imageslist.get(position)).into(imageview_detail);
        }
        return convertView;
    }
    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */


}

