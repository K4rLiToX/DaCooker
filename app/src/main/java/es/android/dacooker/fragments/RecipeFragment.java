package es.android.dacooker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.activities.AddNewRecipeActivity;
import es.android.dacooker.activities.RecipeDetails;
import es.android.dacooker.adapters.RecyclerViewAdapter;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment implements RecipeClickListener{

    //Constants
    private final Context PARENT_ACTIVITY_CONTEXT = getActivity();
    //List to Show
    private List<RecipeModel> recipeList;
    //Views
    private RecyclerView recipeRecyclerView;
    private TextView tvNoRecipes;
    private FloatingActionButton btnAddRecipe;
    //Adapters
    private RecyclerViewAdapter adapter;
    //Interface
    private RecipeClickListener recipeClickListener;


    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        initRecipeList(view);
        tvNoRecipes = view.findViewById(R.id.tvEmptyRecipes);
        btnAddRecipe = view.findViewById(R.id.fabAddRecipe);
        btnAddRecipe.setOnClickListener(click -> {
            addRecipe();
        });

        adapter = new RecyclerViewAdapter(recipeList,recipeClickListener);

        return view;
    }

    private void initRecipeList(View view){
        //Inicializar la lista de recetas
        BBDD_Helper db = new BBDD_Helper(PARENT_ACTIVITY_CONTEXT);
        this.recipeList = BD_Operations.getRecipes(db);
        if(this.recipeList.isEmpty()){
            this.recipeRecyclerView.setVisibility(View.GONE);
            //Mostramos Text View (no hay recetas)
            this.tvNoRecipes.setVisibility(View.VISIBLE);
        } else {
            initRecyclerView(view);
        }
    }

    private void initRecyclerView(View view){
        recipeRecyclerView = view.findViewById(R.id.recipe_recyclerView);
        recipeRecyclerView.setHasFixedSize(true);
        recipeRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onRecipeClick(int position){
        RecipeModel recipe = recipeList.get(position);
        Intent i = new Intent(PARENT_ACTIVITY_CONTEXT, RecipeDetails.class);
        i.putExtra("recipeSelected", recipe);
        startActivity(i);
    }

    public void addRecipe(){
        startActivity(new Intent(PARENT_ACTIVITY_CONTEXT, AddNewRecipeActivity.class));
    }
}