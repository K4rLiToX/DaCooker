package es.android.dacooker.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
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
    private static final String SHARE_DELETED_RECIPE = "SHARE_RECETA_ELIMINADA";
    private static final String SHARE_FAV_KEY = "SHARE_FAV_KEY";
    private final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";

    //Variables
    private List<RecipeModel> recipeList;
    private RecipeModel recipeClicked;

    //Views_Recycler
    private RecyclerView recipeRecyclerView;
    private RecyclerViewAdapter adapter;
    TextView errMostCooked;

    public MostUsedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_most_used, container, false);

        initView(view);
        initListAndRecyclerView();

        return view;
    }

    //Init Views
    private void initView(View view){
        this.errMostCooked = view.findViewById(R.id.txt_noMost);
        recipeRecyclerView = view.findViewById(R.id.most_recipe_recyclerView);
        this.recipeClicked = new RecipeModel();
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

    //Utilities
    @Override
    public void onRecipeClick(int position){
        recipeClicked = recipeList.get(position);
        SingletonMap.getInstance().put(SHARE_RECIPE_KEY, recipeClicked);
        Intent i = new Intent(getActivity(), RecipeDetails.class);
        startActivity(i);
    }

    //Navigation
    @Override
    public void onResume() {
        super.onResume();
        String isDeleted = (String) SingletonMap.getInstance().get(SHARE_DELETED_RECIPE);
        String fav = (String) SingletonMap.getInstance().get(SHARE_FAV_KEY);
        if(recipeList.contains(recipeClicked) && recipeList.size() == 1 && isDeleted != null){
            errMostCooked.setVisibility(View.VISIBLE);
        }
        if(fav != null) recipeClicked.setFavourite(!recipeClicked.isFavourite());

        initListAndRecyclerView();
    }
}