package es.android.dacooker.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.activities.AddUpdateRecipeActivity;
import es.android.dacooker.activities.RecipeDetails;
import es.android.dacooker.adapters.RecyclerViewAdapter;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.MealType;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 */

    /*
        Fragmento de la pantalla principal en el que se muestran todas las recetas añadidas
     */
public class RecipeFragment extends Fragment implements RecipeClickListener{

    //SingletonMap Key
    private static final String SHARE_RESULT_LIST_KEY = "SHARE_RESULT_LIST_KEY";
    private static final String SHARE_DELETED_RECIPE = "SHARE_RECETA_ELIMINADA";
    private static final String SHARE_FILTER_KEY = "SHARE_FILTER_SEARCH";
    private static final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";
    private static final String SHARE_FAV_KEY = "SHARE_FAV_KEY";

    //Filters posibles para las listas
    boolean isFilterMealType, isFilterTimer, isFilter;  //Filtro por tipo de comida, timer o filtros en general
    String[] filters;   //Filtros recibidos por el customDialog

    //List to Show
    private List<RecipeModel> recipeList;   //Lista de recetas

    //Views
    private FloatingActionButton btnAddRecipe;
    private TextView tvNoRecipes, filterField;
    private RecyclerView recipeRecyclerView;
    private LinearLayout layoutFilters;
    private Button btnUndoFilter;

    //Adapters
    RecyclerViewAdapter adapter;

    //ItemTouch
    private RecipeModel recipeClicked;
    ItemTouchHelper itemTouchHelper;

    public RecipeFragment() {
        // Required empty public constructor
    }

    //Creacion de los elementos de la vista
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        initView(view); //Inicializar elementos de la view
        initButtons(); //Inicializar botones del fragment
        initListAndRecyclerView(); //Inicializar reycler view y la propia lista

        return view;
    }

    //Init Views
    private void initView(View view){
        recipeRecyclerView = view.findViewById(R.id.recipe_recyclerView);
        tvNoRecipes = view.findViewById(R.id.tvEmptyRecipes);
        layoutFilters = view.findViewById(R.id.recipe_fragment_layout_showFilters);
        filterField = view.findViewById(R.id.recipe_fragment_filterApplied);
        btnUndoFilter = view.findViewById(R.id.recipe_fragment_btnUndo);
        btnAddRecipe = view.findViewById(R.id.fabAddRecipe);
        this.recipeClicked = new RecipeModel();
    }

    private void initButtons(){

        //Deshacer filtros (si los hay)
        btnUndoFilter.setOnClickListener(undo -> {
            recipeList = null;
            filters = null; //seteamos los filtros a null para la comprobacion al inicialziar el recyclerView

            //Filtros a false ya que los queremos quitar
            isFilter = false;
            isFilterMealType = false;
            isFilterTimer = false;

            //Creamos una instancia null para los filtros (al volver a iniciar, mantenga el null)
            SingletonMap.getInstance().put(SHARE_FILTER_KEY, null);
            initListAndRecyclerView();  //volvemos a cargar todas las recetas, sin filtros
            layoutFilters.setVisibility(View.GONE); //Desaparece ellayout de filtros
        });

        //Si queremos añadir una receta (floatingButton)
        btnAddRecipe.setOnClickListener(click -> {
            addRecipe();
        });
    }

    private void initListAndRecyclerView() {    //Inicializar la lista y el recycler view
        BBDD_Helper db = new BBDD_Helper(getActivity());

        checkFilters(); //Comprobamos los filtros que estan activos

        //Sipor algun casual el recycler view es null, lo volvemos a identificar
        if(recipeRecyclerView == null) { recipeRecyclerView = getView().findViewById(R.id.recipe_recyclerView); }

        if(isFilter) chargeFilterList(db);  //Si hay algun filtro activo, cargamos la lista con dichos filtros
        else {  //Si no hay filtros
            this.recipeList = BD_Operations.getRecipes(db); //Sacamos todas las recetas
            adapter = new RecyclerViewAdapter(recipeList,this); //Seteamos en el adapter
            recipeRecyclerView.setAdapter(adapter); //Seteamos el recyclerView

            if(this.recipeList.isEmpty()){  //Si la lista obtenida de la BD es vacia...
                //Mostramos Text View (no hay recetas)
                this.tvNoRecipes.setVisibility(View.VISIBLE);
            } else {    //Si tiene elementos...
                this.tvNoRecipes.setVisibility(View.GONE);  //Ocultamos no hay recetas

                //Iniciamos el ItemTouch para permitir el Swipe en las cards de recetas
                itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recipeRecyclerView);
            }
        }
    }

    //Utilities Filters
    private void checkFilters(){    //Comprobacion de los filtros que estan activos

        filters = (String[]) SingletonMap.getInstance().get(SHARE_FILTER_KEY);  //Consultamos la instancia de los filtros (enviada por el dialog o por la propia activity)
        if(filters == null) filters = new String[]{"", ""}; //Si no hay filtros aplicados, inicializamos a cadena vacia

        if(filters[0].equalsIgnoreCase("")){    //Si es vacia (no filtros)...

            //Filtros a false
            isFilterMealType = false;
            isFilterTimer = false;
            isFilter = false;
            this.recipeList = null; //Lista nula (con intencion de cargarla entera)
        } else {    //Si no es cadena vacia es que hay filtros activos
            isFilter = true;    //Hay filtro
            if(filters[0].equals("1")) isFilterMealType = true; //Si recibimos un 1 indica que es filtro de mealtype
            else isFilterTimer = true;  //En otro caso (un 2), es un filtro por tiempo

            //La instancia que recogemos de la lista es enviada desde el customDialog
            this.recipeList = (List<RecipeModel>) SingletonMap.getInstance().get(SHARE_RESULT_LIST_KEY);
        }
    }

    //Si hay filtros activos, cargamos la lista en base al filtro usado
    private void chargeFilterList(BBDD_Helper db){  //Recibe una instancia de BBDD_Helper para poder conseguir las busquedas en la BD

        layoutFilters.setVisibility(View.VISIBLE);  //Mostramos el layout con la informacion de filtros
        if(isFilterMealType){   //Si el filtro es por mealtype...
            filterField.setText(filters[1]);    //Mostramos el mealtype por el que se ha filtrado
            try { this.recipeList = BD_Operations.getRecipesByMealType(MealType.valueOf(filters[1]), db);   //Obtenemos los correspondientes datos de la BD
            } catch (Exception e) { //Si no hay datos en la BD correspondientes con el filtro...
                Toast.makeText(getActivity(), getString(R.string.recipe_fragment_delete_showing_all), Toast.LENGTH_LONG).show();    //Avisamos de que no hay disponibles
                this.recipeList = BD_Operations.getRecipes(db); //Cargamos todas las recetas
            }

        } else {    //El filtro es por tiempo
            String[] time = filters[1].split(":");  //Obtenemos la hora y minutos por los que se ha filtrado
            filterField.setText(getString(R.string.filters_less) + " " + time[0]+ getString(R.string.hours) + " " + time[1] + getString(R.string.minutes)); //Seteamos la infromacion del filtro
            try { this.recipeList = BD_Operations.getRecipesByLessExecutionTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]), db);  //Obtenemos los datos de BD
            } catch (Exception e) { //Si no hay datos en la BD correspondientes con el filtro...
                Toast.makeText(getActivity(), getString(R.string.recipe_fragment_delete_showing_all), Toast.LENGTH_LONG).show(); //Avisamos de que no hay disponibles
                this.recipeList = BD_Operations.getRecipes(db); //Cargamos todas las recetas
            }
        }

        //Si el adapter es nulo (un no creado), lo creamos
        if(adapter == null) adapter = new RecyclerViewAdapter(recipeList,this);
        else adapter.setRecipeList(this.recipeList);    //Si ya lo esta, le actualizamos la lista de recetas que mostrara

        recipeRecyclerView.setAdapter(adapter); //Seteamos el adapter

        //Inicializamos itemTouch para permitir el Swipe
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recipeRecyclerView);

    }

    @Override //Cuando volvemos al fragment dede la acivty de details...
    public void onResume() {
        super.onResume();

        //Obtenemos los singleton que manda details al cerrarse
            //Si la receta ha sido borrada o si ha cambiado de fav a unfav (viceversa)
        String isDeleted = (String) SingletonMap.getInstance().get(SHARE_DELETED_RECIPE);
        String fav = (String) SingletonMap.getInstance().get(SHARE_FAV_KEY);

        //Si la receta ha sido eliminada y era la unica que quedaba (ya sea con filtros o no)...
        if(recipeList.contains(recipeClicked) && recipeList.size() == 1 && isDeleted != null){
            SingletonMap.getInstance().put(SHARE_FILTER_KEY, null); //Mandamos una instancia null para resetear filtros
            layoutFilters.setVisibility(View.GONE); //Ocultamos el layout de filtros
        }

        //Si fav != null, significa que ha cambiado dede que entro, luego lo modificamos en la receta
        if(fav !=  null) recipeClicked.setFavourite(!recipeClicked.isFavourite());

        initListAndRecyclerView(); //Volvemos a cargar la lista
    }

    //Utilities - Navigation
    @Override   //Cuando una receta es presionada...
    public void onRecipeClick(int position){
        this.recipeClicked = recipeList.get(position);  //Iniciamos la receta clickada
        //Guardamos esta en un singleton que se recibira en la activity siguiente: Details
        SingletonMap.getInstance().put(SHARE_RECIPE_KEY, recipeClicked);
        Intent i = new Intent(getActivity(), RecipeDetails.class);
        startActivity(i);   //Nos vamos a la Activity
    }

    //Iniciamos la activity para añadir recetas
    private void addRecipe(){
        startActivity(new Intent(getActivity(), AddUpdateRecipeActivity.class));
    }

    //Metodo para eliminar recetas cuadno deslizamos hacia la izquierda una card en el fragment
    private void deleteRecipe(RecipeModel recipeToDelete, int position, BBDD_Helper db){

        //Creacion de un Dialos de alerta que nos avise de lo que estamos haciendo : Borrar una receta
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle(R.string.recipe_fragment_alert_dialog_title);
        alertBuilder.setMessage(R.string.recipe_fragment_alert_dialog_message);

        //Definimos el comportamiento del dialog
        //Si presionamos aceptar : eliminar...
        alertBuilder.setPositiveButton(R.string.recipe_fragment_alert_dialog_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    if(recipeToDelete.isFavourite()){ //Si la recta es favorita mandamos mensaje de error
                        Toast.makeText(getActivity(),  R.string.recipe_detail_delete_fav_error, Toast.LENGTH_SHORT).show();
                    } else { //En caso contrario
                        recipeList.remove(position);    //Eliminamos la receta de la lista
                        adapter.setRecipeList(recipeList);  //seteamos la lista en el adapter
                        BD_Operations.deleteRecipe(recipeToDelete.getId(), db); //eliminamos la receta de la BD
                        recipeRecyclerView.setAdapter(adapter); //Seteamos el adapter

                        if(recipeList.isEmpty() && isFilter) { //Si habia filtro activo y la lista de ese filtro ahora esta vacia (no resultados del filtro)...
                            recipeList = null;

                            //Desactivamos filtros
                            isFilterMealType = false;
                            isFilterTimer = false;
                            isFilter = false;
                            SingletonMap.getInstance().put(SHARE_FILTER_KEY, null);
                            layoutFilters.setVisibility(View.GONE); //ocultamos el layout de filtros
                            //Avisamos con un toast de que se mostraran todas las recetas
                            Toast.makeText(getActivity(), getString(R.string.recipe_fragment_delete_showing_all), Toast.LENGTH_LONG).show();
                        } else Toast.makeText(getActivity(), R.string.recipe_fragment_delete_recipe_ok, Toast.LENGTH_LONG).show();  //Si aun quedan elementos, avisamos del eliminado
                    }
                    initListAndRecyclerView();  //Seteamos el recyclerview
                } catch (Exception e){
                    initListAndRecyclerView();  //Si no se ha podido eliminar...
                    Toast.makeText(getActivity(), getString(R.string.recipe_fragment_err_delete), Toast.LENGTH_SHORT).show();   //Avisamos del fallo
                }
            }
        });

        //Si presionamos el boton cancelar : no eliminar
        alertBuilder.setNegativeButton(R.string.recipe_fragment_alert_dialog_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();  //Cerramos el dialog
                initListAndRecyclerView();  //Volvemos a cargar la vista
            }
        });

        alertBuilder.create();  //Creamos el dialog
        alertBuilder.show();    //Mostramos el dialog
    }

    //Comprobar si a una receta se le ha dado o quitado el favoritos -> haciendo swipe a la derecha de la card
    private void favOrUnFav(RecipeModel recipe, BBDD_Helper db){
        if(!recipe.isFavourite()){  //Si no estaba en favoritos...
            try {
                //Cambiamos en la base de datos el atributo isFavourite a true
                BD_Operations.updateFavourite(recipe.getId(), true, db);
                //Toast anunciando que se ha añadido a favoritos
                Toast.makeText(getActivity(), R.string.recipe_fragment_favourite_recipe, Toast.LENGTH_SHORT).show();
            } catch (Exception e){  //Si se ha producido un error, avisamos del mismo
                Toast.makeText(getActivity(), getString(R.string.recipe_fragment_err_addFav), Toast.LENGTH_SHORT).show();
            }
        } else {    //Si estaba en favoritos, la quitamos de ahi
            try {
                //Cambiamos en la base de datos el atributo isFavourite a false
                BD_Operations.updateFavourite(recipe.getId(), false, db);
                //Toast anunciando que se ha añadido a favoritos
                Toast.makeText(getActivity(), R.string.recipe_fragment_no_favourite_recipe, Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Toast.makeText(getActivity(), getString(R.string.recipe_fragment_err_removeFav), Toast.LENGTH_SHORT).show();
            }
        }
        //Llamar initListAndRecyclerView
        initListAndRecyclerView();
    }

    //Definimos el comportamiento del Swipe, es decir, del desplaazmiento a izq y dcha de las cards en el recycler view
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override   //No permitimos movimiento de las cards
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override   //Si las deslizamos...
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition(); //Obtenemos la posicion de la que hemos deslizado
            RecipeModel recipe = recipeList.get(position);  //Obtenemos la receta que hay en esa posicion
            BBDD_Helper db = new BBDD_Helper(getActivity());
            if(direction == ItemTouchHelper.LEFT) deleteRecipe(recipe, position, db);   //Si es a la izquierda, eliminamos la receta
            else favOrUnFav(recipe, db);    //Si es a la derecha, revisamos si es fav o unFav
        }

        @Override   //Segun el lado para el que se deslice la card...
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_500))    //Si es a la izquierda, el fondo sera negro
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_app_bar)   //El logo sera la papelera
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.amber_500)) //Si es a la derecha, el fondo sera amarillo
                    .addSwipeRightActionIcon(R.drawable.ic_favourite)   //El logo sera un fav
                    .create()   //Creamos el swipe
                    .decorate();    //Seteamos los colores e iconos

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}