package com.example.dinasaad.dailyrecipesapp;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by DinaSaad on 27/08/2017.
 */

public class RecipeWidgetService extends IntentService {


    public static final String ACTION_RECIPE = "com.example.android.dailyrecipesapp.action.water_plants";
    public static final String ACTION_UPDATE_RECIPE_WIDGETS = "com.example.android.dailyrecipesapp.action.update_plant_widgets";

    public RecipeWidgetService() {
        super("RecipeWidgetService");
    }

    /**
     * Starts this service to perform WaterPlants action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionWaterPlants(Context context) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.setAction(ACTION_RECIPE);
        context.startService(intent);
    }

    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECIPE.equals(action)) {
                handleActionWaterPlants();
            } else if (ACTION_UPDATE_RECIPE_WIDGETS.equals(action)) {
                handleActionUpdatePlantWidgets();
            }
        }
    }

    public static void startActionUpdatePlantWidgets(Context context) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGETS);
        context.startService(intent);
    }

    /**
     * Handle action WaterPlant in the provided background thread with the provided
     * parameters.
     */
    private void handleActionWaterPlants() {
        ArrayList<String> IngredientName = new ArrayList<String>();
        JSONArray IngredientsArray;
        try {
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

    /**
     * Handle action UpdatePlantWidgets in the provided background thread
     */
    private void handleActionUpdatePlantWidgets() {
        ArrayList<String> IngredientName = new ArrayList<String>();
        JSONArray IngredientsArray;
        try {
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String IngredientsJson = sharedPref.getString("Widget_data", null);
            if (IngredientsJson != null) {
                IngredientsArray = new JSONArray(IngredientsJson);

                if (IngredientsArray != null) {
                    if (IngredientsArray.length() == 0) {

                    } else {
                        ArrayList<String> ingredientList = new ArrayList<String>();
                        for (int i = 0; i < IngredientsArray.length(); i++) {
                            IngredientName.add(IngredientsArray.get(i).toString());
                        }

                    }
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.WidgetIngedientsList);
        //Now update all widgets
        RecipeWidgetProvider.updateWidgets(this, appWidgetManager, appWidgetIds);
    }
}
