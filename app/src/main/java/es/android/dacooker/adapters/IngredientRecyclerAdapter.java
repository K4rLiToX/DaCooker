package es.android.dacooker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.models.IngredientModel;

public class IngredientRecyclerAdapter extends RecyclerView.Adapter<IngredientRecyclerAdapter.IngredientViewHolder>{

    //List to Show
    List<IngredientModel> ingredientList;

    public IngredientRecyclerAdapter(List<IngredientModel> ingredientList){
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientRecyclerAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View ingredientCardView = layoutInflater.inflate(R.layout.ingredient_dropdown_card, parent, false);
        return new IngredientViewHolder(ingredientCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientRecyclerAdapter.IngredientViewHolder holder, int position) {
        IngredientModel ingredient = ingredientList.get(position);
        holder.ingredientCardName.setText(ingredient.getIngredientName());
        holder.ingredientCardQuantity.setText(ingredient.getQuantity());
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    //Intern Class
    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView ingredientCardName, ingredientCardQuantity;
        LinearLayout expandableLayout;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ingredientCardName = itemView.findViewById(R.id.ingredient_card_name);
            this.ingredientCardQuantity = itemView.findViewById(R.id.ingredient_card_quantity);
            this.expandableLayout = itemView.findViewById(R.id.expandableLayout);
        }
    }
}
