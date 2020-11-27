package es.android.dacooker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        RecipeModel recipe = recipeList.get(position);
        if(recipe.getImage() != null) holder.imgRecipeCard.setImageBitmap(recipe.getImage());
        else holder.imgRecipeCard.setImageResource(R.drawable.img_recipe_card_default);
        holder.titleRecipeCard.setText(recipe.getRecipeName());
        holder.timeRecipeCard.setText(recipe.getExecutionTime());
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    //Intern Class
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipeCard;
        TextView titleRecipeCard, timeRecipeCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgRecipeCard = itemView.findViewById(R.id.img_card_recipe);
            this.titleRecipeCard = itemView.findViewById(R.id.recipe_card_title);
            this.timeRecipeCard = itemView.findViewById(R.id.recipe_card_time);

            itemView.setOnClickListener( view -> {
                recipeClickListener.onRecipeClick(getAdapterPosition());
            });
        }
    }
}
