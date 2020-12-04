package es.android.dacooker.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.activities.AddUpdateRecipeActivity;
import es.android.dacooker.models.MealType;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment {

    //Constants
    private final static int PERMISSION_CODE = 1000;
    private final int GALLERY_OPTION = 10;

    MealType[] MEALTYPES = MealType.values();
    AutoCompleteTextView mealTypeDropdown;
    ArrayAdapter<MealType> adapter;

    TextInputEditText recipeName, recipeHours, recipeMinutes, recipeDescription;
    FloatingActionButton fabChooseRecipePhoto;
    ImageView recipePhoto;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        initView(v);
        initButton();

        return v;
    }

    //Init View
    private void initView(View v){
        adapter =  new ArrayAdapter<>(getActivity(), R.layout.meal_type_dropdown_item, MEALTYPES);
        mealTypeDropdown = v.findViewById(R.id.recipe_mealType_dropdown_select);
        mealTypeDropdown.setAdapter(adapter);

        recipePhoto = v.findViewById(R.id.recipe_img_input);
        recipeName = v.findViewById(R.id.recipe_name_input);
        recipeHours = v.findViewById(R.id.recipe_hour_input);
        recipeMinutes = v.findViewById(R.id.recipe_minute_input);
        recipeDescription = v.findViewById(R.id.recipe_description_input);
        fabChooseRecipePhoto = v.findViewById(R.id.fabChooseRecipePhoto);

        ((AddUpdateRecipeActivity)getActivity()).callFromEditFragment(v, null, null);
    }

    private void initButton(){
        fabChooseRecipePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:   //Se aceptan los permisos -> abrimos cámara
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Permisos conseguidos
                    openGallery();
                } else {    //Permisos denegados
                    Toast.makeText(getActivity(), "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
        }

    }

    private void openGallery(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Sin permisos aún -> se piden
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        } else {//Permisos Dados = OK

            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(galleryIntent, getActivity().getString(R.string.add_recipe_alert_dialog_choose_gallery_app)), GALLERY_OPTION);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == this.GALLERY_OPTION) {
            Uri miPath = Objects.requireNonNull(data).getData();
            recipePhoto.setImageURI(miPath);
        }
    }

}