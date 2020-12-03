package es.android.dacooker.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.activities.AddNewRecipeActivity;
import es.android.dacooker.activities.MainActivity;
import es.android.dacooker.activities.RecipeDetails;
import es.android.dacooker.adapters.RecyclerViewAdapter;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.MealType;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.models.StepModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment implements RecipeClickListener{

    //SingletonMap Key
    private static final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";
    private static final String SHARE_RESULT_LIST_KEY = "SHARE_RESULT_LIST_KEY";
    private static final String SHARE_FILTER_KEY = "SHARE_FILTER_SEARCH";

    //Filters
    boolean isFilterMealType, isFilterTimer, isFilter;
    String[] filters;

    //List to Show
    private List<RecipeModel> recipeList;
    //Views
    private RecyclerView recipeRecyclerView;
    private LinearLayout layoutFilters;
    private TextView tvNoRecipes, filterField;
    private Button btnUndoFilter;
    //Adapters
    RecyclerViewAdapter adapter;
    //ItemTouch
    ItemTouchHelper itemTouchHelper;
    private RecipeModel recipeClicked;


    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        recipeRecyclerView = view.findViewById(R.id.recipe_recyclerView);
        tvNoRecipes = view.findViewById(R.id.tvEmptyRecipes);

        layoutFilters = view.findViewById(R.id.recipe_fragment_layout_showFilters);
        filterField = view.findViewById(R.id.recipe_fragment_filterApplied);
        btnUndoFilter = view.findViewById(R.id.recipe_fragment_btnUndo);

        this.recipeClicked = new RecipeModel();

        btnUndoFilter.setOnClickListener(undo -> {
            recipeList = null;
            filters = null;
            isFilter = false;
            isFilterMealType = false;
            isFilterTimer = false;
            SingletonMap.getInstance().put(SHARE_FILTER_KEY, null);
            initListAndRecyclerView();
            layoutFilters.setVisibility(View.GONE);
        });

        initListAndRecyclerView();
        FloatingActionButton btnAddRecipe = view.findViewById(R.id.fabAddRecipe);
        btnAddRecipe.setOnClickListener(click -> {
            addRecipe();
        });

        return view;
    }

    private void initListAndRecyclerView() {
        BBDD_Helper db = new BBDD_Helper(getActivity());

        filters = (String[]) SingletonMap.getInstance().get(SHARE_FILTER_KEY);
        if(filters == null) {
            Log.e("Filters","NULL");
            filters = new String[]{"", ""};
        }
        if(filters[0].equalsIgnoreCase("")){
            Log.e("Filters: ", "none");
            isFilterMealType = false;
            isFilterTimer = false;
            isFilter = false;
            this.recipeList = null;
        } else {
            isFilter = true;
            Log.e("Filters: ", "Active");
            if(filters[0].equals("1")) isFilterMealType = true;
            else isFilterTimer = true;

            this.recipeList = (List<RecipeModel>) SingletonMap.getInstance().get(SHARE_RESULT_LIST_KEY);
        }

        if(recipeRecyclerView == null) { recipeRecyclerView = getView().findViewById(R.id.recipe_recyclerView); }

        if(isFilter){
            layoutFilters.setVisibility(View.VISIBLE);
            if(isFilterMealType){
                filterField.setText(filters[1]);
                Log.e("Filters: ", "mealtype");
                try { this.recipeList = BD_Operations.getRecipesByMealType(MealType.valueOf(filters[1]), db);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.recipe_fragment_delete_showing_all), Toast.LENGTH_LONG).show();
                    this.recipeList = BD_Operations.getRecipes(db);
                }

            } else {    //isFilterTimer == true
                Log.e("Filters: ", "timer");
                String[] time = filters[1].split(":");
                filterField.setText(getString(R.string.filters_less) + " " + time[0]+"h " + time[1] + "min");
                try { this.recipeList = BD_Operations.getRecipesByLessExecutionTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]), db);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.recipe_fragment_delete_showing_all), Toast.LENGTH_LONG).show();
                    this.recipeList = BD_Operations.getRecipes(db);
                }
            }

            if(adapter == null) adapter = new RecyclerViewAdapter(recipeList,this);
            else adapter.setRecipeList(this.recipeList);

            recipeRecyclerView.setAdapter(adapter);
            itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recipeRecyclerView);
        } else {
            this.recipeList = BD_Operations.getRecipes(db);
            adapter = new RecyclerViewAdapter(recipeList,this);

            if(this.recipeList.isEmpty()){
                //Mostramos Text View (no hay recetas)
                this.tvNoRecipes.setVisibility(View.VISIBLE);
            } else {
                this.tvNoRecipes.setVisibility(View.GONE);
                recipeRecyclerView.setAdapter(adapter);
                //Set the itemtouchhelper to delete on swipe
                itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recipeRecyclerView);
            }
        }
    }

    //LEFT is for action on swipe from left to right
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if(direction == ItemTouchHelper.LEFT){
                RecipeModel recipeToDelete;
                recipeToDelete = recipeList.get(position);
                deleteRecipe(recipeToDelete, position);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_500))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_app_bar)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    @Override
    public void onRecipeClick(int position){
        RecipeModel recipe = recipeList.get(position);
        this.recipeClicked = recipe;
        SingletonMap.getInstance().put(SHARE_RECIPE_KEY, recipe);
        Intent i = new Intent(getActivity(), RecipeDetails.class);
        startActivity(i);
    }

    private void addRecipe(){
        startActivity(new Intent(getActivity(), AddNewRecipeActivity.class));
    }


    @Override
    public void onResume() {
        super.onResume();
        String isDeleted = (String) SingletonMap.getInstance().get("SHARE_RECETA_ELIMINADA");
        if(recipeList.contains(recipeClicked) && recipeList.size() == 1 && isDeleted != null){
            SingletonMap.getInstance().put(SHARE_FILTER_KEY, null);
            layoutFilters.setVisibility(View.GONE);
        }
        initListAndRecyclerView();
    }

    private void deleteRecipe(RecipeModel recipeToDelete, int position){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle(R.string.recipe_fragment_alert_dialog_title);
        alertBuilder.setMessage(R.string.recipe_fragment_alert_dialog_message);
        alertBuilder.setPositiveButton(R.string.recipe_fragment_alert_dialog_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {

                    BBDD_Helper db = new BBDD_Helper(getActivity());
                    recipeList.remove(position);
                    adapter.setRecipeList(recipeList);
                    BD_Operations.deleteRecipe(recipeToDelete.getId(), db);
                    recipeRecyclerView.setAdapter(adapter);

                    if(recipeList.isEmpty() && isFilter) { //Mostramos todas las recetas de la BD
                        recipeList = null;
                        isFilterMealType = false;
                        isFilterTimer = false;
                        isFilter = false;
                        SingletonMap.getInstance().put(SHARE_FILTER_KEY, null);
                        initListAndRecyclerView();
                        layoutFilters.setVisibility(View.GONE);
                        //Toast.makeText(getActivity(), getString(R.string.recipe_fragment_delete_showing_all), Toast.LENGTH_LONG).show();
                    } else Toast.makeText(getActivity(), R.string.recipe_fragment_delete_recipe_ok, Toast.LENGTH_LONG).show();

                } catch (Exception e){
                    initListAndRecyclerView();
                    Toast.makeText(getActivity(), "Error on Delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertBuilder.setNegativeButton(R.string.recipe_fragment_alert_dialog_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                initListAndRecyclerView();
            }
        });
        alertBuilder.create();
        alertBuilder.show();
    }

}