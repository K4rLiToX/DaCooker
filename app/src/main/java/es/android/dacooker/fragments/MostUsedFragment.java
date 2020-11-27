package es.android.dacooker.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.android.dacooker.R;
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
public class MostUsedFragment extends Fragment implements RecipeClickListener{

    //List to Show
    private List<RecipeModel> mostUsedRecipesList;
    //Views
    private RecyclerView mostUsedRecipeRecyclerView;
    private TextView tvNoMostUsedRecipes;
    //Adapter
    private RecipeRecyclerViewAdapter adapter;
    //Interface
    private RecipeClickListener recipeClickListener;
    //Services
    private BBDD_Helper db;

    // Required empty public constructor
    public MostUsedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate de View
        View view = inflater.inflate(R.layout.fragment_most_used, container, false);
        //Find the Layout Views
        mostUsedRecipeRecyclerView = view.findViewById(R.id.most_used_recycler_view);
        tvNoMostUsedRecipes = view.findViewById(R.id.tvEmptyMostUsedRecipes);
        //Initialize the Recipe List
        initRecipeList();
        //Create the Recyclerview's Adapter
        adapter = new RecipeRecyclerViewAdapter(mostUsedRecipesList, recipeClickListener);

        return view;
    }

    private void initRecipeList(){
        //Get the DB Service via SingletonMap
        db = (BBDD_Helper) SingletonMap.getInstance().get(MainActivity.SHARED_DB_DATA_KEY);
        //Opertate with the DB to get the Recipe List
        try {
            this.mostUsedRecipesList = BD_Operations.getRecipesOrderByTimesCooked(db);
            mostUsedRecipeRecyclerView.setAdapter(adapter);
        } catch (Exception e){
            //No recipes -> hide recyclerview
            this.mostUsedRecipeRecyclerView.setVisibility(View.GONE);
            //Show information textview
            this.tvNoMostUsedRecipes.setVisibility(View.VISIBLE);
        }
    }

    //Whenever a Recycler's View Item is clicked
    @Override
    public void onRecipeClick(int position){
        //Get the recipe that has been clicked
        RecipeModel recipe = mostUsedRecipesList.get(position);
        //Create the intent to go to the RecipeDetails activity
        Intent i = new Intent(getActivity(), RecipeDetails.class);
        //Put the recipe selected inside de intent
        i.putExtra("recipeSelected", recipe);
        startActivity(i);
    }
}