package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;

public class AddNewRecipeActivity extends AppCompatActivity {

    //Fragments
    private final AddRecipeFragment addRecipeFragment= new AddRecipeFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.add_recipe_fragment_container, addRecipeFragment).commit();
    }
}