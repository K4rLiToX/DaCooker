package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

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
import es.android.dacooker.utilities.SingletonMap;

public class AddNewRecipeActivity extends AppCompatActivity {

    private TabLayout tabsLayout;
    private ViewPager viewPager;
    AddRecipePagerAdapter pagerAdapter;
    private Button btnFinish, btnValidate;

    private Fragment add_recipe;
    private Fragment add_ingredients;
    private Fragment add_steps;

    //Edition Mode
    boolean forEdit;
    private RecipeModel rEdit;
    private List<IngredientModel> ingList;
    private List<StepModel> stepList;

    //SingletonMap Keys
    private final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";
    private final String SHARE_INGLIST_KEY = "SHARED_INGLIST_KEY";
    private final String SHARE_STEPLIST_KEY = "SHARED_STEPLIST_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        //Is edition?
        Bundle b = getIntent().getExtras();
        if(b != null) this.forEdit = b.getBoolean("edit");

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

                if(tabsLayout.getSelectedTabPosition() == 0)
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
            if(!forEdit) this.finishRecipe();
            else this.updateRecipe();
        });

    }

    private void initializeElements(){

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnFinish = findViewById(R.id.btnFinish_activity);
        btnValidate = findViewById(R.id.btnValidate_activity);
        btnFinish.setVisibility(View.INVISIBLE);
        tabsLayout = findViewById(R.id.add_recipe_tab_layout);
        viewPager = findViewById(R.id.add_recipe_viewPager);

        if(forEdit) {
            btnFinish.setText(getString(R.string.btnUpdate));
            setTitle(R.string.edit_title);
        }

    }

    private void initializeViewPager(){
        pagerAdapter = new AddRecipePagerAdapter(getSupportFragmentManager());

        add_recipe = new AddRecipeFragment();
        add_ingredients = new AddIngredientFragment();
        add_steps = new AddStepFragment();

        pagerAdapter.addFragment(add_recipe, getString(R.string.add_recipe_fragment_title));
        pagerAdapter.addFragment(add_ingredients, getString(R.string.add_ingredients_fragment_title));
        pagerAdapter.addFragment(add_steps, getString(R.string.add_steps_fragment_title));

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsLayout.setupWithViewPager(viewPager);
    }

    private RecipeModel getRecipeData(){
        String name, description, hours, minutes;
        String mtSelection;
        MealType mt = null;
        ImageView image;
        BitmapDrawable bitmapDrawable;
        Bitmap bitmap;
        View v = add_recipe.getView();

        //Get Information
        name = ((EditText) v.findViewById(R.id.recipe_name_input)).getText().toString();
        image = v.findViewById(R.id.recipe_img_input);
        bitmapDrawable = (BitmapDrawable) image.getDrawable();
        bitmap = bitmapDrawable.getBitmap();
        description = ((EditText) v.findViewById(R.id.recipe_description_input)).getText().toString();
        hours = ((EditText) v.findViewById(R.id.recipe_hour_input)).getText().toString();
        minutes = ((EditText) v.findViewById(R.id.recipe_minute_input)).getText().toString();
        mtSelection = ((AutoCompleteTextView) v.findViewById(R.id.recipe_mealType_dropdown_select)).getText().toString();

        if(!mtSelection.trim().equals("") && mtSelection != null)
            mt = MealType.valueOf(mtSelection);

        int h = 0, min = 0;
        if(hours == null || hours.equalsIgnoreCase("")) h = -1;
        else h = Integer.parseInt(hours);
        if(minutes == null || minutes.equalsIgnoreCase("")) min = -1;
        else min = Integer.parseInt(minutes);

        //Prepare Recipe
        RecipeModel r = new RecipeModel();
        r.setRecipeName(name);

        Bitmap default_img = ((BitmapDrawable) getDrawable(R.mipmap.img_recipe_card_default)).getBitmap();
        if(!bitmap.equals(default_img)) r.setImage(bitmap);
        else r.setImage(null);
        r.setRecipeDescription(description);
        r.setExecutionTimeHour(h);
        r.setExecutionTimeMinute(min);
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

        String error = "";

        if(r.getRecipeName().trim().equalsIgnoreCase("") || r.getRecipeName().length() > 60)
            error = getString(R.string.validation_err_name);
        else if(r.getRecipeDescription().length() > 140)
            error = getString(R.string.validation_err_description);
        else if(r.getExecutionTimeHour() < 0)
            error = getString(R.string.validation_err_hour);
        else if(r.getExecutionTimeMinute() < 0 || r.getExecutionTimeMinute() > 59)
            error = getString(R.string.validation_err_minute);
        else if(r.getMealType() == null) error = getString(R.string.validation_err_mealtype);

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
                Toast.makeText(this, getString(R.string.err_addition_recipe), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    //Edition
    public void callFromEditFragment(View vRecipe, View vIng, View vStep){
        if(forEdit) {
            if(vRecipe != null) prepareEditionRecipe(vRecipe);
            if(vIng != null) prepareEditionIngredients(vIng);
            if(vStep != null) prepareEditionSteps(vStep);
        }
    }

    private void prepareEditionRecipe(View v){

        this.rEdit = (RecipeModel) SingletonMap.getInstance().get(SHARE_RECIPE_KEY);
        String description = "";
        Bitmap image;

        //SET RECIPE DATA //View v = this.add_recipe.getView();
        ((EditText) v.findViewById(R.id.recipe_name_input)).setText(rEdit.getRecipeName());

        if (rEdit.getRecipeDescription() != null) description = rEdit.getRecipeDescription();
        ((EditText) v.findViewById(R.id.recipe_description_input)).setText(description);

        ((EditText) v.findViewById(R.id.recipe_hour_input)).setText(String.valueOf(rEdit.getExecutionTimeHour()));
        ((EditText) v.findViewById(R.id.recipe_minute_input)).setText(String.valueOf(rEdit.getExecutionTimeMinute()));
        ((AutoCompleteTextView) v.findViewById(R.id.recipe_mealType_dropdown_select))
                .setText(rEdit.getMealType().name(), false);

        if (rEdit.getImage() == null) image = ((BitmapDrawable)
            getDrawable(R.mipmap.img_recipe_card_default)).getBitmap();
        else image = rEdit.getImage();
        ((ImageView) v.findViewById(R.id.recipe_img_input)).setImageBitmap(image);

    }

    private void prepareEditionIngredients(View v){

        this.ingList = (List<IngredientModel>) SingletonMap.getInstance().get(SHARE_INGLIST_KEY);

        //SET INGREDIENTS DATA //View v = this.add_ingredients.getView();
        ((AddIngredientRecyclerAdapter) ((RecyclerView) v.findViewById(R.id.add_ingredient_recycler))
                .getAdapter()).setEditList(this.ingList);

    }

    private void prepareEditionSteps(View v){
        //SET STEP DATA //View v = this.add_step.getView();
        this.stepList = (List<StepModel>) SingletonMap.getInstance().get(SHARE_STEPLIST_KEY);

        //SET INGREDIENTS DATA //View v = this.add_ingredients.getView();
        ((AddStepRecyclerAdapter) ((RecyclerView) v.findViewById(R.id.add_step_recyclerView))
                .getAdapter()).setEditList(this.stepList);
    }

    private void updateRecipe(){

        if(this.validateRecipeFields()) {
            try {
                RecipeModel r = this.getRecipeData();
                r.setId(rEdit.getId());
                BBDD_Helper dbHelper = new BBDD_Helper(this);
                BD_Operations.updateRecipe(r, dbHelper);

                if(r.getId() == -1) throw new Exception();

                BD_Operations.deleteIngredientsFromRecipeId(r.getId(), dbHelper);

                for(IngredientModel ing : this.getIngredientsData()){
                    ing.setIdRecipe(r.getId());
                    BD_Operations.addIngredient(ing, r.getId(), dbHelper);
                }

                BD_Operations.deleteStepsFromRecipeId(r.getId(), dbHelper);
                int o = 1;
                for(StepModel s : this.getStepsData()){
                    s.setStepOrder(o);
                    s.setRecipe_ID(r.getId());
                    BD_Operations.addStep(s, r.getId(), dbHelper);
                    o++;
                }

                finish();

            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.err_update_recipe), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    }

}