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

/*
    Fragmento de la pantalla principal en el que se muestran las recetas añadidas a favoritos
 */
public class FavouritesFragment extends Fragment implements RecipeClickListener {

    //Singleton Keys
    private static final String SHARE_DELETED_RECIPE = "SHARE_RECETA_ELIMINADA";
    private static final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";
    private static final String SHARE_FAV_KEY = "SHARE_FAV_KEY";

    //Views necesarias
    TextView noFavourites;
    RecyclerView rv_fav;
    RecyclerViewAdapter adapter;

    //Variables usadas
    List<RecipeModel> favouritesList;   //Lista de favoritas
    RecipeModel recipeClicked;  //Receta Pulsada

    public FavouritesFragment() {
        // Required empty public constructor
    }

    //Creacion de los elementos de la vista
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        initView(view); //Inicialziar elementos de la view
        initListAndRecyclerView();  //Inicializar reycler view y la propia lista

        return view;
    }

    //Init View
    private void initView(View view){
        this.noFavourites = view.findViewById(R.id.txt_noFav);
        this.rv_fav = view.findViewById(R.id.favourites_recipe_recyclerView);

        this.recipeClicked = new RecipeModel();
        //this.favouritesList = new ArrayList<>();
        //adapter = new RecyclerViewAdapter(favouritesList, this);
    }

    //Inicializacion del recylcer view y de la lista mostrada
    private void initListAndRecyclerView(){
        BBDD_Helper db = new BBDD_Helper(getActivity());    //BD_Helper para realizar acciones en la BD
        try {
            //Obtenemos la lista de favoritas y eliminamos la visibilidad del mensaje diciendo que no hay
            this.favouritesList = BD_Operations.getFavouritesRecipes(db);
            this.noFavourites.setVisibility(View.GONE);
        } catch (Exception e){  //Se produce si la lista sacada de la BD es vacia
            this.favouritesList = new ArrayList<>();    //Iniciamos la lista a vacia
            this.noFavourites.setVisibility(View.VISIBLE); //Mostramos Text View (no hay recetas)
        }
        //Creamos el adapter y seteamos al recyclerview
        adapter = new RecyclerViewAdapter(favouritesList,this);
        rv_fav.setAdapter(adapter);
    }

    //Utilities
    @Override   //Cuando una receta es presionada...
    public void onRecipeClick(int position) {
        this.recipeClicked = favouritesList.get(position);  //Iniciamos la receta clickada
        //Guardamos esta en un singleton que se recibira en la activity siguiente: Details
        SingletonMap.getInstance().put(SHARE_RECIPE_KEY, recipeClicked);
        Intent i = new Intent(getActivity(), RecipeDetails.class);
        startActivity(i);   //Nos vamos a la Activity
    }

    //Navigation
    @Override   //Cuando volvemos al fragment dede la acivty de details...
    public void onResume() {
        super.onResume();

        //Obtenemos los singleton que manda details al cerrarse
            //Si la receta ha sido borrada o si ha cambiado de fav a unfav (viceversa)
        String isDeleted = (String) SingletonMap.getInstance().get(SHARE_DELETED_RECIPE);
        String fav = (String) SingletonMap.getInstance().get(SHARE_FAV_KEY);

        //Si la receta ha sido eliminada y era la unica que quedaba en favoritas...
        if(favouritesList.contains(recipeClicked) && favouritesList.size() == 1
                && isDeleted != null) noFavourites.setVisibility(View.VISIBLE); //Hacemos visible el text de no favoritas

        //Si fav != null, significa que ha cambiado dede que entro, luego lo modificamos en la receta
        if(fav != null) recipeClicked.setFavourite(!recipeClicked.isFavourite());

        initListAndRecyclerView();  //Volvemos a cargar la lista
    }

}