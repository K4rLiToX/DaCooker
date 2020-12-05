package es.android.dacooker.interfaces;

//Interfaz para comunicar a la activity la receta pulsada en el recyclerView de recetas inicial
public interface RecipeClickListener {
    void onRecipeClick(int position);
}
