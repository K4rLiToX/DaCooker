package es.android.dacooker.adapters;

import android.annotation.SuppressLint;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    //Intefaz auxilar para delegar las funcionalidades al fragmento/activity correspondiente
    RecipeClickListener recipeClickListener;
    //Lista de recetas a mostrar
    List<RecipeModel> recipeList; //List to Show

    //Constructor
    public RecyclerViewAdapter(List<RecipeModel> recipeList, RecipeClickListener recipeClickListener) {
        this.recipeList = recipeList;
        this.recipeClickListener = recipeClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflo la vista de la card de la receta
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View recipeCardView = layoutInflater.inflate(R.layout.recipe_card_view, parent, false);
        return new ViewHolder(recipeCardView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        //Obtengo la receta de la posición position
        RecipeModel recipe = recipeList.get(position);
        //Si la imagen de la receta es distinto de nula, muestro dicha imagen
        //En caso contrario muestro la por defecto
        if(recipe.getImage() != null) holder.imgRecipeCard.setImageBitmap(recipe.getImage());
        else holder.imgRecipeCard.setImageResource(R.mipmap.img_recipe_card_default);
        //Seteo las vistas de la card de receta con los valores obtenidos de la receta
        holder.titleRecipeCard.setText(recipe.getRecipeName());
        holder.timeRecipeCard.setText(recipe.getExecutionTimeHour() + "h " + recipe.getExecutionTimeMinute()+"min");
        holder.mealTypeRecipeCard.setText(recipe.getMealType().name());
        //Si la receta está puesta como favorita hago visible el icono de fav
        //En caso contario lo oculto
        if(recipe.isFavourite()) holder.imgFavRecipeCard.setVisibility(View.VISIBLE);
        else holder.imgFavRecipeCard.setVisibility(View.GONE);
    }

    //Setear una nueva lista de recetas
    public void setRecipeList(List<RecipeModel> rList){
        //Guardo el tamaño de la lista a modificar
        int prevSize = this.recipeList.size();
        //Limpio la lista
        this.recipeList.clear();
        //Si la lista que me pasan por parámtero es nula, la inicializo a vacía
        if(rList == null) rList = new ArrayList<>();
        //Añado la lista que pasan por parámetro a la lista limpiada anteriormente
        this.recipeList.addAll(rList);
        //Notifico al adaptador que el rango de items se ha eliminado
        notifyItemRangeRemoved(0, prevSize);
        //Notifico al adaptardor que se ha añadido un nuevo rango de items
        notifyItemRangeInserted(0, rList.size());
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    //Clase interna
    class ViewHolder extends RecyclerView.ViewHolder {
        //Vistas a utilizar
        ImageView imgRecipeCard, imgFavRecipeCard;
        TextView titleRecipeCard, timeRecipeCard, mealTypeRecipeCard;

        //Inicializo las vistas que va a utlizar el método onBindViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgRecipeCard = itemView.findViewById(R.id.img_card_recipe);
            this.imgFavRecipeCard = itemView.findViewById(R.id.img_fav_card_recipe);
            this.titleRecipeCard = itemView.findViewById(R.id.recipe_card_title);
            this.timeRecipeCard = itemView.findViewById(R.id.recipe_card_time);
            this.mealTypeRecipeCard = itemView.findViewById(R.id.recipe_card_mealType);

            //Añado funcionalidad a la card en sí
            itemView.setOnClickListener( view -> {
                //Llamo a la interfaz pasándole la posición de la card de receta para que ejecute el método correspondiente
                recipeClickListener.onRecipeClick(getAdapterPosition());
            });
        }
    }
}
