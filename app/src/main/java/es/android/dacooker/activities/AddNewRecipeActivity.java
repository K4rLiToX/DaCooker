package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.adapters.AddRecipePagerAdapter;
import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;

public class AddNewRecipeActivity extends AppCompatActivity {

    private TabLayout tabsLayout;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        // Initialize Adapter With Tabs
        btnFinish = findViewById(R.id.btnFinish_activity);
        btnFinish.setVisibility(View.INVISIBLE);
        tabsLayout = findViewById(R.id.add_recipe_tab_layout);
        viewPager = findViewById(R.id.add_recipe_viewPager);

        pagerAdapter = new AddRecipePagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        tabsLayout.setupWithViewPager(viewPager);


        tabsLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabsLayout.getSelectedTabPosition() == 2)
                    btnFinish.setVisibility(View.VISIBLE);
                else btnFinish.setVisibility(View.INVISIBLE);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}