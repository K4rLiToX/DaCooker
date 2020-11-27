package es.android.dacooker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder>{

    //List to Show
    List<RecipeModel> recipeList;
    //Interface for OnClick
    RecipeClickListener recipeClickListener;

    public RecipeRecyclerViewAdapter(List<RecipeModel> recipeList, RecipeClickListener recipeClickListener) {
        this.recipeList = recipeList;
        this.recipeClickListener = recipeClickListener;
    }

    @NonNull
    @Override
    public RecipeRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View recipeCardView = layoutInflater.inflate(R.layout.recipe_card_view, parent, false);
        ViewHolder vh = new ViewHolder(recipeCardView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRecyclerViewAdapter.ViewHolder holder, int position) {
        RecipeModel recipe = recipeList.get(position);
        holder.imgRecipeCard.setImageBitmap(recipe.getImage());
        holder.titleRecipeCard.setText(recipe.getRecipeName());
        holder.timeRecipeCard.setText(recipe.getExecutionTime());
        holder.btnDeleteRecipeCard.setOnClickListener(v -> {
            recipeList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, recipeList.size());
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    //Inner Class to Manage One Card Item
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipeCard;
        TextView titleRecipeCard, timeRecipeCard;
        Button btnDeleteRecipeCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgRecipeCard = itemView.findViewById(R.id.img_card_recipe);
            this.titleRecipeCard = itemView.findViewById(R.id.recipe_card_title);
            this.timeRecipeCard = itemView.findViewById(R.id.recipe_card_time);
            this.btnDeleteRecipeCard = itemView.findViewById(R.id.btnDeleteRecipe_card);

            itemView.setOnClickListener( view -> {
                recipeClickListener.onRecipeClick(getAdapterPosition());
            });
        }
    }
}
