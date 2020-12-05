package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import es.android.dacooker.R;
import es.android.dacooker.adapters.AddIngredientRecyclerAdapter;
import es.android.dacooker.adapters.AddRecipePagerAdapter;
import es.android.dacooker.adapters.AddStepRecyclerAdapter;
import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.MealType;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.models.StepModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;

public class AddUpdateRecipeActivity extends AppCompatActivity {

    //Vistas
    private Button btnFinish, btnValidate;
    AddRecipePagerAdapter pagerAdapter;
    private TabLayout tabsLayout;
    private ViewPager viewPager;

    //Tabs - Fragmentos
    private Fragment add_ingredients;
    private Fragment add_recipe;
    private Fragment add_steps;

    //Variables para el modo edición
    List<IngredientModel> ingList;
    List<StepModel> stepList;
    private RecipeModel rEdit;
    boolean forEdit;

    //SingletonMap Keys
    private static final String SHARE_STEPLIST_KEY = "SHARED_STEPLIST_KEY";
    private static final String SHARE_INGLIST_KEY = "SHARED_INGLIST_KEY";
    private static final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_recipe);

        //Si estamos en modo edición
        Bundle b = getIntent().getExtras();
        if(b != null) this.forEdit = b.getBoolean("edit");

        initializeView();
        initializeViewPager();
        initializeTabLayout();
        initButtons();

    }

    /*Métodos de inicialización de vistas*/

    //Inicializar las vistas
    private void initializeView(){
        //Habilitamos marcha atrás en la app bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnFinish = findViewById(R.id.btnFinish_activity);
        btnValidate = findViewById(R.id.btnValidate_activity);
        btnFinish.setVisibility(View.INVISIBLE);
        tabsLayout = findViewById(R.id.add_recipe_tab_layout);
        viewPager = findViewById(R.id.add_recipe_viewPager);

        //Cambiamos el título de la appbar y del textview en caso de estar en modo edición
        if(forEdit) {
            btnFinish.setText(getString(R.string.btnUpdate));
            setTitle(R.string.edit_title);
        }
    }

    //Inicializamos el tablayout
    private void initializeTabLayout(){
        tabsLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //En caso de estemos en el último fragmento del tab, habilitamos el botón para terminar la edicion/adición
                //En caso contrario, deshabilitamos su visibilidad
                if(tabsLayout.getSelectedTabPosition() == 2) btnFinish.setVisibility(View.VISIBLE);
                else btnFinish.setVisibility(View.INVISIBLE);

                //En caso de estar en el primer fragmento del tab, habilitamos el botón de validar la primera parte de la receta
                //En caso contrario, deshabilitamos su visibilidad
                if(tabsLayout.getSelectedTabPosition() == 0) btnValidate.setVisibility(View.VISIBLE);
                else btnValidate.setVisibility(View.INVISIBLE);

                //Asignamos el viewpager a los fragmentos
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //No hacemos uso del método
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //No hacemos uso del método
            }
        });
    }

    //Inicializamos el viewpager
    private void initializeViewPager(){
        //Creamos un nuevo adapter
        pagerAdapter = new AddRecipePagerAdapter(getSupportFragmentManager());

        //Creamos los fragmentos
        add_recipe = new AddRecipeFragment();
        add_ingredients = new AddIngredientFragment();
        add_steps = new AddStepFragment();

        //Añadimos los fragmentos al adapter
        pagerAdapter.addFragment(add_recipe, getString(R.string.add_recipe_fragment_title));
        pagerAdapter.addFragment(add_ingredients, getString(R.string.add_ingredients_fragment_title));
        pagerAdapter.addFragment(add_steps, getString(R.string.add_steps_fragment_title));

        //Añadimos al viewpager el adapter
        viewPager.setAdapter(pagerAdapter);
        //Permitimos la carga de todos los fragmentos
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        //Añadimos al tablayour el viewpager
        tabsLayout.setupWithViewPager(viewPager);
    }

    //Añadimos funcionalidad a los botones
    private void initButtons(){
        btnValidate.setOnClickListener(view -> {
            this.validateRecipeFields();
        });

        btnFinish.setOnClickListener(view -> {
            if(!forEdit) this.finishRecipe();
            else this.updateRecipe();
        });
    }

    /*Métodos para añadir una receta nueva*/

    //Obtener y validar los datos de la primera parte de la receta
    private RecipeModel getRecipeData(){
        //Creamos las variables necesarias para añadir/actualizar la primera parte de la receta
        String name, description, hours, minutes, mtSelection;
        MealType mt = null;
        ImageView image;
        BitmapDrawable bitmapDrawable;
        Bitmap bitmap;
        View v = add_recipe.getView();

        //Obtenemos la información de los campos
        name = ((EditText) v.findViewById(R.id.recipe_name_input)).getText().toString();
        image = v.findViewById(R.id.recipe_img_input);
        bitmapDrawable = (BitmapDrawable) image.getDrawable();
        bitmap = bitmapDrawable.getBitmap();
        description = ((EditText) v.findViewById(R.id.recipe_description_input)).getText().toString();
        hours = ((EditText) v.findViewById(R.id.recipe_hour_input)).getText().toString();
        minutes = ((EditText) v.findViewById(R.id.recipe_minute_input)).getText().toString();
        mtSelection = ((AutoCompleteTextView) v.findViewById(R.id.recipe_mealType_dropdown_select)).getText().toString();

        //Validamos campos especiales como la enumeración y los campos de tiempo
        if(!mtSelection.trim().equals("") && mtSelection != null)
            mt = MealType.valueOf(mtSelection);

        int h = 0, min = 0;
        if(hours == null || hours.equalsIgnoreCase("")) h = -1;
        else h = Integer.parseInt(hours);
        if(minutes == null || minutes.equalsIgnoreCase("")) min = -1;
        else min = Integer.parseInt(minutes);

        //Preparamos la receta
        RecipeModel r = new RecipeModel();
        r.setRecipeName(name);

        //Tratamos la imagen
        Bitmap default_img = ((BitmapDrawable) getDrawable(R.mipmap.img_recipe_card_default)).getBitmap();
        //Si la imagen obtenida de la receta es distinta a la por defecto la añadimos
        //En caso contrario no la añadimos para no ocupar espacio en la base de datos, pues la imagen por defecto se almacena en la carpeta mipmap
        if(!bitmap.equals(default_img)) r.setImage(bitmap);
        else r.setImage(null);
        //Añadimos los datos de la primera parte de la receta
        r.setRecipeDescription(description);
        r.setExecutionTimeHour(h);
        r.setExecutionTimeMinute(min);
        r.setMealType(mt);

        return r;
    }

    //Devuelve la lista de ingredientes asociados a la receta que estamos añadiendo o actualizando
    private List<IngredientModel> getIngredientsData(){
        View add_ing = add_ingredients.getView();
        RecyclerView rw = add_ing.findViewById(R.id.add_ingredient_recycler);
        AddIngredientRecyclerAdapter rwA = (AddIngredientRecyclerAdapter) rw.getAdapter();
        return rwA.getList();

    }

    //Devuelve la lista de pasos asociados a la receta que estamos añadiendo o actualizando
    private List<StepModel> getStepsData(){
        View add_step = add_steps.getView();
        RecyclerView rw = add_step.findViewById(R.id.add_step_recyclerView);
        AddStepRecyclerAdapter rwA = (AddStepRecyclerAdapter) rw.getAdapter();
        return rwA.getStepModelList();
    }

    //Añade la receta
    private void finishRecipe() {
        //Si todos los campos son válidos
        if(this.validateRecipeFields()) {
            try {
                //Obtenemos la receta a añadir
                RecipeModel r = this.getRecipeData();
                //Seteamos las veces que se ha cocinado a 0 y la ponemos como no favorita
                r.setTimesCooked(0);
                r.setFavourite(false);

                BBDD_Helper dbHelper = new BBDD_Helper(this);
                //Añadimos la primera parte de la receta a la BD
                BD_Operations.addRecipe(r, dbHelper);

                //Comprobamos que se ha añadido
                int idRecipe = BD_Operations.getLastID(dbHelper);
                if(idRecipe == -1) throw new Exception();

                //Para cada ingrediente en la lista de ingredientes de la receta a añadir, obtenemos su id y lo añadimos a la base de datos asociándolo a la receta previamente añadida
                for(IngredientModel ing : this.getIngredientsData()){
                    addIngredientsToDB(idRecipe, ing, dbHelper, R.string.err_addition_recipe);
                }

                //Para cada paso en la lista de pasos de la receta a añadir, obtenemos su id y lo añadimos a la base de datos asociándolo a la receta previamente añadida
                for(StepModel s : this.getStepsData()){
                    addStepsToDB(s, idRecipe, dbHelper, R.string.err_addition_recipe);
                }

                //Una vez añadido lo necesario, destruimos la actividad
                finish();
            } catch (Exception e) {
                //Mensaje que se muestra en caso de que ocurra cualquier error
                showException(e, R.string.err_addition_recipe);
            }
        }
    }

    /*Métodos para modificar y actualizar una receta*/

    //Obtener los datos de la receta a modificar
    public void callFromEditFragment(View vRecipe, View vIng, View vStep){
        //Si vamos a editar, preparamos la receta, sus ingrediente y sus pasos para modificación
        if(forEdit) {
            if(vRecipe != null) prepareEditionRecipe(vRecipe);
            if(vIng != null) prepareEditionIngredients(vIng);
            if(vStep != null) prepareEditionSteps(vStep);
        }
    }

    //Preparar la primera parte de la receta
    private void prepareEditionRecipe(View v){
        //Obtenemos la receta mediante SingletonMap
        this.rEdit = (RecipeModel) SingletonMap.getInstance().get(SHARE_RECIPE_KEY);
        //Creamos algunas variables auxiliares
        String description = "";
        Bitmap image;

        //Buscamos el edittext del nombre de la receta y le ponemos el nombre de la receta obtenida
        ((EditText) v.findViewById(R.id.recipe_name_input)).setText(rEdit.getRecipeName());

        //Si hay descripción en la receta la mostramos
        //Si no la hay mostramos cadena vacía
        if (rEdit.getRecipeDescription() != null) description = rEdit.getRecipeDescription();
        ((EditText) v.findViewById(R.id.recipe_description_input)).setText(description);

        //Buscamos los edittexts de horas, minutos y el select del tipo de comida y les ponemos los valores de la receta obtenida
        ((EditText) v.findViewById(R.id.recipe_hour_input)).setText(String.valueOf(rEdit.getExecutionTimeHour()));
        ((EditText) v.findViewById(R.id.recipe_minute_input)).setText(String.valueOf(rEdit.getExecutionTimeMinute()));
        ((AutoCompleteTextView) v.findViewById(R.id.recipe_mealType_dropdown_select)).setText(rEdit.getMealType().name(), false);

        //Si la imagen obtenida de la receta es nula, entonces mostramos la por defecto
        //En caso contrario mostramos la imagen obtenida de la receta
        if (rEdit.getImage() == null) image = ((BitmapDrawable)getDrawable(R.mipmap.img_recipe_card_default)).getBitmap();
        else image = rEdit.getImage();
        ((ImageView) v.findViewById(R.id.recipe_img_input)).setImageBitmap(image);

    }

    //Preparar los ingredientes de la receta a modificar
    private void prepareEditionIngredients(View v){
        //Obtenemos la lista de ingredientes mediante SingletonMap
        this.ingList = (List<IngredientModel>) SingletonMap.getInstance().get(SHARE_INGLIST_KEY);

        //Mostramos los ingredientes de la receta en el recyclerview
        ((AddIngredientRecyclerAdapter) ((RecyclerView) v.findViewById(R.id.add_ingredient_recycler)).getAdapter()).setEditList(this.ingList);
    }

    //Preparar los pasos de la receta a modificar
    private void prepareEditionSteps(View v){
        //Obtenemos la lista de pasos mediante SingletonMap
        this.stepList = (List<StepModel>) SingletonMap.getInstance().get(SHARE_STEPLIST_KEY);

        //Mostramos los pasos de la receta en el recyclerview
        ((AddStepRecyclerAdapter) ((RecyclerView) v.findViewById(R.id.add_step_recyclerView)).getAdapter()).setEditList(this.stepList);
    }

    //Actualizamos los datos de la receta
    private void updateRecipe(){
        //Si la información de los campos obtenida es válida
        if(this.validateRecipeFields()) {
            try {
                //Obtenemos la receta que vamos a modificar
                RecipeModel r = this.getRecipeData();
                //Seteamos su ID al ID de la receta modificada
                r.setId(rEdit.getId());
                //Mantenemos el estado de fav
                r.setFavourite(rEdit.isFavourite());

                //Si la imagen original es la por defecto y al editar añado una imagen o si ambas tienen imagen pero la original es distinta a la que cambio en el editar, entonces cambio la original por la editada
                if((r.getImage() == null && rEdit.getImage() != null) || (r.getImage() != null && rEdit.getImage() != null && !r.getImage().equals(rEdit.getImage()))){
                    Bitmap img = r.getImage();
                    img = Bitmap.createScaledBitmap(img, img.getWidth()*5, img.getHeight()*5, true);
                    r.setImage(img);
                }

                BBDD_Helper dbHelper = new BBDD_Helper(this);
                //Actualizamos la primera parte de la receta
                BD_Operations.updateRecipe(r, dbHelper);

                //Comprobamos que se han realiza las modificaciones
                if(r.getId() == -1) throw new Exception();

                //Eliminamos los ingrediente asociados a la receta
                //Añadimos los nuevos (puede que no se hayan modificado o que solo se hayan eliminado algunos y otros se conserven)
                //Lo hacemos de esta manera para mantener el orden
                BD_Operations.deleteIngredientsFromRecipeId(r.getId(), dbHelper);
                for(IngredientModel ing : this.getIngredientsData()){
                    addIngredientsToDB(r.getId(), ing, dbHelper, R.string.err_update_recipe);
                }

                //Eliminamos los pasos asociados a la receta
                //Añadimos los nuevos (puede que no se hayan modificado o que solo se hayan eliminado algunos y otros se conserven)
                //Lo hacemos de esta manera para mantener el orden
                BD_Operations.deleteStepsFromRecipeId(r.getId(), dbHelper);
                //Creamos una variable para setear el orden de los pasos
                int o = 1;
                for(StepModel s : this.getStepsData()){
                    //Seteamos el orden de los pasos
                    s.setStepOrder(o);
                    addStepsToDB(s, r.getId(), dbHelper, R.string.err_update_recipe);
                    o++;
                }
                //Destruimos la actividad
                finish();
            } catch (Exception e) {
                showException(e, R.string.err_update_recipe);
            }
        }
    }

    /*Métodos auxiiares*/

    //Valida los datos de la primera parte de la receta
    private boolean validateRecipeFields(){
        //Obtenemos la receta
        RecipeModel r = this.getRecipeData();
        String error = "";

        //Validamos campos
        //En caso contrario mostramos error
        if(r.getRecipeName().trim().equalsIgnoreCase("") || r.getRecipeName().length() > 60)
            error = getString(R.string.validation_err_name);
        else if(r.getRecipeDescription().length() > 140)
            error = getString(R.string.validation_err_description);
        else if(r.getExecutionTimeHour() < 0)
            error = getString(R.string.validation_err_hour);
        else if(r.getExecutionTimeMinute() < 0 || r.getExecutionTimeMinute() > 59)
            error = getString(R.string.validation_err_minute);
        else if(r.getMealType() == null) error = getString(R.string.validation_err_mealtype);

        //Si el mensaje de error no es vacio lo mostramos y retornamos false (validación incorrecta)
        //En caso contrario retornamos true (validación correcta)
        if(error.trim().length() != 0) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    //Añade un ingrediente de una receta a la base de datos
    private void addIngredientsToDB(int rID, IngredientModel ing, BBDD_Helper db, int messageID){
        try {
            ing.setIdRecipe(rID);
            BD_Operations.addIngredient(ing, rID, db);
        } catch (Exception e){
            showException(e, messageID);
        }
    }

    //Añade un paso de una receta a la base de datos
    private void addStepsToDB(StepModel step, int rID, BBDD_Helper db, int messageID){
        try {
            step.setRecipe_ID(rID);
            BD_Operations.addStep(step, rID, db);
        } catch (Exception e){
            showException(e, messageID);
        }
    }

    //Muestra excepción
    private void showException(Exception e, int messageID){
        //Si surge cualquier problema mandamos mensaje de error
        Toast.makeText(this, getString(messageID), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }
}