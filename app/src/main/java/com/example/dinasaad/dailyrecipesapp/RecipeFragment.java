package com.example.dinasaad.dailyrecipesapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.dinasaad.dailyrecipesapp.data.DatabaseContract;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;
import static com.example.dinasaad.dailyrecipesapp.BuildConfig.The_Application_ID;
import static com.example.dinasaad.dailyrecipesapp.BuildConfig.The_Application_Key;
import static com.example.dinasaad.dailyrecipesapp.MainActivity.TASK_LOADER_ID;
import static com.example.dinasaad.dailyrecipesapp.MainActivity.favouriteRecipesId;
import static com.example.dinasaad.dailyrecipesapp.R.id.searchView;

/**
 * Created by DinaSaad on 06/10/2017.
 */

public class RecipeFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static ArrayList<RecipeObject> RecipeList = new ArrayList<RecipeObject>();
    ListView listView;
    ListView FavlistView;
    boolean search_value = false;
    private RecipeListAdaper IngredientAdapter;
    public static RecipeListAdaper FavRecipeAdapter;
    boolean savedInstanceStateValue = false;
    public static TabHost tabhost;
    SearchView v;
    int savedtab;
    ArrayList<Integer> ListFavourite = new ArrayList<Integer>();
    public static ArrayList<RecipeObject> FavouriteRecipe = new ArrayList<RecipeObject>();

    public RecipeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        if (savedInstanceState == null || !savedInstanceState.containsKey("SavedRecipeList")) {
            RecipeList = new ArrayList<RecipeObject>();


        } else {
            RecipeList = savedInstanceState.getParcelableArrayList("SavedRecipeList");
            savedtab = savedInstanceState.getInt("Savedtab");
            savedInstanceStateValue = true;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ListFavourite.clear();
        View rootView = inflater.inflate(R.layout.recipefragment_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.IngedientsList);
        FavlistView = (ListView) rootView.findViewById(R.id.FavouriteList);
        tabhost = (TabHost) rootView.findViewById(R.id.tabHost);
        v = (SearchView) rootView.findViewById(searchView);

        tabhost.setup();
        //Tab 1
        TabHost.TabSpec spec = tabhost.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Recipes");
        tabhost.addTab(spec);
        //Tab 2
        spec = tabhost.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Favourites");
        tabhost.addTab(spec);
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if ("Tab Two".equals(tabId)) {
                    v.onActionViewCollapsed();
                    FavouriteRecipe = new ArrayList<RecipeObject>();
                    Cursor cursor = null;
                    try {
                        cursor = getActivity().getContentResolver().query(DatabaseContract.CONTENT_URI,
                                null,
                                null,
                                null,
                                DatabaseContract.COLUMN_Movie_NAME);

                    } catch (Exception e) {
                        Log.e(TAG, getString(R.string.failed_load_data));
                        e.printStackTrace();
                    }
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            RecipeObject object = new RecipeObject();
                            object.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_Movie_NAME)));
                            object.setId(cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_Movie_ID)));
                            object.setRating(cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_Rating)));
                            object.setTotalTimeInSeconds(cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_TotalTimeInSeconds)));
                            object.setImageURL(cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_ImageURL)));
                            object.setIngredientJsonString(cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_IngredientJsonString)));
                            object.setSourceURL(cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_SourceURL)));
                            object.setIsFavourite("1");

                            FavouriteRecipe.add(object);
                            cursor.moveToNext();
                        }

                        FavRecipeAdapter =
                                new RecipeListAdaper(
                                        getContext(), FavouriteRecipe);
                        FavRecipeAdapter.notifyDataSetChanged();
                        FavlistView.setAdapter(FavRecipeAdapter);

                        if (Utilities.Istablet) {

                            Fragment newFragment = new DetailFragment();
                            Bundle extras = new Bundle();
                            savedInstanceStateValue = true;
                            extras.putParcelable("clickedRecipeObject", FavouriteRecipe.get(0));
                            newFragment.setArguments(extras);
                            FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                            t.replace(R.id.head_container, newFragment);
                            t.commit();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.no_favourite_recipe, Toast.LENGTH_SHORT).show();
                    }
                }

                if ("Tab One".equals(tabId) && !search_value) {

                    onStart();

                    if (Utilities.Istablet) {
                        Fragment newFragment = new DetailFragment();
                        Bundle extras = new Bundle();
                        savedInstanceStateValue = true;
                        extras.putParcelable("clickedRecipeObject", RecipeList.get(0));
                        newFragment.setArguments(extras);
                        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                        t.replace(R.id.head_container, newFragment);
                        t.commit();
                    }

                }

            }

        });
        tabhost.setCurrentTab(savedtab);
        if (savedInstanceState == null || !savedInstanceState.containsKey("SavedRecipeList")) {
        } else {
            IngredientAdapter =
                    new RecipeListAdaper(
                            getContext(), RecipeList);
            IngredientAdapter.notifyDataSetChanged();
            listView.setAdapter(IngredientAdapter);

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (Utilities.Istablet) {
                    Fragment newFragment = new DetailFragment();
                    Bundle extras = new Bundle();
                    savedInstanceStateValue = true;
                    extras.putParcelable("clickedRecipeObject", RecipeList.get(position));
                    newFragment.setArguments(extras);
                    FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                    t.replace(R.id.head_container, newFragment);
                    t.commit();
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle extras = new Bundle();
                    savedInstanceStateValue = true;
                    extras.putParcelable("clickedRecipeObject", RecipeList.get(position));
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });
        FavlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (Utilities.Istablet) {
                    Fragment newFragment = new DetailFragment();
                    Bundle extras = new Bundle();
                    savedInstanceStateValue = true;
                    extras.putParcelable("clickedRecipeObject", FavouriteRecipe.get(position));
                    newFragment.setArguments(extras);
                    FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                    t.replace(R.id.head_container, newFragment);
                    t.commit();
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle extras = new Bundle();
                    savedInstanceStateValue = true;
                    extras.putParcelable("clickedRecipeObject", FavouriteRecipe.get(position));
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });

        v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.contains(" ")) {
                    query = query.replaceAll(" ", "+");

                }
                search_value = true;
                String apiKey = "http://api.yummly.com/v1/api/recipes?_app_id=" + The_Application_ID + "&_app_key=" + The_Application_Key + "&q=" + query;
                FetchRecipesTask Task = new FetchRecipesTask();
                Task.execute(apiKey);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("SavedRecipeList", RecipeList);
        outState.putInt("Savedtab", tabhost.getCurrentTab());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!savedInstanceStateValue) {
            String apiKey = "https://api.yummly.com/v1/api/recipes?_app_id=" + The_Application_ID + "&_app_key=" + The_Application_Key;
            FetchRecipesTask Task = new FetchRecipesTask();
            Task.execute(apiKey);
        }
    }

    public class FetchRecipesTask extends AsyncTask<String, Void, ArrayList<RecipeObject>> {

        private final String LOG_TAG = FetchRecipesTask.class.getSimpleName();

        private ArrayList<RecipeObject> getRecipesDataFromJson(String RecipeJsonStr)
                throws JSONException {
            RecipeList = new ArrayList<RecipeObject>();
            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(RecipeJsonStr, JsonElement.class);
            JsonObject jsonobj = jsonElement.getAsJsonObject();

            JsonArray RecipesArray = jsonobj.getAsJsonArray("matches");
            if (RecipesArray.size() == 0) {

            } else {
                for (int i = 0; i < RecipesArray.size(); i++) {
                    // Get the JSON object
                    RecipeObject NewRecipeObject = new RecipeObject();
                    JsonObject RecipeObject = (JsonObject) RecipesArray.get(i);
                    NewRecipeObject.setId(RecipeObject.get("id").getAsString());
                    if (favouriteRecipesId.size() != 0) {
                        if (favouriteRecipesId.contains(NewRecipeObject.getId())) {
                            NewRecipeObject.setIsFavourite("1");
                            FavouriteRecipe.add(NewRecipeObject);
                        }
                    } else
                        NewRecipeObject.setIsFavourite("0");
                    NewRecipeObject.setName(RecipeObject.get("recipeName").getAsString());
                    NewRecipeObject.setRating(RecipeObject.get("rating").getAsString());
                    NewRecipeObject.setTotalTimeInSeconds(RecipeObject.get("totalTimeInSeconds").getAsString());

                    JsonObject IngredientimageURL = RecipeObject.get("imageUrlsBySize").getAsJsonObject();
                    if (IngredientimageURL != null) {

                        String imageSize = IngredientimageURL.get("90").getAsString();
                        imageSize = imageSize.replace("s90-c", "s500-c");
                        NewRecipeObject.setImageURL(imageSize);
                    }

                    RecipeList.add(NewRecipeObject);
                }
            }
            return RecipeList;
        }

        @Override
        protected ArrayList<RecipeObject> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String RecipeJsonStr = null;
            try {
                String apiKey = String.valueOf(params[0]);
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
                        Log.e(LOG_TAG, getString(R.string.stream_error) , e);
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
        protected void onPostExecute(ArrayList<RecipeObject> result) {

            if (result == null) {
                makeText(getContext(), R.string.error_message, Toast.LENGTH_LONG).show();
            } else if (result.size() == 0) {
                makeText(getContext(), R.string.noresults_message, Toast.LENGTH_LONG).show();
            } else {
                if (!savedInstanceStateValue || search_value) {
                    IngredientAdapter =
                            new RecipeListAdaper(
                                    getContext(), result);
                    listView.setAdapter(IngredientAdapter);
                    if (search_value) {
                        v.onActionViewCollapsed();
                        tabhost.setCurrentTab(0);
                        search_value = false;
                    }
                }
                if (Utilities.Istablet) {
                    Fragment newFragment = new DetailFragment();
                    Bundle extras = new Bundle();
                    savedInstanceStateValue = true;
                    extras.putParcelable("clickedRecipeObject", RecipeList.get(0));
                    newFragment.setArguments(extras);
                    FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                    t.replace(R.id.head_container, newFragment);
                    t.commit();
                }

            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(getContext()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;


            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {

                forceLoad();
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getActivity().getContentResolver().query(DatabaseContract.CONTENT_URI,
                            null,
                            null,
                            null,
                            DatabaseContract.COLUMN_Movie_NAME);

                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.failed_load_data));
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                favouriteRecipesId = new ArrayList<String>();

                mTaskData = data;
                data.moveToFirst();
                //cursor.moveToPosition(i);
                while (!data.isAfterLast()) {
                    String x = data.getString(data.getColumnIndex(DatabaseContract.COLUMN_Movie_ID));
                    if (!favouriteRecipesId.contains(x)) {
                        favouriteRecipesId.add(x);
                    }
                    data.moveToNext();
                }


                super.deliverResult(data);
            }
        };

    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        if (FavRecipeAdapter != null)
            FavRecipeAdapter.updateReceipesList(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
