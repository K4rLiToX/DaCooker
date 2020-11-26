package es.android.dacooker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.adapters.RecyclerViewAdapter;
import es.android.dacooker.fragments.CustomFragment;
import es.android.dacooker.fragments.MostUsedFragment;
import es.android.dacooker.fragments.RecipeFragment;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    //Main Fragments
    private final RecipeFragment recipeFragment = new RecipeFragment();
    private final MostUsedFragment mostUsedFragment = new MostUsedFragment();
    private final CustomFragment customFragment = new CustomFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Views*/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_recipes);
        setTitle(R.string.recipes_label);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean res = false;

        if(itemID == R.id.menu_recipes){
            changeFragment(recipeFragment);
            setTitle(R.string.recipes_label);
            res = true;
        } else if(itemID == R.id.menu_most_used){
            changeFragment(recipeFragment);
            setTitle(R.string.recipes_label);
            res = true;
        } else if(itemID == R.id.menu_custom){
            changeFragment(recipeFragment);
            setTitle(R.string.recipes_label);
            res = true;
        } else {
            res = false;
        }

        return res;
    }

    private void changeFragment(Fragment fragmentToChange){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentToChange).commit();
    }

}