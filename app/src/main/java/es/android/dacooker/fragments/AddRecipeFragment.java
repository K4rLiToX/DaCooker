package es.android.dacooker.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

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

    EditText recipeName, recipeHours, recipeMinutes, recipeDescription;
    ImageView recipePhoto;


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

        recipePhoto = v.findViewById(R.id.recipe_img_input);
        recipeName = v.findViewById(R.id.recipe_name_input);
        recipeHours = v.findViewById(R.id.recipe_hour_input);
        recipeMinutes = v.findViewById(R.id.recipe_minute_input);
        recipeDescription = v.findViewById(R.id.recipe_description_input);


        FloatingActionButton fabTakePhoto = v.findViewById(R.id.fabTakeRecipePhoto);
        fabTakePhoto.setOnClickListener(click ->{
            takeRecipePhoto();
        });

        FloatingActionButton fabSelectPhoto = v.findViewById(R.id.fabSelectRecipePhoto);
        fabSelectPhoto.setOnClickListener(click ->{
            selectRecipePhoto();
        });

        return v;
    }

    private void takeRecipePhoto() {

    }

    private void selectRecipePhoto(){
        Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGallery.setType("images/");
        startActivityForResult(intentGallery.createChooser(intentGallery, "Seleccione la Aplicación"), 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            Uri path = data.getData();
            recipePhoto.setImageURI(path);
        }
    }
}