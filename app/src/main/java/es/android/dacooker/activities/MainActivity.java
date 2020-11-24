package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.adapters.RecyclerViewAdapter;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;

public class MainActivity extends AppCompatActivity implements RecipeClickListener{
    /*Constants*/
    private final Context activityContext = MainActivity.this;

    /*Views*/
    private BottomNavigationView bottomNavigationView;
    private RecyclerView mainRecyclerView;
    private TextView tvNoRecipes;

    /*Utilities*/
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecipeClickListener recipeClickListener;

    /*Parameters*/
    private List<RecipeModel> recipeList;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initParameters();

        //Set bottom navigation listener from changing activity
        this.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_most_used:
                    gotoActivity(MostUsedActivity.class);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menu_recipes:
                    return true;
                case R.id.menu_custom:
                    gotoActivity(CustomActivity.class);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    /*On click methods*/
    public void addRecipe(View view){
        gotoActivity(AddNewRecipeActivity.class);
    }

    @Override
    public void onRecipeClick(int position){
        RecipeModel recipe = recipeList.get(position);
        Intent i = new Intent(activityContext, RecipeDetails.class);
        i.putExtra("recipeSelected", recipe);
        startActivity(i);
    }

    /*Private Methods*/

    //Assigns views
    private void initViews() {
        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        this.mainRecyclerView = findViewById(R.id.main_recyclerView);
        this.tvNoRecipes = findViewById(R.id.tvEmptyRecipes);
        //Set custom selected
        this.bottomNavigationView.setSelectedItemId(R.id.menu_recipes);
    }

    private void initParameters(){
        //Inicializo la la lista de recetas
        BBDD_Helper db = new BBDD_Helper(activityContext);
        this.recipeList = BD_Operations.getRecipes(db);
        if(this.recipeList.isEmpty()){
            this.mainRecyclerView.setVisibility(View.GONE);
            //Mostramos Text View (no hay recetas)
            this.tvNoRecipes.setVisibility(View.VISIBLE);
        } else {
            initRecyclerView();
        }
    }

    private void initRecyclerView(){
        this.recyclerViewAdapter = new RecyclerViewAdapter(this.recipeList, this.recipeClickListener);
        this.mainRecyclerView.setAdapter(this.recyclerViewAdapter);
    }

    //Changes activity given a class
    private void gotoActivity(Class aClass){
        startActivity(new Intent(activityContext, aClass));
    }

    //Shows a Toast
    private void showToast(String msg){
        Toast.makeText(activityContext, msg, Toast.LENGTH_LONG).show();
    }
}