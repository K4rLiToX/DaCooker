package es.android.dacooker.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.adapters.IngredientRecyclerAdapter;
import es.android.dacooker.exceptions.IngredientException;
import es.android.dacooker.exceptions.StepException;
import es.android.dacooker.fragments.RecipeFragment;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.models.StepModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;


public class RecipeDetails extends AppCompatActivity {

    //SingletonMap Key
    private final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";
    private final String SHARE_INGLIST_KEY = "SHARED_INGLIST_KEY";
    private final String SHARE_STEPLIST_KEY = "SHARED_STEPLIST_KEY";

    //Views
    private ImageView imgRecipeDetail, imgIngredientRecyclerViewIcon;
    private TextView tvRecipeTitleDetail, tvRecipeTimeDetail, tvRecipeMealType, tvRecipeDescription;
    private RecyclerView ingredientRecyclerView;
    Button btnStartRecipe;
    LinearLayout expandableLayout;

    //Utilities
    IngredientRecyclerAdapter ingredientAdapter;
    private boolean isExpanded;

    //Recipe to Show
    private RecipeModel recipeSelected;

    //Ingredients and Steps Lists
    private List<IngredientModel> ingredientList;
    private List<StepModel> stepList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Toolbar detail_toolbar = findViewById(R.id.recipe_detail_app_bar);
        setSupportActionBar(detail_toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initViews();
        initParameters();
        setViews();
        initIngredientRecyclerView();

        //detail_toolbar.setNavigationOnClickListener(v -> finish());

        detail_toolbar.setOnMenuItemClickListener(item -> {
            int itemID = item.getItemId();
            if(itemID == R.id.recipe_detail_menu_app_bar_delete){
                deleteRecipe();
                return true;
            } else if(itemID == R.id.recipe_detail_menu_app_bar_edit){
                editRecipe();
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recipe_detail_menu_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*Private Methods*/
    private void initViews(){
        this.imgRecipeDetail = findViewById(R.id.img_detail_recipe);
        this.tvRecipeTitleDetail = findViewById(R.id.recipe_detail_title);
        this.tvRecipeTimeDetail = findViewById(R.id.recipe_detail_time);
        this.tvRecipeMealType = findViewById(R.id.recipe_detail_mealType);
        this.tvRecipeDescription = findViewById(R.id.recipe_detail_description);
        this.ingredientRecyclerView = findViewById(R.id.recipe_detail_ingredient_recyclerView);
        this.btnStartRecipe = findViewById(R.id.btn_start_recipe);
        this.expandableLayout = findViewById(R.id.expandableLayout);
        this.imgIngredientRecyclerViewIcon = findViewById(R.id.recipe_detail_ingredient_recyclerView_icon);

        this.expandableLayout.setOnClickListener(view -> {
            if(!this.ingredientList.isEmpty()){
                ingredientRecyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                if(isExpanded) imgIngredientRecyclerViewIcon.setImageResource(R.drawable.ic_down_arrow);
                else imgIngredientRecyclerViewIcon.setImageResource(R.drawable.ic_up_arrow);

                isExpanded = !isExpanded;
            } else {
                Toast.makeText(getApplicationContext(), R.string.recipe_detail_ingredientList_empty, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initParameters(){
        try {
            BBDD_Helper db = new BBDD_Helper(getApplicationContext());
            this.recipeSelected = (RecipeModel) SingletonMap.getInstance().get(SHARE_RECIPE_KEY);
            this.ingredientList = BD_Operations.getIngredientsByIdRecipe(recipeSelected.getId(), db);
            this.stepList = BD_Operations.getStepsFromRecipeIdOrdered(recipeSelected.getId(), db);
        } catch (IngredientException e){
            this.ingredientList = new ArrayList<>();
        } catch(StepException e) {
            this.stepList = new ArrayList<>();
        }

        isExpanded = false;
        this.ingredientRecyclerView.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void setViews(){

        if(this.recipeSelected.getImage() != null) this.imgRecipeDetail.setImageBitmap(this.recipeSelected.getImage());
        else this.imgRecipeDetail.setImageResource(R.mipmap.img_recipe_card_default);

        this.tvRecipeTitleDetail.setText(this.recipeSelected.getRecipeName());
        this.tvRecipeTimeDetail.setText(this.recipeSelected.getExecutionTimeHour()+"h "
                + this.recipeSelected.getExecutionTimeMinute()+"min");
        this.tvRecipeMealType.setText(String.valueOf(this.recipeSelected.getMealType()));
        this.tvRecipeDescription.setText(this.recipeSelected.getRecipeDescription());
    }

    private void initIngredientRecyclerView(){
        this.ingredientAdapter = new IngredientRecyclerAdapter(this.ingredientList);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
    }

    private void deleteRecipe(){
        try {
            BBDD_Helper db = new BBDD_Helper(RecipeDetails.this);
            BD_Operations.deleteRecipe(recipeSelected.getId(), db);
            Toast.makeText(RecipeDetails.this, R.string.recipe_detail_deleted, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e){
            Toast.makeText(RecipeDetails.this, R.string.recipe_detail_deleted_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void editRecipe(){
        finish();
        SingletonMap.getInstance().put(SHARE_RECIPE_KEY, recipeSelected);
        SingletonMap.getInstance().put(SHARE_INGLIST_KEY, ingredientList);
        SingletonMap.getInstance().put(SHARE_STEPLIST_KEY, stepList);
        Intent i = new Intent(RecipeDetails.this, AddNewRecipeActivity.class);
        i.putExtra("edit", true);
        startActivity(i);
    }
}