package es.android.dacooker.adapters;

import android.content.Context;
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
import es.android.dacooker.models.IngredientModel;

public class AddIngredientRecyclerAdapter extends RecyclerView.Adapter<AddIngredientRecyclerAdapter.MyViewHolder> {

    List<IngredientModel> ingredientList;

    public AddIngredientRecyclerAdapter(List<IngredientModel> ingredientList){
        this.ingredientList = ingredientList;
    }

    /*
    public void adviseAdd(IngredientModel ing){
        this.ingredientList.add(ing);
        notifyItemInserted(ingredientList.size()-1);
    }
    */

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View ingredientItemView = layoutInflater.inflate(R.layout.add_ingredient_adapter_item, parent, false);
        MyViewHolder vh = new MyViewHolder(ingredientItemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        IngredientModel ingredient = ingredientList.get(position);
        holder.nameIngredient.setText(ingredient.getIngredientName());
        holder.quantityIngredient.setText(ingredient.getQuantity());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    //Intern Class
    static class MyViewHolder extends RecyclerView.ViewHolder {
        Button btnDelete;
        TextView nameIngredient, quantityIngredient;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.btnDelete = itemView.findViewById(R.id.add_ingredient_listView_btnDelete);
            this.nameIngredient = itemView.findViewById(R.id.ingredient_name_add_listView);
            this.quantityIngredient = itemView.findViewById(R.id.ingredient_quantity_add_listView);
        }
    }
}
