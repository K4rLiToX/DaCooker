package es.android.dacooker.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.models.MealType;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment {

    //Constants
    private final int CAMERA_OPTION = 20;
    private final int GALLERY_OPTION = 10;

    MealType[] MEALTYPES = MealType.values();
    ArrayAdapter<MealType> adapter;
    AutoCompleteTextView mealTypeDropdown;

    EditText recipeName, recipeHours, recipeMinutes, recipeDescription;
    ImageView recipePhoto;

    //Propio
    private Uri photoURI;

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


        FloatingActionButton fabChooseRecipePhoto = v.findViewById(R.id.fabChooseRecipePhoto);
        fabChooseRecipePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCameraOrGallery();
            }
        });

        return v;
    }

    private void chooseCameraOrGallery(){
        String camera = getString(R.string.add_recipe_alert_dialog_camera);
        String gallery = getString(R.string.add_recipe_alert_dialog_gallery);
        final String[] options = {camera, gallery};

        buildAlertDialog(options);
    }

    private void buildAlertDialog(String[] options){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.add_recipe_alert_dialog_title);
        alertDialog.setItems(options, (dialogInterface, i) -> {
            //Open Camera
            if(options[i].equalsIgnoreCase(getString(R.string.add_recipe_alert_dialog_camera))){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_OPTION);
            } else { //Open Gallery
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(galleryIntent, String.valueOf(R.string.add_recipe_alert_dialog_choose_gallery_app)), GALLERY_OPTION);
            }
        });
        alertDialog.setNegativeButton(R.string.add_recipe_alert_dialog_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){

            switch (requestCode){
                case GALLERY_OPTION:
                    Uri miPath = Objects.requireNonNull(data).getData();
                    recipePhoto.setImageURI(miPath);
                    break;
                case CAMERA_OPTION:
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    // convert byte array to Bitmap

                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                            byteArray.length);

                    recipePhoto.setImageBitmap(bitmap);
                    break;
            }
        }
    }
}