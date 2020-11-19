package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.android.dacooker.R;

public class MainActivity extends AppCompatActivity {
    /*Constants*/
    private final Context activityContext = MainActivity.this;

    /*Views*/
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //Set bottom navigation listener from changing activity
        this.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_most_used:
                    gotoActivity(MostUsedActivity.class);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menu_recipes:
                    return true;
                case R.id.menu_custom:
                    gotoActivity(CustomActivity.class);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    /*On click methods*/
    public void addRecipe(View view){
        showToast("Button Add Selected");
    }

    /*Private Methods*/

    //Assigns views
    private void initViews() {
        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        //Set custom selected
        this.bottomNavigationView.setSelectedItemId(R.id.menu_recipes);
    }
    //Changes activity given a class
    private void gotoActivity(Class aClass){
        startActivity(new Intent(activityContext, aClass));
    }

    //Shows a Toast
    private void showToast(String msg){
        Toast.makeText(activityContext, msg, Toast.LENGTH_LONG).show();
    }
}