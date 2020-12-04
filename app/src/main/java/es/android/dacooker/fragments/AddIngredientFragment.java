package es.android.dacooker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.activities.AddUpdateRecipeActivity;
import es.android.dacooker.adapters.AddIngredientRecyclerAdapter;
import es.android.dacooker.models.IngredientModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddIngredientFragment extends Fragment {

    //Vistas
    TextInputEditText til_name, til_quantity;
    Button btnAdd;
    RecyclerView rw;

    //Lista de ingredientes
    List<IngredientModel> ingredientList;

    //Adaptador para el recyclerview de ingredientes
    AddIngredientRecyclerAdapter rwAdapter;

    //Constructor
    public AddIngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Infla la vista del fragmento añadir ingrediente
        View v = inflater.inflate(R.layout.fragment_add_ingredient, container, false);

        initView(v);
        initButton();

        return v;
    }

    //Inicializa las vistas a utilizar
    private void initView(View v){
        til_name = v.findViewById(R.id.ingredient_name_input);
        til_quantity = v.findViewById(R.id.ingredient_quantity_input);
        btnAdd = v.findViewById(R.id.add_ingredient_btnAdd);
        rw = v.findViewById(R.id.add_ingredient_recycler);

        //Inicializa la lista de ingredientes
        ingredientList = new ArrayList<>();
        //Crea el adaptador
        rwAdapter = new AddIngredientRecyclerAdapter(getActivity().getApplicationContext(), ingredientList);
        //Setea el adaptador al recyclerview
        rw.setAdapter(rwAdapter);

        ((AddUpdateRecipeActivity)getActivity()).callFromEditFragment(null, v, null);
    }

    //Inicializa los botones
    private void initButton(){
        //Añade funcionalidad al boton de añadir ingrediente
        btnAdd.setOnClickListener(view -> {
            //Creo una variable donde verifico que los valores de los inputs son válidos
            String err = validFields();
            if(err.equalsIgnoreCase("")) { //Si no hay errores
                //Creo el ingrediente a añadir
                IngredientModel ing = new IngredientModel();
                //Seteo los valores obtenidos de los inputs
                ing.setIngredientName(til_name.getText().toString());
                ing.setQuantity(til_quantity.getText().toString());
                //Añado el ingrediente a la lista (No se añade a la BD porque eso se hace cuando se quiere añadir la receta completa)
                ingredientList.add(ing);
                //Notifico al adaptador que se ha insertado un ingrediente en la última posición de la lista de ingredientes
                rwAdapter.notifyItemInserted(ingredientList.size()-1);
                //Vacio los inputs correspondientes
                til_name.setText("");
                til_quantity.setText("");
            } else { //Si hay errores mando mensaje notificando
                Toast.makeText(getActivity().getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Validación de campos
    private String validFields(){
        //Variable donde guardo el error (si lo hay)
        String err = "";
        //Si el nombre del ingrediente es vacio o supera los 100 caracteres guardo error
        //Si la cantidad del ingrediente es vacía o supera los 50 caracteres guardo error
        if(til_name.getText().toString().trim().equalsIgnoreCase("") || til_name.getText().toString().length() > 100) err = getString(R.string.addIng_err_name);
        else if(til_quantity.getText().toString().trim().equalsIgnoreCase("") || til_quantity.getText().toString().length() > 50)  err = getString(R.string.addIng_err_quantity);

        //Devuelvo el error
        return err;
    }
}