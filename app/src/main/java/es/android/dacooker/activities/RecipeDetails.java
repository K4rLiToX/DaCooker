package es.android.dacooker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
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
    MenuItem favIcon;
    boolean isFav;
    boolean isDeleted;

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
        detail_toolbar.getNavigationIcon();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initViews();
        initParameters();
        setViews();
        initIngredientRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recipe_detail_menu_app_bar, menu);
        favIcon = menu.findItem(R.id.recipe_detail_menu_app_bar_fav);
        updateFavIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.recipe_detail_menu_app_bar_delete){
            deleteRecipe();
            return true;
        } else if(itemID == R.id.recipe_detail_menu_app_bar_edit){
            editRecipe();
            return true;
        } else if(itemID == R.id.recipe_detail_menu_app_bar_fav) {
            fav();
            return true;
        } else if(itemID == android.R.id.home){
            this.onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
        BBDD_Helper db = new BBDD_Helper(getApplicationContext());

        //RecipeSelected
        this.recipeSelected = (RecipeModel) SingletonMap.getInstance().get(SHARE_RECIPE_KEY);
        this.isFav = recipeSelected.isFavourite();
        this.isDeleted = false;

        //StepList
        try { this.stepList = BD_Operations.getStepsFromRecipeIdOrdered(recipeSelected.getId(), db);
        } catch (StepException e) { this.stepList = new ArrayList<>(); }

        //IngredientList
        try { this.ingredientList = BD_Operations.getIngredientsByIdRecipe(recipeSelected.getId(), db);
        } catch (IngredientException e){ this.ingredientList = new ArrayList<>(); }

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

        this.btnStartRecipe.setOnClickListener(view -> {

            if(this.stepList == null || this.stepList.isEmpty())
                Toast.makeText(this, getString(R.string.recipe_detail_nosteps_error), Toast.LENGTH_SHORT).show();
            else {
                SingletonMap.getInstance().put(SHARE_STEPLIST_KEY, stepList);
                Intent i = new Intent(RecipeDetails.this, StepsRecipeCooking.class);
                startActivity(i);
            }
        });
    }

    private void initIngredientRecyclerView(){
        this.ingredientAdapter = new IngredientRecyclerAdapter(this.ingredientList);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
    }

    private void deleteRecipe(){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RecipeDetails.this);
            alertBuilder.setTitle(R.string.recipe_fragment_alert_dialog_title);
            alertBuilder.setMessage(R.string.recipe_fragment_alert_dialog_message);
            alertBuilder.setPositiveButton(R.string.recipe_fragment_alert_dialog_confirmation, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        if(isFav){
                            Toast.makeText(RecipeDetails.this, R.string.recipe_detail_delete_fav_error, Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        } else {
                            BBDD_Helper db = new BBDD_Helper(RecipeDetails.this);
                            BD_Operations.deleteRecipe(recipeSelected.getId(), db);
                            isDeleted = true;
                            SingletonMap.getInstance().put("SHARE_RECETA_ELIMINADA", "true");
                            Toast.makeText(RecipeDetails.this, R.string.recipe_detail_deleted, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e){
                        Toast.makeText(RecipeDetails.this, R.string.recipe_detail_deleted_error, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                }
            });
            alertBuilder.setNegativeButton(R.string.recipe_fragment_alert_dialog_dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertBuilder.create();
            alertBuilder.show();
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

    private void fav(){
        isFav = !isFav;
        updateFavIcon();
    }

    private void updateFavIcon(){
        if(isFav){
            favIcon.setIcon(R.drawable.ic_favourite).setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            favIcon.setIcon(R.drawable.ic_no_favourite);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isFav != recipeSelected.isFavourite() && !isDeleted){
            SingletonMap.getInstance().put("SHARE_FAV_KEY", "true");
            BBDD_Helper db = new BBDD_Helper(RecipeDetails.this);
            try {
                BD_Operations.updateFavourite(recipeSelected.getId(), isFav, db);
                finish();
            } catch (Exception e){
                Toast.makeText(RecipeDetails.this, R.string.recipe_detail_deleted_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}