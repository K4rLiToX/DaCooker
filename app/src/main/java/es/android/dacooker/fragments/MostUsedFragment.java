package es.android.dacooker.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.activities.MainActivity;
import es.android.dacooker.activities.RecipeDetails;
import es.android.dacooker.adapters.RecyclerViewAdapter;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostUsedFragment extends Fragment implements RecipeClickListener {

    //SingletonMap Key
    private final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";

    //List to Show
    private List<RecipeModel> recipeList;
    //Views
    private RecyclerView recipeRecyclerView;
    TextView errMostCooked;
    //Adapters
    RecyclerViewAdapter adapter;
    RecipeModel recipeClicked;

    public MostUsedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_most_used, container, false);

        this.errMostCooked = view.findViewById(R.id.txt_noMost);
        recipeRecyclerView = view.findViewById(R.id.most_recipe_recyclerView);
        this.recipeClicked = new RecipeModel();
        initListAndRecyclerView();

        return view;
    }

    private void initListAndRecyclerView() {
        BBDD_Helper db = new BBDD_Helper(getActivity().getApplicationContext());
        try {
            this.recipeList = BD_Operations.getRecipesOrderByTimesCooked(db);
            adapter = new RecyclerViewAdapter(recipeList,this);
            recipeRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            this.errMostCooked.setVisibility(View.VISIBLE);
            this.recipeRecyclerView.setVisibility(View.GONE);
            this.recipeList = new ArrayList<>();
        }
    }

    @Override
    public void onRecipeClick(int position){
        RecipeModel recipe = recipeList.get(position);
        recipeClicked = recipe;
        SingletonMap.getInstance().put(SHARE_RECIPE_KEY, recipe);
        Intent i = new Intent(getActivity(), RecipeDetails.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        String isDeleted = (String) SingletonMap.getInstance().get("SHARE_RECETA_ELIMINADA");
        String fav = (String) SingletonMap.getInstance().get("SHARE_FAV_KEY");
        if(recipeList.contains(recipeClicked) && recipeList.size() == 1 && isDeleted != null){
            errMostCooked.setVisibility(View.VISIBLE);
        }
        if(fav != null){
            recipeClicked.setFavourite(!recipeClicked.isFavourite());
        }
        initListAndRecyclerView();
    }
}