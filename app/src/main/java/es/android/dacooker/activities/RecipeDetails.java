package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
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
import es.android.dacooker.fragments.RecipeFragment;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;


public class RecipeDetails extends AppCompatActivity {

    //SingletonMap Key
    private final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";

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

    //Ingredient's List to Show
    private List<IngredientModel> ingredientList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initViews();
        initParameters();
        setViews();
        initIngredientRecyclerView();
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
            if(!ingredientList.isEmpty()){
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
        } catch (Exception e){
            ingredientList = new ArrayList<>();
        }

        isExpanded = false;
        this.ingredientRecyclerView.setVisibility(View.GONE);
    }

    private void setViews(){

        if(this.recipeSelected.getImage() != null) this.imgRecipeDetail.setImageBitmap(this.recipeSelected.getImage());
        else this.imgRecipeDetail.setImageResource(R.drawable.img_recipe_card_default);

        this.tvRecipeTitleDetail.setText(this.recipeSelected.getRecipeName());
        this.tvRecipeTimeDetail.setText(this.recipeSelected.getExecutionTimeHour()+"h " + this.recipeSelected.getExecutionTimeMinute()+"min");
        this.tvRecipeMealType.setText(String.valueOf(this.recipeSelected.getMealType()));
        this.tvRecipeDescription.setText(this.recipeSelected.getRecipeDescription());
    }

    private void initIngredientRecyclerView(){
        this.ingredientAdapter = new IngredientRecyclerAdapter(this.ingredientList);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
    }


}