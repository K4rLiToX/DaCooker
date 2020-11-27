package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<< Updated upstream
import android.app.Fragment;
=======
import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
>>>>>>> Stashed changes
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
<<<<<<< Updated upstream

=======
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import java.util.List;
>>>>>>> Stashed changes
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;

public class AddNewRecipeActivity extends AppCompatActivity {

<<<<<<< Updated upstream
    //Fragments
    private final AddRecipeFragment addRecipeFragment= new AddRecipeFragment();
=======
    private TabLayout tabsLayout;
    private ViewPager viewPager;
    AddRecipePagerAdapter pagerAdapter;
    private Button btnFinish, btnValidate;
>>>>>>> Stashed changes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.add_recipe_fragment_container, addRecipeFragment).commit();
    }
<<<<<<< Updated upstream
=======

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private RecipeModel getRecipeData(){
        String name, description, hours, minutes;
        ImageView iv;
        BitmapDrawable image;
        String mtSelection;
        MealType mt = null;
        View v = add_recipe.getView();

        //Get Information
        name = ((EditText) v.findViewById(R.id.recipe_name_input)).getText().toString();
        iv = ((ImageView) v.findViewById(R.id.recipe_img_input));
        image = (BitmapDrawable) iv.getDrawable();
        description = ((EditText) v.findViewById(R.id.recipe_description_input)).getText().toString();
        hours = ((EditText) v.findViewById(R.id.recipe_hour_input)).getText().toString();
        minutes = ((EditText) v.findViewById(R.id.recipe_minute_input)).getText().toString();
        mtSelection = ((AutoCompleteTextView) v.findViewById(R.id.recipe_mealType_dropdown_select)).getText().toString();

        Log.e("MT: ", mtSelection+"");

        if(!mtSelection.trim().equals("") && mtSelection != null)
            mt = MealType.valueOf(mtSelection);

        //Prepare Recipe
        RecipeModel r = new RecipeModel();



        if(!image.getBitmap().equals(((BitmapDrawable) getDrawable(R.drawable.img_recipe_card_default)).getBitmap())){
            r.setImage(image.getBitmap());
        }
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

>>>>>>> Stashed changes
}