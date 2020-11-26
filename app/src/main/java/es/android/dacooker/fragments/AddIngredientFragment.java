package es.android.dacooker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.adapters.AddIngredientRecyclerAdapter;
import es.android.dacooker.adapters.AddStepRecyclerAdapter;
import es.android.dacooker.models.IngredientModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddIngredientFragment extends Fragment {

    EditText til_name, til_quantity;
    Button btnAdd;
    List<IngredientModel> ingredientList;

    AddIngredientRecyclerAdapter rwAdapter;
    RecyclerView rw;


    public AddIngredientFragment() {
        // Required empty public constructor
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //rw = getView().findViewById(R.id.add_ingredient_recycler);
        //rw.setAdapter(rwAdapter);
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_ingredient, container, false);

        //Initialize Elements
        til_name = v.findViewById(R.id.ingredient_name_input);
        til_quantity = v.findViewById(R.id.ingredient_quantity_input);
        btnAdd = v.findViewById(R.id.add_ingredient_btnAdd);
        rw = v.findViewById(R.id.add_ingredient_recycler);

        ingredientList = new ArrayList<>();
        rwAdapter = new AddIngredientRecyclerAdapter(getActivity().getApplicationContext(), ingredientList);
        rw.setAdapter(rwAdapter);

        btnAdd.setOnClickListener(view -> {

            if(validFields()) {
                IngredientModel ing = new IngredientModel();
                ing.setIngredientName(til_name.getText().toString());
                ing.setQuantity(til_quantity.getText().toString());
                ingredientList.add(ing);

                //Eliminar cuando funcione
                Toast.makeText(getActivity().getApplicationContext(), ingredientList.size()+"", Toast.LENGTH_SHORT).show();

                rwAdapter.notifyItemInserted(ingredientList.size()-1);
                til_name.setText("");
                til_quantity.setText("");

            } else {
                //Errores
                Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }

        });

        return v;
    }

    private boolean validFields(){  //Cambiar para Validación Bien
        if(til_name.getText().toString().trim().equalsIgnoreCase("") || til_quantity.getText().toString().trim().equalsIgnoreCase("") ||
                til_name.getText().toString() == null || til_quantity.getText().toString() == null ) return false;

        return true;
    }
}