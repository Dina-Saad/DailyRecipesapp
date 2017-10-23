package com.example.dinasaad.dailyrecipesapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by DinaSaad on 14/08/2017.
 */

public class RecipeObject implements Parcelable {
    String Id;
    String Name;
    String rating;
    String ImageURL;
    String SourceURL;
    String IsFavourite;
    String IngredientJsonString;
    ArrayList<String> IngredientsList = new ArrayList<String>();
    ArrayList<String> flavorsList = new ArrayList<String>();//sweet|meaty|sour|bitter|salty|piquant
    String totalTimeInSeconds;

    public RecipeObject() {

    }

    //constructor
    public RecipeObject(String Id,
                        String Name,
                        String rating,
                        String ImageURL,
                        String SourceURL,
                        String IsFavourite,
                        String IngredientJsonString,
                        ArrayList<String> IngredientsList,
                        ArrayList<String> flavorsList,
                        String totalTimeInSeconds) {
        this.Id = Id;
        this.Name = Name;
        this.rating = rating;
        this.ImageURL = ImageURL;
        this.SourceURL = SourceURL;
        this.IsFavourite = IsFavourite;
        this.IngredientJsonString = IngredientJsonString;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.IngredientsList = IngredientsList;
        this.flavorsList = flavorsList;

    }

    public RecipeObject(Parcel in) {
        String[] data = new String[8];
        in.readStringArray(data);

        this.Id = data[0];
        this.Name = data[1];
        this.rating = data[2];
        this.ImageURL = data[3];
        this.SourceURL = data[4];
        this.IsFavourite = data[5];
        this.IngredientJsonString = data[6];
        this.totalTimeInSeconds = data[7];
        in.readStringList(IngredientsList);
        in.readStringList(flavorsList);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.Id,
                this.Name,
                this.rating,
                this.ImageURL,
                this.SourceURL,
                this.IsFavourite,
                this.IngredientJsonString,
                this.totalTimeInSeconds});
        dest.writeStringList(IngredientsList);
        dest.writeStringList(flavorsList);
    }

    public static final Creator CREATOR
            = new Creator() {

        @Override
        public RecipeObject createFromParcel(Parcel in) {
            return new RecipeObject(in);
        }

        @Override
        public RecipeObject[] newArray(int size) {
            return new RecipeObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getSourceURL() {
        return SourceURL;
    }

    public void setSourceURL(String sourceURL) {
        SourceURL = sourceURL;
    }

    public String getIsFavourite() {
        return IsFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        IsFavourite = isFavourite;
    }

    public ArrayList<String> getIngredientsList() {
        return IngredientsList;
    }

    public void setIngredientsList(ArrayList<String> ingredientsList) {
        IngredientsList = ingredientsList;
    }

    public String getIngredientJsonString() {
        return IngredientJsonString;
    }

    public void setIngredientJsonString(String ingredientJsonString) {
        IngredientJsonString = ingredientJsonString;
    }

    public ArrayList<String> getFlavorsList() {
        return flavorsList;
    }

    public void setFlavorsList(ArrayList<String> flavorsList) {
        this.flavorsList = flavorsList;
    }

    public String getTotalTimeInSeconds() {
        return totalTimeInSeconds;
    }

    public void setTotalTimeInSeconds(String totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
    }
}
