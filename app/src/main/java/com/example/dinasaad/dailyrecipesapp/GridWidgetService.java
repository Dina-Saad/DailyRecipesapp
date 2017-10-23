package com.example.dinasaad.dailyrecipesapp;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    ArrayList<String> IngredientName;
    Context mContext;
    JSONArray IngredientsArray;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
        IngredientName = new ArrayList<String>();
        try {
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(mContext);
            String IngredientsJson = sharedPref.getString("Widget_data", null);
            if (IngredientsJson != null) {
                IngredientsArray = new JSONArray(IngredientsJson);

                if (IngredientsArray != null) {
                    if (IngredientsArray.length() == 0) {

                    } else {
                        ArrayList<String> ingredientList = new ArrayList<String>();
                        for (int i = 0; i < IngredientsArray.length(); i++) {
                            ingredientList.add(IngredientsArray.get(i).toString());
                        }
                        // Get the JSON object
                        IngredientName = ingredientList;

                    }
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }


    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {

        IngredientName = new ArrayList<String>();
        try {
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(mContext);
            String IngredientsJson = sharedPref.getString("Widget_data", null);
            if (IngredientsJson != null) {
                IngredientsArray = new JSONArray(IngredientsJson);

                if (IngredientsArray != null) {
                    if (IngredientsArray.length() == 0) {

                    } else {
                        ArrayList<String> ingredientList = new ArrayList<String>();
                        for (int i = 0; i < IngredientsArray.length(); i++) {
                            ingredientList.add(IngredientsArray.get(i).toString());
                        }
                        // Get the JSON object
                        IngredientName = ingredientList;

                    }
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (IngredientsArray != null) {
            return IngredientsArray.length();
        } else {
            return 0;
        }
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {

        if (IngredientsArray == null || IngredientsArray.length() == 0)
            return null;
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widgetlistitem);
        views.setTextViewText(R.id.ingredientItem, IngredientName.get(position));
        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

