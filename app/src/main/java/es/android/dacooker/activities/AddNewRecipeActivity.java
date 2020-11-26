package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import es.android.dacooker.R;
import es.android.dacooker.adapters.AddRecipePagerAdapter;
import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;

public class AddNewRecipeActivity extends AppCompatActivity {

    private TabLayout tabsLayout;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize Adapter With Tabs
        tabsLayout = findViewById(R.id.add_recipe_tab_layout);
        viewPager = findViewById(R.id.add_recipe_viewPager);

        /*  Implementar si al cambiar Fragment se Borran Datos
        ArrayList<String> titles = new ArrayList<>();
        titles.add("Recipes");
        titles.add("Ingredients");
        titles.add("Potato");
        */

        pagerAdapter = new AddRecipePagerAdapter(getSupportFragmentManager());
        // Lo mismo que antes
        //pagerAdapter.addFragment(new AddRecipeFragment(), titles.get(0));
        //pagerAdapter.addFragment(new AddIngredientFragment(), titles.get(1));
        //pagerAdapter.addFragment(new AddStepFragment(), titles.get(2));

        viewPager.setAdapter(pagerAdapter);
        tabsLayout.setupWithViewPager(viewPager);

    }
}