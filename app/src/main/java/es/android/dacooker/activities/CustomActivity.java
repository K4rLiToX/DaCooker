package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.android.dacooker.R;

public class CustomActivity extends AppCompatActivity {

    @Override
    public void onBackPressed(){

    }

    /*Constants*/
    private final Context activityContext = CustomActivity.this;

    /*Views*/
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        initViews();

        //Set bottom navigation listener from changing activity
        this.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_most_used:
                    gotoActivity(MostUsedActivity.class);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menu_recipes:
                    gotoActivity(MainActivity.class);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menu_custom:
                    return true;
            }
            return false;
        });
    }


    /*Private Methods*/

    //Assigns views
    private void initViews() {
        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        //Set custom selected
        this.bottomNavigationView.setSelectedItemId(R.id.menu_custom);
    }

    //Changes activity given a class
    private void gotoActivity(Class aClass){
        startActivity(new Intent(activityContext, aClass));
    }
}