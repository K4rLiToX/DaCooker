package es.android.dacooker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.android.dacooker.R;
import es.android.dacooker.fragments.CustomFragment;
import es.android.dacooker.fragments.MostUsedFragment;
import es.android.dacooker.fragments.RecipeFragment;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.utilities.SingletonMap;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    //Constants
    public static final String SHARED_DB_DATA_KEY = "SHARED_DB_KEY";
    private final Context MAIN_ACTIVITY_CONTEXT = MainActivity.this;

    public BBDD_Helper db;

    //Main Fragments
    private final RecipeFragment recipeFragment = new RecipeFragment();
    private final MostUsedFragment mostUsedFragment = new MostUsedFragment();
    private final CustomFragment customFragment = new CustomFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bottom Navbar setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_recipes);
        setTitle(R.string.recipes_label);

        //Database setup
        db = new BBDD_Helper(MAIN_ACTIVITY_CONTEXT);
        SingletonMap.getInstance().put(SHARED_DB_DATA_KEY, db);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean res = false;

        if(itemID == R.id.menu_recipes){
            changeFragment(recipeFragment, R.string.recipes_label);
            res = true;
        } else if(itemID == R.id.menu_most_used){
            changeFragment(mostUsedFragment, R.string.most_used_recipes_label);
            res = true;
        } else if(itemID == R.id.menu_custom){
            changeFragment(customFragment, R.string.custom_recipes_label);
            res = true;
        } else {
            res = false;
        }

        return res;
    }


    /*Private Methods*/
    private void changeFragment(Fragment fragmentToChange, int title){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentToChange).commit();
        setTitle(title);
    }

}