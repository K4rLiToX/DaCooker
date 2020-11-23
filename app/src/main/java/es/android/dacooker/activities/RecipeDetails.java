package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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

    //Adapters
    private IngredientRecyclerAdapter ingredientAdapter;

    //Recipe to Show
    private RecipeModel recipe;

    //Ingredient's List to Show
    private List<IngredientModel> ingredientList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        initViews();
        initParameters();
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
    }

    private void initParameters(){
        //Recibir la receta por intent

        //Inicializar lista de ingredientes

    }

    private void initIngredientRecyclerView(){
        this.ingredientAdapter = new IngredientRecyclerAdapter(this.ingredientList);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
    }
}