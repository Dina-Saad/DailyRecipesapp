package com.example.dinasaad.dailyrecipesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinasaad.dailyrecipesapp.data.DatabaseContract;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.makeText;
import static com.example.dinasaad.dailyrecipesapp.BuildConfig.The_Application_ID;
import static com.example.dinasaad.dailyrecipesapp.BuildConfig.The_Application_Key;
import static com.example.dinasaad.dailyrecipesapp.MainActivity.favouriteRecipesId;
import static com.example.dinasaad.dailyrecipesapp.R.id.ratingBar;
import static com.example.dinasaad.dailyrecipesapp.RecipeFragment.FavRecipeAdapter;
import static com.example.dinasaad.dailyrecipesapp.RecipeFragment.FavouriteRecipe;
import static com.example.dinasaad.dailyrecipesapp.RecipeFragment.tabhost;

/**
 * Created by DinaSaad on 18/08/2017.
 */

public class DetailFragment extends Fragment {
    RecipeObject Recipe = new RecipeObject();
    Boolean SourceURLExist = false;
    ListView IngredientListView;
    private ListAdapter IngredientAdapter;
    TextView ingredients;
    private FirebaseAnalytics mFirebaseAnalytics;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Recipe = new RecipeObject();

        IngredientListView = (ListView) rootView.findViewById(R.id.IngredientList);
        if (Utilities.Istablet) {
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                Recipe = bundle.getParcelable("clickedRecipeObject");

            }

        } else {
            Intent intent = getActivity().getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Recipe = extras.getParcelable("clickedRecipeObject");
            }
        }

        if (favouriteRecipesId.size() != 0) {
            if (favouriteRecipesId.contains(Recipe.getId()))
                Recipe.setIsFavourite("1");
            else
                Recipe.setIsFavourite("0");

        } else
            Recipe.setIsFavourite("0");
        ImageView imageview_detail = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(Recipe.getImageURL()).into(imageview_detail);

        RatingBar ratingbar = (RatingBar) rootView.findViewById(ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingbar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        ratingbar.setRating(Integer.parseInt(Recipe.getRating()));

        TextView Recipe_name = (TextView) rootView.findViewById(R.id.Recipe_name); // title
        Recipe_name.setText(Recipe.getName());
        ingredients = (TextView) rootView.findViewById(R.id.textView1); // title

        TextView Time = (TextView) rootView.findViewById(R.id.textView3); // title
        int TimeMinutes = Integer.parseInt(Recipe.getTotalTimeInSeconds()) / 60;
        int TimeHour;
        if (TimeMinutes > 60) {
            TimeHour = TimeMinutes / 60;
            if (TimeMinutes % 60 != 0) {
                TimeMinutes = TimeMinutes % 60;
                String time = String.valueOf(TimeHour).concat(" ") + getString(R.string.hour_text).concat(" ") + String.valueOf(TimeMinutes).concat(" ") + getString(R.string.minutes_text);
                Time.setText(time);
            } else {
                String time = String.valueOf(TimeHour).concat(" ") +getString(R.string.hour_text);
                Time.setText(time);
            }

        } else if (TimeMinutes == 60) {

            TimeHour = TimeMinutes / 60;
            String time = String.valueOf(TimeHour).concat(" ")+getString(R.string.hour_text);
            Time.setText(time);

        } else {
            String time = String.valueOf(TimeMinutes).concat(" ") +getString(R.string.minutes_text);
            Time.setText(time);

        }
        FetchRecipesTask Task = new FetchRecipesTask();
        Task.execute();
        final Button Sourcebutton = (Button) rootView.findViewById(R.id.button2);
        Sourcebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if (SourceURLExist) {
                    Uri uri = Uri.parse(Recipe.getSourceURL());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        final ImageButton favourite_btn = (ImageButton) rootView.findViewById(R.id.imageButton);
        if (Recipe.getIsFavourite().matches("0")) {
            favourite_btn.setImageResource(android.R.drawable.btn_star_big_off);
        } else if (Recipe.getIsFavourite().matches("1")) {
            favourite_btn.setImageResource(android.R.drawable.btn_star_big_on);
        }

        favourite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Recipe.getIsFavourite().matches("0")) {
                    favourite_btn.setImageResource(android.R.drawable.btn_star_big_on);
                    Recipe.setIsFavourite("1");
                    AddFavouriteRecipe(Recipe);
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf((R.id.imageButton)));
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "favourite_btn");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ImageButton");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                } else if (Recipe.getIsFavourite().matches("1")) {
                    favourite_btn.setImageResource(android.R.drawable.btn_star_big_off);
                    Recipe.setIsFavourite("0");
                    DeleteFavouriteRecipe(Recipe);
                }


            }
        });
        return rootView;
    }

    private void AddFavouriteRecipe(RecipeObject Recipe) {
        ContentValues ContVal = new ContentValues();
        ContVal.put(DatabaseContract.COLUMN_Movie_ID, Recipe.getId());
        ContVal.put(DatabaseContract.COLUMN_Movie_NAME, Recipe.getName());
        ContVal.put(DatabaseContract.COLUMN_Rating, Recipe.getRating());
        ContVal.put(DatabaseContract.COLUMN_ImageURL, Recipe.getImageURL());
        ContVal.put(DatabaseContract.COLUMN_IngredientJsonString, Recipe.getIngredientJsonString());
        ContVal.put(DatabaseContract.COLUMN_SourceURL, Recipe.getSourceURL());
        ContVal.put(DatabaseContract.COLUMN_TotalTimeInSeconds, Recipe.getTotalTimeInSeconds());
        Uri uri = getContext().getContentResolver().insert(DatabaseContract.CONTENT_URI, ContVal);
        favouriteRecipesId.add(Recipe.getId());
        //widget data
        String value = Recipe.getIngredientJsonString();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Widget_data", value);
        editor.apply();
    }

    public void DeleteFavouriteRecipe(RecipeObject Recipe) {
        // Build appropriate uri with String row id appended
        Uri uri = DatabaseContract.CONTENT_URI;
        uri = uri.buildUpon().build();
        getContext().getContentResolver().delete(uri,
                DatabaseContract.COLUMN_Movie_ID + "=?", new String[]{Recipe.getId()});
        favouriteRecipesId.remove(Recipe.getId());

        if (Utilities.Istablet) {
            FavouriteRecipe.remove(Recipe);
            if (tabhost.getCurrentTab() == 1 && FavouriteRecipe.size() > 0) {

                Fragment newFragment = new DetailFragment();
                Bundle extras = new Bundle();
                extras.putParcelable("clickedRecipeObject", FavouriteRecipe.get(0));
                newFragment.setArguments(extras);
                FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                t.replace(R.id.head_container, newFragment);
                t.commit();

            }

        } else {
            for (int i = 0; i < FavouriteRecipe.size(); i++) {
                if (FavouriteRecipe.get(i).getId().matches(Recipe.getId())) {
                    FavouriteRecipe.remove(i);

                }
            }
        }
        Cursor cursor = getActivity().getContentResolver().query(DatabaseContract.CONTENT_URI,
                null,
                null,
                null,
                DatabaseContract.COLUMN_Movie_NAME);
        if(FavRecipeAdapter != null)
            FavRecipeAdapter.updateReceipesList(cursor);


    }

    public class FetchRecipesTask extends AsyncTask<Void, Void, RecipeObject> {

        private final String LOG_TAG = FetchRecipesTask.class.getSimpleName();

        private RecipeObject getRecipesDataFromJson(String RecipeJsonStr)
                throws JSONException {


            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(RecipeJsonStr, JsonElement.class);
            JsonObject jsonobj = jsonElement.getAsJsonObject();
            JsonArray RecipesArray = jsonobj.getAsJsonArray("ingredientLines");
            String IngredientsJson = RecipesArray.toString();

            Recipe.setIngredientJsonString(IngredientsJson);
            if (RecipesArray.size() == 0) {

            } else {
                ArrayList<String> ingredientList = new ArrayList<String>();
                for (int i = 0; i < RecipesArray.size(); i++) {
                    ingredientList.add(RecipesArray.get(i).getAsString());
                }
                // Get the JSON object
                Recipe.setIngredientsList(ingredientList);

                //Flavors data
                if (!jsonobj.get("source").isJsonNull()) {
                    JsonObject SourceJson = (JsonObject) jsonobj.get("source");

                    if (SourceJson != null) {
                        Recipe.setSourceURL(SourceJson.get("sourceRecipeUrl").getAsString());

                    }

                }
            }
            return Recipe;
        }

        @Override
        protected RecipeObject doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String RecipeJsonStr = null;
            try {
                String apiKey = "http://api.yummly.com/v1/api/recipe/" + Recipe.getId() + "?_app_id=" + The_Application_ID + "&_app_key=" + The_Application_Key;
                URL url = new URL(apiKey);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                RecipeJsonStr = buffer.toString();
            } catch (IOException e) {

                Log.e(LOG_TAG, getString(R.string.error), e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, getString(R.string.stream_error), e);
                    }
                }
            }
            try {
                return getRecipesDataFromJson(RecipeJsonStr);
            } catch (JSONException e) {

                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(RecipeObject result) {

            if (result == null) {
                makeText(getContext(), R.string.error_message, Toast.LENGTH_LONG).show();
            } else {
                SourceURLExist = true;
                IngredientAdapter =
                        new ListAdapter(
                                getContext(), Recipe.getIngredientsList());
                IngredientListView.setAdapter(IngredientAdapter);
                ingredients.setText(Recipe.getIngredientsList().size() + " Ingredients");

            }
        }

    }


}


