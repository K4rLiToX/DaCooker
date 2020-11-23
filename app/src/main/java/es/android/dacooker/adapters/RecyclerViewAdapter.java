package es.android.dacooker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    //List to Show
    private List<RecipeModel> recipeList;
    //Interface for OnClick
    private RecipeClickListener recipeClickListener;

    public RecyclerViewAdapter(List<RecipeModel> recipeList, RecipeClickListener recipeClickListener) {
        this.recipeList = recipeList;
        this.recipeClickListener = recipeClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View recipeCardView = layoutInflater.inflate(R.layout.recipe_card_view, parent, false);
        ViewHolder vh = new ViewHolder(recipeCardView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    //Intern Class
    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
