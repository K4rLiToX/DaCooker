package es.android.dacooker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.android.dacooker.R;
import es.android.dacooker.models.MealType;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment {

    MealType[] MEALTYPES = MealType.values();
    ArrayAdapter<MealType> adapter;
    AutoCompleteTextView mealTypeDropdown;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        adapter =  new ArrayAdapter<>(getActivity(), R.layout.meal_type_dropdown_item, MEALTYPES);
        mealTypeDropdown = v.findViewById(R.id.recipe_mealType_dropdown_select);
        mealTypeDropdown.setAdapter(adapter);

        FloatingActionButton fabChangePhoto = v.findViewById(R.id.fabAddRecipeImage);
        fabChangePhoto.setOnClickListener(click ->{
            addOrChangeRecipePhoto();
        });

        return v;
    }

    private void addOrChangeRecipePhoto() {

    }
}