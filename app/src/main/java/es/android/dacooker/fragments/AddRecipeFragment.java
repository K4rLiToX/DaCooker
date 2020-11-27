package es.android.dacooker.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.activities.AddNewRecipeActivity;
import es.android.dacooker.models.MealType;
import es.android.dacooker.models.RecipeModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment {

    MealType[] MEALTYPES = MealType.values();
    ArrayAdapter<MealType> adapter;
    AutoCompleteTextView mealTypeDropdown;

    EditText recipeName, recipeHours, recipeMinutes, recipeDescription;
    ImageView recipePhoto;
    Button btnToIngredients;

    final String CARPETA_RAIZ = "misImagenesPrueba/";
    final String RUTA_IMAGEN = CARPETA_RAIZ + "misFotos";
    String path = "";




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
        btnToIngredients = v.findViewById(R.id.btnToAddIngredient);

        FloatingActionButton fabTakePhoto = v.findViewById(R.id.fabTakeRecipePhoto);
        fabTakePhoto.setOnClickListener(photo ->{
            takeRecipePhoto();
        });

        FloatingActionButton fabSelectPhoto = v.findViewById(R.id.fabSelectRecipePhoto);
        fabSelectPhoto.setOnClickListener(gallery ->{
            selectRecipePhoto();
        });

        btnToIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToIngredients();
            }
        });

        return v;
    }

    private void takeRecipePhoto() {
        File fileImage = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImage.exists();
        String imageName="";

        if(!isCreada){
            isCreada = fileImage.mkdirs();
        }

        if(isCreada){
            imageName = (System.currentTimeMillis()/100) + ".jpg";
        }

        path = Environment.getExternalStorageDirectory() +
                File.separator + RUTA_IMAGEN + File.separator + imageName; //Ruta de almacenamiento
        File image = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Lanza la app de camara
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image)); //Enviar la imgen tomada y almacenarla
        startActivityForResult(intent, 20);
    }

    private void selectRecipePhoto(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Application"), 10);    //PICK_IMAGE
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){

            switch (requestCode){
                case 10:
                    Uri miPath = Objects.requireNonNull(data).getData();
                    recipePhoto.setImageURI(miPath);
                    break;
                case 20:
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    recipePhoto.setImageBitmap(bitmap);
                    break;
            }

        }
    }

    private void goToIngredients(){
        boolean validate = validateFields();

        if(validate){
            //Create New Recipe
            RecipeModel recipeCreated = new RecipeModel();
            //Treat the Image
            BitmapDrawable bitmapDrawable = (BitmapDrawable) recipePhoto.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            recipeCreated.setImage(bitmap);
            //Name
            recipeCreated.setRecipeName(recipeName.getText().toString());
            //Execution Time
            String minutes = "00";
            if(!recipeMinutes.getText().toString().isEmpty()) minutes = recipeMinutes.getText().toString();
            recipeCreated.setExecutionTime(recipeHours.getText().toString() + ":" + minutes);
            //Mealtype
            MealType mealType;
            String mealTypeS = mealTypeDropdown.getEditableText().toString();

            if(mealTypeS.toLowerCase().equals("launch")) mealType = MealType.LAUNCH;
            else if(mealTypeS.toLowerCase().equals("dinner")) mealType = MealType.DINNER;
            else mealType = MealType.OTHER;
            recipeCreated.setMealType(mealType);
            //Description
            recipeCreated.setRecipeDescription(recipeDescription.getText().toString());

            //Create a Bundle to Store the Recipe
            Bundle bundleRecipe = new Bundle();
            bundleRecipe.putSerializable("recipeCreated", recipeCreated);

            //Go to the AddIngredientFragment
            AddIngredientFragment ingredientFragment = new AddIngredientFragment();
            ingredientFragment.setArguments(bundleRecipe);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, ingredientFragment).commit();
        } else {
            Toast.makeText(getContext(), recipeName.getText().toString() + ", " + recipeHours.getText().toString() + ", " + recipeMinutes.getText().toString() + ", " + mealTypeDropdown.getEditableText().toString() + ", " + recipeDescription.getText().toString(), Toast.LENGTH_LONG).show();
            //Toast.makeText(getContext(), "Some Fields are Empty", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateFields(){
        boolean recipeNameValidator = nameValidator();
        boolean recipeHourValidator = hourValidator();
        boolean recipeMinuteValidator = minuteValidator();
        boolean recipeMealTypeValidator = mealTypeValidator();

        return recipeNameValidator && recipeHourValidator && recipeMinuteValidator && recipeMealTypeValidator;
    }

    private boolean nameValidator(){
        return !recipeName.getText().toString().isEmpty();
    }

    private boolean hourValidator(){
        boolean horas;
        if(!recipeHours.getText().toString().isEmpty()) {
            horas = Integer.parseInt(recipeHours.getText().toString()) >= 0;
        } else {
            horas = false;
        }

        return horas;
    }

    private boolean minuteValidator(){
        boolean minutes;
        if(!recipeMinutes.getText().toString().isEmpty()){
            minutes = Integer.parseInt(recipeMinutes.getText().toString()) >= 0;
        } else {
            minutes = true;
        }
        return minutes;
    }

    private boolean mealTypeValidator(){
        return !mealTypeDropdown.getEditableText().toString().isEmpty();
    }
}