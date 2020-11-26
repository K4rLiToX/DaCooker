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

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.models.IngredientModel;

public class AddIngredientRecyclerAdapter extends RecyclerView.Adapter<AddIngredientRecyclerAdapter.MyViewHolder> {

    private Context context;
    private List<IngredientModel> ingredientList;

    public AddIngredientRecyclerAdapter(Context context, List<IngredientModel> ingredientList){
        this.context = context;
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

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View ingredientItemView = layoutInflater.inflate(R.layout.add_ingredient_adapter_item, parent, false);
        MyViewHolder vh = new MyViewHolder(ingredientItemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        IngredientModel ingredient = ingredientList.get(position);
        holder.nameIngredient.setText(ingredient.getIngredientName());
        holder.quantityIngredient.setText(ingredient.getQuantity());
        holder.btnDelete.setOnClickListener( view -> {
            ingredientList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    //Intern Class
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton btnDelete;
        TextView nameIngredient, quantityIngredient;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.btnDelete = itemView.findViewById(R.id.add_ingredient_listView_btnDelete);
            this.nameIngredient = itemView.findViewById(R.id.ingredient_name_add_listView);
            this.quantityIngredient = itemView.findViewById(R.id.ingredient_quantity_add_listView);
        }
    }

    public void setItems(List<IngredientModel> newItems)
    {
        clearItems();
        addItems(newItems);
    }

    public void addItem(IngredientModel item, int position)
    {
        if (position > ingredientList.size()) return;

        ingredientList.add(item);
        notifyItemInserted(position);
    }

    public void addMoreItems(List<IngredientModel> newItems)
    {
        int position = ingredientList.size() + 1;
        newItems.addAll(newItems);
        notifyItemChanged(position, newItems);
    }

    public void addItems(List<IngredientModel> newItems)
    {
        ingredientList.addAll(newItems);
        notifyDataSetChanged();
    }

    public void clearItems()
    {
        ingredientList.clear();
        notifyDataSetChanged();
    }

    public void addLoader()
    {
        ingredientList.add(null);
        notifyItemInserted(ingredientList.size() - 1);
    }

    public void removeLoader()
    {
        ingredientList.remove(ingredientList.size() - 1);
        notifyItemRemoved(ingredientList.size());
    }

    public void removeItem(int position)
    {
        if (position >= ingredientList.size()) return;

        ingredientList.remove(position);
        notifyItemRemoved(position);
    }

    public void swapItems(int positionA, int positionB)
    {
        if (positionA > ingredientList.size()) return;
        if (positionB > ingredientList.size()) return;

        IngredientModel firstItem = ingredientList.get(positionA);

        ingredientList.set(positionA, ingredientList.get(positionB));
        ingredientList.set(positionB, firstItem);

        notifyDataSetChanged();
    }
}
