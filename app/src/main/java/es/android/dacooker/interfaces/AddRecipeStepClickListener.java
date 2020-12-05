package es.android.dacooker.interfaces;

//Interfaz usada en el fragment addStep que permite comunicar al fragment
// la accion a realizar con el paso : editar o borrar
public interface AddRecipeStepClickListener {
    void onEditListener(int position);
    void onDeleteListener(int position);
}
