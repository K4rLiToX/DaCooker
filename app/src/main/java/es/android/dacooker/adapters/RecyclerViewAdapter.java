package es.android.dacooker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.models.StepModel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    //List to Show
    List<RecipeModel> recipeList;
    //Interface for OnClick
    RecipeClickListener recipeClickListener;

    public RecyclerViewAdapter(List<RecipeModel> recipeList, RecipeClickListener recipeClickListener) {
        this.recipeList = recipeList;
        this.recipeClickListener = recipeClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View recipeCardView = layoutInflater.inflate(R.layout.recipe_card_view, parent, false);
        return new ViewHolder(recipeCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        RecipeModel recipe = recipeList.get(position);
        if(recipe.getImage() != null) holder.imgRecipeCard.setImageBitmap(recipe.getImage());
        else holder.imgRecipeCard.setImageResource(R.mipmap.img_recipe_card_default);
        holder.titleRecipeCard.setText(recipe.getRecipeName());
        holder.timeRecipeCard.setText(recipe.getExecutionTimeHour()+"h " + recipe.getExecutionTimeMinute()+"min");
        holder.mealTypeRecipeCard.setText(recipe.getMealType().name());
        if(recipe.isFavourite()) holder.imgFavRecipeCard.setVisibility(View.VISIBLE);
        else holder.imgFavRecipeCard.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void setRecipeList(List<RecipeModel> rList){
        int prevSize = this.recipeList.size();
        this.recipeList.clear();
        if(rList == null) rList = new ArrayList<>();
        this.recipeList.addAll(rList);
        notifyItemRangeRemoved(0, prevSize);
        notifyItemRangeInserted(0, rList.size());
    }

    //Intern Class
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipeCard, imgFavRecipeCard;
        TextView titleRecipeCard, timeRecipeCard, mealTypeRecipeCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgRecipeCard = itemView.findViewById(R.id.img_card_recipe);
            this.imgFavRecipeCard = itemView.findViewById(R.id.img_fav_card_recipe);
            this.titleRecipeCard = itemView.findViewById(R.id.recipe_card_title);
            this.timeRecipeCard = itemView.findViewById(R.id.recipe_card_time);
            this.mealTypeRecipeCard = itemView.findViewById(R.id.recipe_card_mealType);

            itemView.setOnClickListener( view -> {
                recipeClickListener.onRecipeClick(getAdapterPosition());
            });
        }
    }
}
