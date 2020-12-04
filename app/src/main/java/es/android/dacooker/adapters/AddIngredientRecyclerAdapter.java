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

    //Contexto de la Actividad
    private final Context context;
    //Lista de ingredientes a mostrar
    List<IngredientModel> ingredientList;

    //Constructor
    public AddIngredientRecyclerAdapter(Context context, List<IngredientModel> ingredientList){
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflamos la vista de la card de añadir ingrediente (no es la misma que la de ingrediente. Es parecida. La única diferencia es el botón de eliminar)
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View ingredientItemView = layoutInflater.inflate(R.layout.add_ingredient_adapter_item, parent, false);
        return new MyViewHolder(ingredientItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Obtenemos el ingrediente en la posicion position
        IngredientModel ingredient = ingredientList.get(position);
        //Asignamos a las vistas los valores de los atributos del ingrediente correspondiente
        holder.nameIngredient.setText(ingredient.getIngredientName());
        holder.quantityIngredient.setText(ingredient.getQuantity());
        //Añadimos funcionalidad al boton eliminar
        holder.btnDelete.setOnClickListener( view -> {
            //Eliminamos el ingrediente de la lista de ingredientes
            //No lo eliminamos de la base de datos porque todavía no se ha insertado la receta completa
            ingredientList.remove(position);
            //Notificamos al adaptador que se ha eliminado un elemento
            notifyItemRemoved(position);
            //Notificamos al adaptador que el rango de items ha sido modficado
            notifyItemRangeChanged(position, ingredientList.size());
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    //Devuelve la lista de ingredientes
    public List<IngredientModel> getList(){
        return ingredientList;
    }

    //Setea una lista de ingredientes
    public void setEditList(List<IngredientModel> ingredientList){
        //Guardamos el tamaño de la lista que va a ser modificada
        int prevSize = this.ingredientList.size();
        //Limpiamos la lista
        this.ingredientList.clear();
        //Si la lista que me pasan es nula, la inicializo a vacía
        if(ingredientList == null) ingredientList = new ArrayList<>();
        //Añado a la lista que he vaciado la lista que me pasan por parámetro (puede estar vacia o llena)
        this.ingredientList.addAll(ingredientList);
        //Notifico al adaptador que el el rango de items ha sido eliminado
        notifyItemRangeRemoved(0, prevSize);
        //Notifico al adaptador que se ha insertado un nuevo rango de items
        notifyItemRangeInserted(0, ingredientList.size());
    }

    //Clase interna que tiene las vistas de la card de ingrediente
    static class MyViewHolder extends RecyclerView.ViewHolder {
        //Vistas a utilizar
        ImageView btnDelete;
        TextView nameIngredient, quantityIngredient;

        //Seteo de las vistas
        //Esto lo utilizará el método onBindViewHolder
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.btnDelete = itemView.findViewById(R.id.add_ingredient_listView_btnDelete);
            this.nameIngredient = itemView.findViewById(R.id.ingredient_name_add_listView);
            this.quantityIngredient = itemView.findViewById(R.id.ingredient_quantity_add_listView);
        }
    }
}
