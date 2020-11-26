package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;

public class AddNewRecipeActivity extends AppCompatActivity {

    private Button btnToIngredients, btnToSteps, btnFinish;

    //Fragments
    private final AddRecipeFragment addRecipeFragment= new AddRecipeFragment();
    private final AddIngredientFragment addIngredientFragment = new AddIngredientFragment();
    private final AddStepFragment addStepFragment = new AddStepFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.add_recipe_fragment_container, addRecipeFragment).commit();
        btnToIngredients = findViewById(R.id.toAddIngredient);
        btnToSteps = findViewById(R.id.toAddStep);
        btnFinish = findViewById(R.id.btnFinish_activity);

        btnToIngredients.setVisibility(View.VISIBLE);

        btnToIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.add_recipe_fragment_container, addIngredientFragment).commit();
                btnToIngredients.setVisibility(View.GONE);
                btnToSteps.setVisibility(View.VISIBLE);
            }
        });

        btnToSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.add_recipe_fragment_container, addStepFragment).commit();
                btnToSteps.setVisibility(View.GONE);
                btnFinish.setVisibility(View.VISIBLE);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFinish.setVisibility(View.GONE);
                finish();
            }
        });

    }
}