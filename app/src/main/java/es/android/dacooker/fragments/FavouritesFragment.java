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
public class FavouritesFragment extends Fragment implements RecipeClickListener {

    //Singleton Keys
    private static final String SHARE_DELETED_RECIPE = "SHARE_RECETA_ELIMINADA";
    private static final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";
    private static final String SHARE_FAV_KEY = "SHARE_FAV_KEY";

    //View
    TextView noFavourites;
    RecyclerView rv_fav;
    RecyclerViewAdapter adapter;

    //Variables
    List<RecipeModel> favouritesList;
    RecipeModel recipeClicked;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        initView(view);
        initListAndRecyclerView();

        return view;
    }

    //Init View
    private void initView(View view){
        this.noFavourites = view.findViewById(R.id.txt_noFav);
        this.rv_fav = view.findViewById(R.id.favourites_recipe_recyclerView);

        this.recipeClicked = new RecipeModel();
        this.favouritesList = new ArrayList<>();
        adapter = new RecyclerViewAdapter(favouritesList, this);
    }

    private void initListAndRecyclerView(){
        BBDD_Helper db = new BBDD_Helper(getActivity());
        try {
            this.favouritesList = BD_Operations.getFavouritesRecipes(db);
            this.noFavourites.setVisibility(View.GONE);
        } catch (Exception e){
            //Mostramos Text View (no hay recetas)
            this.favouritesList = new ArrayList<>();
            this.noFavourites.setVisibility(View.VISIBLE);
        }
        adapter = new RecyclerViewAdapter(favouritesList,this);
        rv_fav.setAdapter(adapter);
    }

    //Utilities
    @Override
    public void onRecipeClick(int position) {
        this.recipeClicked = favouritesList.get(position);
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

        if(favouritesList.contains(recipeClicked) && favouritesList.size() == 1
                && isDeleted != null) noFavourites.setVisibility(View.VISIBLE);

        if(fav != null) recipeClicked.setFavourite(!recipeClicked.isFavourite());

        initListAndRecyclerView();
    }

}