package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.adapters.AddIngredientRecyclerAdapter;
import es.android.dacooker.adapters.AddRecipePagerAdapter;
import es.android.dacooker.adapters.AddStepRecyclerAdapter;
import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.MealType;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.models.StepModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;

public class AddNewRecipeActivity extends AppCompatActivity {

    private TabLayout tabsLayout;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;
    private Button btnFinish, btnValidate;

    private Fragment add_recipe;
    private Fragment add_ingredients;
    private Fragment add_steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        // Initialize View Elements
        initializeElements();

        //Initialize Adapter
        initializeViewPager();

        tabsLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tabsLayout.getSelectedTabPosition() == 2)
                    btnFinish.setVisibility(View.VISIBLE);
                else btnFinish.setVisibility(View.INVISIBLE);

                if(tabsLayout.getSelectedTabPosition() == 1)
                    btnValidate.setVisibility(View.VISIBLE);
                else btnValidate.setVisibility(View.INVISIBLE);

                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Buttons onClick
        btnValidate.setOnClickListener(view -> {
            this.validateRecipeFields();
        });

        btnFinish.setOnClickListener(view -> {
            this.finishRecipe();
        });

    }

    private void initializeElements(){

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnFinish = findViewById(R.id.btnFinish_activity);
        btnValidate = findViewById(R.id.btnValidate_activity);
        btnFinish.setVisibility(View.INVISIBLE);
        tabsLayout = findViewById(R.id.add_recipe_tab_layout);
        viewPager = findViewById(R.id.add_recipe_viewPager);
    }

    private void initializeViewPager(){
        pagerAdapter = new AddRecipePagerAdapter(getSupportFragmentManager());

        add_recipe = new AddRecipeFragment();
        add_ingredients = new AddIngredientFragment();
        add_steps = new AddStepFragment();

        pagerAdapter.addFragment(add_recipe, "Recipe");
        pagerAdapter.addFragment(add_ingredients, "Ingredients");
        pagerAdapter.addFragment(add_steps, "Steps");

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsLayout.setupWithViewPager(viewPager);
    }

    private RecipeModel getRecipeData(){
        String name, description, hours, minutes;
        String mtSelection;
        MealType mt = null;
        View v = add_recipe.getView();

        //Get Information
        name = ((EditText) v.findViewById(R.id.recipe_name_input)).getText().toString();
        description = ((EditText) v.findViewById(R.id.recipe_description_input)).getText().toString();
        hours = ((EditText) v.findViewById(R.id.recipe_hour_input)).getText().toString();
        minutes = ((EditText) v.findViewById(R.id.recipe_minute_input)).getText().toString();
        mtSelection = ((AutoCompleteTextView) v.findViewById(R.id.recipe_mealType_dropdown_select)).getText().toString();

        Log.e("MT: ", mtSelection+"");

        if(!mtSelection.trim().equals("") && mtSelection != null)
            mt = MealType.valueOf(mtSelection);

        //Prepare Recipe
        RecipeModel r = new RecipeModel();
        r.setRecipeName(name);
        r.setRecipeDescription(description);
        r.setExecutionTime(hours+":"+minutes);
        r.setMealType(mt);

        return r;
    }

    private List<IngredientModel> getIngredientsData(){
        View add_ing = add_ingredients.getView();
        RecyclerView rw = add_ing.findViewById(R.id.add_ingredient_recycler);
        AddIngredientRecyclerAdapter rwA = (AddIngredientRecyclerAdapter) rw.getAdapter();
        return rwA.getList();

    }

    private List<StepModel> getStepsData(){
        View add_step = add_steps.getView();
        RecyclerView rw = add_step.findViewById(R.id.add_step_recyclerView);
        AddStepRecyclerAdapter rwA = (AddStepRecyclerAdapter) rw.getAdapter();
        return rwA.getStepModelList();
    }

    private boolean validateRecipeFields(){
        RecipeModel r = this.getRecipeData();

        String[] time =  r.getExecutionTime().split(":");

        String error = "";

        if(r.getRecipeName().trim().equalsIgnoreCase("") || r.getRecipeName().length() > 60)
            error = "Recipe name must be between 0 and 60 characters";
        else if(r.getRecipeDescription().length() > 140)
            error = "Description must be between 0-140 characters";
        else if(time.length <= 0 || time[0].trim().equalsIgnoreCase("") || Integer.parseInt(time[0]) < 0)
            error = "Hours must be a number. If not necessary, put 0 in it";
        else if(time.length <= 1 || time[1].trim().equalsIgnoreCase("") || Integer.parseInt(time[1]) < 0 || Integer.parseInt(time[1]) > 59)
            error = "Minutes must be a number. Use 0 to 59 to fill";
        else if(r.getMealType() == null) error = "You have to use a MealType";

        if(error.trim().length() != 0) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return false;
        } else return true;

    }

    private void finishRecipe() {
        if(this.validateRecipeFields()) {
            try {
                RecipeModel r = this.getRecipeData();
                r.setTimesCooked(0);
                BBDD_Helper dbHelper = new BBDD_Helper(this);
                BD_Operations.addRecipe(r, dbHelper);

                int idRecipe = BD_Operations.getLastID(dbHelper);

                if(idRecipe == -1) throw new Exception();

                for(IngredientModel ing : this.getIngredientsData()){
                    ing.setIdRecipe(idRecipe);
                    BD_Operations.addIngredient(ing, idRecipe, dbHelper);
                }

                for(StepModel s : this.getStepsData()){
                    s.setRecipe_ID(idRecipe);
                    BD_Operations.addStep(s, idRecipe, dbHelper);
                }

                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Error during addition", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}