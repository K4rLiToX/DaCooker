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
import es.android.dacooker.utilities.SingletonMap;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    //Main Fragments
    private final RecipeFragment recipeFragment = new RecipeFragment();
    private final MostUsedFragment mostUsedFragment = new MostUsedFragment();
    private final CustomFragment customFragment = new CustomFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_recipes);
        bottomNavigationView.getMenu().getItem(1).setCheckable(true).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if(itemID == R.id.menu_recipes){
            changeFragment(recipeFragment, R.string.recipes_label);
            return true;
        } else if(itemID == R.id.menu_most_used){
            changeFragment(mostUsedFragment, R.string.most_used_recipes_label);
            return true;
        } else if(itemID == R.id.menu_custom){
            changeFragment(customFragment, R.string.custom_recipes_label);
            return true;
        } else {
            return false;
        }
    }



    private void changeFragment(Fragment fragmentToChange, int title){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentToChange).commit();
        setTitle(title);
    }

}