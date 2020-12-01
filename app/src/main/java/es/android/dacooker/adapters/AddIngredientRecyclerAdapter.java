package es.android.dacooker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.models.IngredientModel;

public class AddIngredientRecyclerAdapter extends RecyclerView.Adapter<AddIngredientRecyclerAdapter.MyViewHolder> {

    private final Context context;
    List<IngredientModel> ingredientList;

    public AddIngredientRecyclerAdapter(Context context, List<IngredientModel> ingredientList){
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View ingredientItemView = layoutInflater.inflate(R.layout.add_ingredient_adapter_item, parent, false);
        return new MyViewHolder(ingredientItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        IngredientModel ingredient = ingredientList.get(position);
        holder.nameIngredient.setText(ingredient.getIngredientName());
        holder.quantityIngredient.setText(ingredient.getQuantity());
        holder.btnDelete.setOnClickListener( view -> {
            ingredientList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, ingredientList.size());
        });
    }

    public List<IngredientModel> getList(){
        return ingredientList;
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    //Intern Class
    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView btnDelete;
        TextView nameIngredient, quantityIngredient;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.btnDelete = itemView.findViewById(R.id.add_ingredient_listView_btnDelete);
            this.nameIngredient = itemView.findViewById(R.id.ingredient_name_add_listView);
            this.quantityIngredient = itemView.findViewById(R.id.ingredient_quantity_add_listView);
        }
    }

    public void setEditList(List<IngredientModel> ingredientList){
        int prevSize = this.ingredientList.size();
        this.ingredientList.clear();
        if(ingredientList == null) ingredientList = new ArrayList<>();
        this.ingredientList.addAll(ingredientList);
        notifyItemRangeRemoved(0, prevSize);
        notifyItemRangeInserted(0, ingredientList.size());
    }
}
