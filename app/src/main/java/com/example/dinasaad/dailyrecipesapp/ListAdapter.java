package com.example.dinasaad.dailyrecipesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DinaSaad on 21/08/2017.
 */

public class ListAdapter extends BaseAdapter {
    ArrayList<String> results;
    Context context;

    private LayoutInflater mInflater;

    public ListAdapter(Context context, ArrayList<String> Data) {
        results = Data;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public ListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (results == null) return 0;
        else return results.size();
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
            convertView = mInflater.inflate(R.layout.ingredientlistitem, null);
        TextView ingredientView = (TextView) convertView.findViewById(R.id.ingredientItem); // title
        ingredientView.setText(results.get(position));
        return convertView;
    }
}
