package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.adapters.IngredientRecyclerAdapter;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.RecipeModel;


public class RecipeDetails extends AppCompatActivity {

    //Views
    private ImageView imgRecipeDetail;
    private TextView tvRecipeTitleDetail, tvRecipeTimeDetail, tvRecipeMealType, tvRecipeDescription;
    private RecyclerView ingredientRecyclerView;
    private Button btnStartRecipe;
    private LinearLayout expandableLayout;

    //Utilities
    private IngredientRecyclerAdapter ingredientAdapter;
    private boolean isExpanded;

    //Recipe to Show
    private RecipeModel recipeSelected;

    //Ingredient's List to Show
    private List<IngredientModel> ingredientList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        //Set Back Button on AppBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initViews();
        initParameters();
        setViews();
        initIngredientRecyclerView();
    }

    /*Private Methods*/

    //Find Views in the Layout
    private void initViews(){
        this.imgRecipeDetail = findViewById(R.id.img_detail_recipe);
        this.tvRecipeTitleDetail = findViewById(R.id.recipe_detail_title);
        this.tvRecipeTimeDetail = findViewById(R.id.recipe_detail_time);
        this.tvRecipeMealType = findViewById(R.id.recipe_detail_mealType);
        this.tvRecipeDescription = findViewById(R.id.recipe_detail_description);
        this.ingredientRecyclerView = findViewById(R.id.recipe_detail_ingredient_recyclerView);
        this.btnStartRecipe = findViewById(R.id.btn_start_recipe);
        this.expandableLayout = findViewById(R.id.expandableLayout);

        //Set onClick to Ingredients CardView
        this.expandableLayout.setOnClickListener(view -> {
            //If its not expanded, show, else, hide
            ingredientRecyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            isExpanded = !isExpanded;
        });

        //Set onClick to Start Cooking Button
        this.btnStartRecipe.setOnClickListener(v -> {
            //Go to Start Recipe Activity
        });
    }

    private void initParameters(){
        //Get the Recipe inside de Intent
        this.recipeSelected = (RecipeModel) getIntent().getSerializableExtra("recipeSelected");
        //Get the Recipe's Ingredient List
        this.ingredientList = recipeSelected.getIngredientsList();
        //Set AppBar title to Recipe´s Name
        setTitle(recipeSelected.getRecipeName());

        //Ingredient CardView starts Hidden
        isExpanded = false;
        this.ingredientRecyclerView.setVisibility(View.GONE);
    }

    //Set the Views with the Recipe Data
    private void setViews(){
        this.imgRecipeDetail.setImageBitmap(this.recipeSelected.getImage());
        this.tvRecipeTitleDetail.setText(this.recipeSelected.getRecipeName());
        this.tvRecipeTimeDetail.setText(this.recipeSelected.getExecutionTime());
        this.tvRecipeMealType.setText(String.valueOf(this.recipeSelected.getMealType()));
        this.tvRecipeDescription.setText(this.recipeSelected.getRecipeDescription());
    }

    //Set the Ingredient's Recyclerview
    private void initIngredientRecyclerView(){
        this.ingredientAdapter = new IngredientRecyclerAdapter(this.ingredientList);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
    }

}