package es.android.dacooker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import es.android.dacooker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment {

    String[] MEALTYPES = new String[]{"Launch", "Dinner", "Other"};
    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.meal_type_dropdown_item, MEALTYPES);
    AutoCompleteTextView mealTypeDropdown = getView().findViewById(R.id.recipe_mealType_dropdown_select);

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mealTypeDropdown.setAdapter(adapter);
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }
}