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

    /*Constantes*/

    //Variables para el código de permiso y la opción de galería utilizados para abrir la galería del dispositivo y escoger una imagen para la receta
    private final static int PERMISSION_CODE = 1000;
    private final int GALLERY_OPTION = 10;

    //Adaptador de la enumeración Mealtype
    MealType[] MEALTYPES = MealType.values();
    ArrayAdapter<MealType> adapter;

    //Vistas
    TextInputEditText recipeName, recipeHours, recipeMinutes, recipeDescription;
    AutoCompleteTextView mealTypeDropdown;
    FloatingActionButton fabChooseRecipePhoto;
    ImageView recipePhoto;

    //Constructor
    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Infla la vista para el fragmento de añadir receta (primera parte de la receta)
        View v = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        initView(v);
        initButton();

        return v;
    }

    //Inicializa las vistas
    private void initView(View v){
        //Crea el adaptador para el select del tipo de comioda
        adapter =  new ArrayAdapter<>(getActivity(), R.layout.meal_type_dropdown_item, MEALTYPES);
        mealTypeDropdown = v.findViewById(R.id.recipe_mealType_dropdown_select);
        //Setea el adaptador al select del tipo de comida
        mealTypeDropdown.setAdapter(adapter);

        recipePhoto = v.findViewById(R.id.recipe_img_input);
        recipeName = v.findViewById(R.id.recipe_name_input);
        recipeHours = v.findViewById(R.id.recipe_hour_input);
        recipeMinutes = v.findViewById(R.id.recipe_minute_input);
        recipeDescription = v.findViewById(R.id.recipe_description_input);
        fabChooseRecipePhoto = v.findViewById(R.id.fabChooseRecipePhoto);

        ((AddUpdateRecipeActivity)getActivity()).callFromEditFragment(v, null, null);
    }

    //Inicializa el floating action button y le añade funcionalidad
    private void initButton(){
        fabChooseRecipePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            //Cuando se pulsa abre la galería del dispositivo
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    @Override
    //Método que verifica si se tienen los permisos necesarios para abrir la galería del dispositivo
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) { //Si el código de permiso que me dan son coinciden con el que pido
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Si obtengo los permisos
                //abro galería
                openGallery();
            } else {  //Si no obtengo los permisos o me los deniegan notifico con mensaje
                Toast.makeText(getActivity(), "Permission Denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Método para abrir la galería del dispositivo
    private void openGallery(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //Si aún no tengo permisos los pido
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        } else { //Si me dan los permisos o ya los tengo abro muestro dialog para que el usuario eliga qué aplicación de galería quiere utilizar
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(galleryIntent, getActivity().getString(R.string.add_recipe_alert_dialog_choose_gallery_app)), GALLERY_OPTION);
        }
    }

    @Override
    //Método que se ejecuta una vez se ha elegido una foto desde la galería del dispositivo
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == this.GALLERY_OPTION) { //Si tengo todos los permisos y el código corresponde con la opción de galería
            //Guardo la URI de la imagen seleccionada
            Uri miPath = Objects.requireNonNull(data).getData();
            //Seteo la foto de la receta a la imagen que se ha seleccionado desde galería
            recipePhoto.setImageURI(miPath);
        }
    }

}