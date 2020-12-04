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
import es.android.dacooker.activities.AddNewRecipeActivity;
import es.android.dacooker.adapters.AddIngredientRecyclerAdapter;
import es.android.dacooker.models.IngredientModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddIngredientFragment extends Fragment {

    TextInputEditText til_name, til_quantity;
    List<IngredientModel> ingredientList;
    Button btnAdd;

    AddIngredientRecyclerAdapter rwAdapter;
    RecyclerView rw;

    public AddIngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_ingredient, container, false);

        initView(v);
        initButton();

        return v;
    }

    //Init View
    private void initView(View v){
        til_name = v.findViewById(R.id.ingredient_name_input);
        til_quantity = v.findViewById(R.id.ingredient_quantity_input);
        btnAdd = v.findViewById(R.id.add_ingredient_btnAdd);
        rw = v.findViewById(R.id.add_ingredient_recycler);

        ingredientList = new ArrayList<>();
        rwAdapter = new AddIngredientRecyclerAdapter(getActivity().getApplicationContext(), ingredientList);
        rw.setAdapter(rwAdapter);

        ((AddNewRecipeActivity)getActivity()).callFromEditFragment(null, v, null);
    }

    private void initButton(){
        btnAdd.setOnClickListener(view -> {

            String err = validFields();
            if(err.equalsIgnoreCase("")) {
                IngredientModel ing = new IngredientModel();
                ing.setIngredientName(til_name.getText().toString());
                ing.setQuantity(til_quantity.getText().toString());
                ingredientList.add(ing);

                rwAdapter.notifyItemInserted(ingredientList.size()-1);
                til_name.setText("");
                til_quantity.setText("");

            } else {
                //Errores
                Toast.makeText(getActivity().getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        });
    }

    //Utilities - Validation
    private String validFields(){

        String err = "";
        if(til_name.getText().toString().trim().equalsIgnoreCase("")
                || til_name.getText().toString().length() > 100)
            err = getString(R.string.addIng_err_name);
        else if(til_quantity.getText().toString().trim().equalsIgnoreCase("")
                || til_quantity.getText().toString().length() > 50)
            err = getString(R.string.addIng_err_quantity);

        return err;
    }
}