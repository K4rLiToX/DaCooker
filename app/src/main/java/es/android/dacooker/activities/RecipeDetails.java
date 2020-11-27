package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.adapters.IngredientRecyclerAdapter;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;


public class RecipeDetails extends AppCompatActivity {

    //Views
    private ImageView imgRecipeDetail, imgIngredientRecyclerViewIcon;
    private TextView tvRecipeTitleDetail, tvRecipeTimeDetail, tvRecipeMealType, tvRecipeDescription;
    private RecyclerView ingredientRecyclerView;
    Button btnStartRecipe;
    LinearLayout expandableLayout;

    //Utilities
    IngredientRecyclerAdapter ingredientAdapter;
    BBDD_Helper db;
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
        this.imgIngredientRecyclerViewIcon = findViewById(R.id.recipe_detail_ingredient_recyclerView_icon);

        //Set onClick to Ingredients CardView
        this.expandableLayout.setOnClickListener(view -> {
<<<<<<< Updated upstream
            //If its not expanded, show, else, hide
            ingredientRecyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            isExpanded = !isExpanded;
=======
            if(ingredientList.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.recipe_detail_noIngredients, Toast.LENGTH_LONG).show();
            } else {
                ingredientRecyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                if(isExpanded) imgIngredientRecyclerViewIcon.setImageResource(R.drawable.ic_down_arrow);
                else imgIngredientRecyclerViewIcon.setImageResource(R.drawable.ic_up_arrow);
                isExpanded = !isExpanded;
            }
>>>>>>> Stashed changes
        });

        //Set onClick to Start Cooking Button
        this.btnStartRecipe.setOnClickListener(v -> {
            //Go to Start Recipe Activity
        });
    }

    private void initParameters(){
<<<<<<< Updated upstream
        //Get the Recipe inside de Intent
        this.recipeSelected = (RecipeModel) getIntent().getSerializableExtra("recipeSelected");
        //Get the Recipe's Ingredient List
        this.ingredientList = recipeSelected.getIngredientsList();
        //Set AppBar title to Recipe´s Name
        setTitle(recipeSelected.getRecipeName());

        //Ingredient CardView starts Hidden
=======
        try {
            db = new BBDD_Helper(getApplicationContext());
            this.recipeSelected = (RecipeModel) getIntent().getSerializableExtra("recipeSelected");
            this.ingredientList = BD_Operations.getIngredientsByIdRecipe(recipeSelected.getId(), db);
        } catch (Exception e){
            ingredientList = new ArrayList<>();
        }
>>>>>>> Stashed changes
        isExpanded = false;
        this.ingredientRecyclerView.setVisibility(View.GONE);
    }

    //Set the Views with the Recipe Data
    private void setViews(){
        if(recipeSelected.getImage() != null) this.imgRecipeDetail.setImageBitmap(this.recipeSelected.getImage());
        else this.imgRecipeDetail.setImageResource(R.drawable.img_recipe_card_default);
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