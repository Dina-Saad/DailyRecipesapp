package com.example.dinasaad.dailyrecipesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int TASK_LOADER_ID = 0;
    public static ArrayList<String> favouriteRecipesId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable(this)) {

            setContentView(R.layout.activity_main);
            Utilities.Istablet = findViewById(R.id.tabletcontainer) != null;
        } else {
            // Provide feedback to user
            setContentView(R.layout.nointernet);
            Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (isNetworkAvailable(context)) {
                        Intent intent = new Intent(MainActivity.this,
                                MainActivity.class);

                        startActivity(intent);
                    }
                }
            });
        }

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
