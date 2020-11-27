package es.android.dacooker.fragments;

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
import es.android.dacooker.activities.MainActivity;
import es.android.dacooker.activities.RecipeDetails;
import es.android.dacooker.adapters.RecipeRecyclerViewAdapter;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment implements RecipeClickListener{

    //List to Show
    private List<RecipeModel> recipeList;
    //Views
    private RecyclerView recipeRecyclerView;
    private TextView tvNoRecipes;
    //Adapters
    private RecipeRecyclerViewAdapter adapter;
    //Interface
    private RecipeClickListener recipeClickListener;
    //Services
    private BBDD_Helper db;

    // Required empty public constructor
    public RecipeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate de the View
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        //Find de Layout Views
        recipeRecyclerView = view.findViewById(R.id.recipe_recyclerView);
        tvNoRecipes = view.findViewById(R.id.tvEmptyRecipes);
        //Initialize the recipe list
        initRecipeList();
        //FAB Setup
        FloatingActionButton btnAddRecipe = view.findViewById(R.id.fabAddRecipe);
        btnAddRecipe.setOnClickListener(click -> {
            addRecipe();
        });

        //Create the Recyclerview's Adapter
        adapter = new RecipeRecyclerViewAdapter(recipeList,recipeClickListener);

        return view;
    }

    private void initRecipeList(){
        //Get the DB Service via SingletonMap
        db = (BBDD_Helper) SingletonMap.getInstance().get(MainActivity.SHARED_DB_DATA_KEY);
        //Opertate with the DB to get the Recipe List
        this.recipeList = BD_Operations.getRecipes(db);

        if(this.recipeList.isEmpty()){
            //No recipes -> hide recyclerview
            this.recipeRecyclerView.setVisibility(View.GONE);
            //Show information textview
            this.tvNoRecipes.setVisibility(View.VISIBLE);
        } else {
            //Recipes -> set the recyclerview's adapter
            recipeRecyclerView.setAdapter(adapter);
        }
    }

    //Whenever a Recycler's View Item is clicked
    @Override
    public void onRecipeClick(int position){
        //Get the recipe that has been clicked
        RecipeModel recipe = recipeList.get(position);
        //Create the intent to go to the RecipeDetails activity
        Intent i = new Intent(getActivity(), RecipeDetails.class);
        //Put the recipe selected inside de intent
        i.putExtra("recipeSelected", recipe);
        startActivity(i);
    }

    //Whenever the FAB is clicked, it will redirect to the AddNewRecipeActivity
    public void addRecipe(){
        startActivity(new Intent(getActivity(), AddNewRecipeActivity.class));
    }
}