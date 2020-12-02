package es.android.dacooker.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";

    //List to Show
    private List<RecipeModel> recipeList;
    //Views
    private RecyclerView recipeRecyclerView;
    private TextView tvNoRecipes;
    //Adapters
    RecyclerViewAdapter adapter;
    //ItemTouch
    ItemTouchHelper itemTouchHelper;


    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        recipeRecyclerView = view.findViewById(R.id.recipe_recyclerView);
        tvNoRecipes = view.findViewById(R.id.tvEmptyRecipes);
        initListAndRecyclerView();
        FloatingActionButton btnAddRecipe = view.findViewById(R.id.fabAddRecipe);
        btnAddRecipe.setOnClickListener(click -> {
            addRecipe();
        });

        return view;
    }

    private void initListAndRecyclerView() {
        BBDD_Helper db = new BBDD_Helper(getActivity());
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
                RecipeModel recipeToDelete = recipeList.get(position);
                deleteRecipe(recipeToDelete, position);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
        SingletonMap.getInstance().put(SHARE_RECIPE_KEY, recipe);
        Intent i = new Intent(getActivity(), RecipeDetails.class);
        startActivity(i);
    }

    public void addRecipe(){
        startActivity(new Intent(getActivity(), AddNewRecipeActivity.class));
    }


    @Override
    public void onResume() {
        super.onResume();
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
                    BD_Operations.deleteRecipe(recipeToDelete.getId(), db);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, recipeList.size());
                    if(recipeList.isEmpty()){
                        tvNoRecipes.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getActivity(), R.string.recipe_fragment_delete_recipe_ok, Toast.LENGTH_SHORT).show();
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