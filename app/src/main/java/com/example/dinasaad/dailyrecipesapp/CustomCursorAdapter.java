package com.example.dinasaad.dailyrecipesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dinasaad.dailyrecipesapp.data.DatabaseContract;
import com.squareup.picasso.Picasso;

import static com.example.dinasaad.dailyrecipesapp.R.id.Recipe_name;

/**
 * Created by DinaSaad on 27/08/2017.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.TaskViewHolder> {
    Context mContext;
    private Cursor mCursor;
    private LayoutInflater mInflater;

    public CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false);

        return new TaskViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getColumnIndex(DatabaseContract._ID);
        String recipeName = String.valueOf(mCursor.getColumnIndex(DatabaseContract.COLUMN_Movie_NAME));
        String recipeImage = String.valueOf(mCursor.getColumnIndex(DatabaseContract.COLUMN_ImageURL));

        mCursor.moveToPosition(position); // get to the right location in the cursor


        //Set values
        holder.recipe_name.setText(recipeName);
        if (recipeImage != null) {
            Picasso.with(mContext).load(recipeImage).into(holder.imageview_detail);
        }


    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        TextView recipe_name;
        ImageView imageview_detail;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(View itemView) {
            super(itemView);

            recipe_name = (TextView) itemView.findViewById(Recipe_name); // title
            imageview_detail = (ImageView) itemView.findViewById(R.id.RecipeImage);

        }
    }
}

