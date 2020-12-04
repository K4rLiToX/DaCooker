package es.android.dacooker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.adapters.IngredientRecyclerAdapter;
import es.android.dacooker.exceptions.IngredientException;
import es.android.dacooker.exceptions.StepException;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.models.StepModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;


public class RecipeDetails extends AppCompatActivity {

    //SingletonMap Key
    private static final String SHARE_DELETED_RECIPE = "SHARE_RECETA_ELIMINADA";
    private static final String SHARE_STEPLIST_KEY = "SHARED_STEPLIST_KEY";
    private static final String SHARE_INGLIST_KEY = "SHARED_INGLIST_KEY";
    private static final String SHARE_RECIPE_KEY = "SHARED_RECIPE_KEY";
    private static final String SHARE_FAV_KEY = "SHARE_FAV_KEY";

    //Vistas
    private TextView tvRecipeTitleDetail, tvRecipeTimeDetail, tvRecipeMealType, tvRecipeDescription;
    private ImageView imgRecipeDetail, imgIngredientRecyclerViewIcon;
    private RecyclerView ingredientRecyclerView;
    LinearLayout expandableLayout;
    Button btnStartRecipe;

    //Variables auxiliares
    private boolean isExpanded, isFav, isDeleted;
    IngredientRecyclerAdapter ingredientAdapter;
    MenuItem favIcon;

    //Receta a mostrar
    private RecipeModel recipeSelected;

    //Ingredientes y pasos asociados a la receta a mostrar
    private List<IngredientModel> ingredientList;
    private List<StepModel> stepList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        //Creamos la toolbar personalizada y la ponemos como principal
        Toolbar detail_toolbar = findViewById(R.id.recipe_detail_app_bar);
        setSupportActionBar(detail_toolbar);

        //Habilitamos el icono de vuelta atrás
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        initViews();
        initParameters();
        setViews();
        initIngredientRecyclerView();
    }

    /*Métodos de la toolbar*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflamos el menú en la toolbar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recipe_detail_menu_app_bar, menu);
        //Asigamos el icono de fav
        favIcon = menu.findItem(R.id.recipe_detail_menu_app_bar_fav);
        //Actualizamos el icono
        updateFavIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.recipe_detail_menu_app_bar_delete){ //Si pulsamos el icono de eliminar
            //Eliminamos la receta
            deleteRecipe();
            return true;
        } else if(itemID == R.id.recipe_detail_menu_app_bar_edit){ //Si pulsamos el icono de editar
            //Cambiamos la interfaz a modo edición
            editRecipe();
            return true;
        } else if(itemID == R.id.recipe_detail_menu_app_bar_fav) { //Si pulsamos el icono de fav
            //Cambiamos el estado según el que tenga actualmente
            fav();
            return true;
        } else if(itemID == android.R.id.home){ //Si pulsamos volver atrás
            //Volvemos a la actividad anterior
            this.onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //Vuelta atrás
    @Override
    public void onBackPressed() {
        //Realizamos este método para evitar llamadas continuas a la base de datos cambiando el estado de fav de la receta
        //Solo hacemos la llamada a la base de datos cuando vamoa a eliminar o cuando volvemos a la actividad anterior. De ahi la razón de tener la variable isFav e isDeleted
        super.onBackPressed();
        //Si los estados de isFav y favorita de la receta son distintos y no se va a eliminar la receta
        if(isFav != recipeSelected.isFavourite() && !isDeleted){
            //Paso por SingletonMap una cadena de texto cualquiera (utilizamos la misma que en el método de eliminación)
            SingletonMap.getInstance().put(SHARE_FAV_KEY, "true");
            BBDD_Helper db = new BBDD_Helper(RecipeDetails.this);
            try {
                //Actualizo fav en la base de datos y destruyo la actividad
                BD_Operations.updateFavourite(recipeSelected.getId(), isFav, db);
                finish();
            } catch (Exception e){
                //En caso de error muestro mensaje notificando
                Toast.makeText(RecipeDetails.this, R.string.recipe_detail_deleted_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*Métodos de inicialización de vistas*/
    private void initViews(){
        //Obtenemos todas las vistas de la intefaz
        this.imgRecipeDetail = findViewById(R.id.img_detail_recipe);
        this.tvRecipeTitleDetail = findViewById(R.id.recipe_detail_title);
        this.tvRecipeTimeDetail = findViewById(R.id.recipe_detail_time);
        this.tvRecipeMealType = findViewById(R.id.recipe_detail_mealType);
        this.tvRecipeDescription = findViewById(R.id.recipe_detail_description);
        this.ingredientRecyclerView = findViewById(R.id.recipe_detail_ingredient_recyclerView);
        this.btnStartRecipe = findViewById(R.id.btn_start_recipe);
        this.expandableLayout = findViewById(R.id.expandableLayout);
        this.imgIngredientRecyclerViewIcon = findViewById(R.id.recipe_detail_ingredient_recyclerView_icon);

        //Añadimos un click listener a la card de ingredientes
        this.expandableLayout.setOnClickListener(view -> {
            //Si la lista de ingredientes no está vacía
            if(!this.ingredientList.isEmpty()){
                //Hacemos visible el layout si no está expandido, lo ocultamos si lo está
                ingredientRecyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                //Si está expandido cambiamos el icono de la flecha hacia arriba
                //En caso contrario por flecha hacia abajo
                if(isExpanded) imgIngredientRecyclerViewIcon.setImageResource(R.drawable.ic_down_arrow);
                else imgIngredientRecyclerViewIcon.setImageResource(R.drawable.ic_up_arrow);
                //Cambiamos el estado de expandido
                isExpanded = !isExpanded;
            } else {
                //Si ocurre algún error mostramos mensaje
                Toast.makeText(getApplicationContext(), R.string.recipe_detail_ingredientList_empty, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initParameters(){
        BBDD_Helper db = new BBDD_Helper(getApplicationContext());

        //Obtenemos la receta que se ha seleccionado en la actividad anterior mediante el SingletonMap
        this.recipeSelected = (RecipeModel) SingletonMap.getInstance().get(SHARE_RECIPE_KEY);
        //Actualizamos el icono de fav según el estado de fav de la receta
        this.isFav = recipeSelected.isFavourite();
        //Variable auxiliar que nos ayudará a optimizar código si queremos eliminar una receta y hemos pulsado el boton de fav
        this.isDeleted = false;

        //Obtenemos la lista de pasos de la receta asociada
        //En caso de error inicializamos la lista de pasos a vacía
        try { this.stepList = BD_Operations.getStepsFromRecipeIdOrdered(recipeSelected.getId(), db);
        } catch (StepException e) { this.stepList = new ArrayList<>(); }

        //Obtenemos la lista de ingredientes de la receta asociada
        //En caso de error inicializamos la lista de ingredientes a vacía
        try { this.ingredientList = BD_Operations.getIngredientsByIdRecipe(recipeSelected.getId(), db);
        } catch (IngredientException e){ this.ingredientList = new ArrayList<>(); }

        //Iniciamos con la card de ingredientes sin expandir
        isExpanded = false;
        //Ponemos la visiblidad de la lista de ingredientes a no visible
        this.ingredientRecyclerView.setVisibility(View.GONE);
    }

    //Seteamos las vistas con los valores correspondientes de la receta
    private void setViews(){
        //Si la imagen que obtenemos de la receta no es nula la mostramos
        //En caso contrario, mostramos la por defeceto
        if(this.recipeSelected.getImage() != null) this.imgRecipeDetail.setImageBitmap(this.recipeSelected.getImage());
        else this.imgRecipeDetail.setImageResource(R.mipmap.img_recipe_card_default);

        //Mostramos el nombre, el tiempo, el tipo de comida y la descripción (si la hubiese, en caso contrario se mostraria cadena vacía)
        this.tvRecipeTitleDetail.setText(this.recipeSelected.getRecipeName());
        this.tvRecipeTimeDetail.setText(this.recipeSelected.getExecutionTimeHour() + getString(R.string.hours) + " " + this.recipeSelected.getExecutionTimeMinute() + getString(R.string.minutes));
        this.tvRecipeMealType.setText(String.valueOf(this.recipeSelected.getMealType()));
        this.tvRecipeDescription.setText(this.recipeSelected.getRecipeDescription());

        //Funcionalidad del botón empezar receta
        this.btnStartRecipe.setOnClickListener(view -> {
            //Si la lista de pasos asociada a la receta es vacía o nula mostramos error
            if(this.stepList == null || this.stepList.isEmpty())
                Toast.makeText(this, getString(R.string.recipe_detail_nosteps_error), Toast.LENGTH_SHORT).show();
            else { //En caso contrario
                //Ponemos la lista de pasos en el SingletonMap
                SingletonMap.getInstance().put(SHARE_STEPLIST_KEY, stepList);
                //Empezamos la actividad empezar receta
                Intent i = new Intent(RecipeDetails.this, StepsRecipeCooking.class);
                startActivity(i);
            }
        });
    }

    //Inicializar el recyclerview de los ingredientes
    private void initIngredientRecyclerView(){
        //Creamos el adapter con la lista de ingredientes asociados a la receta
        this.ingredientAdapter = new IngredientRecyclerAdapter(this.ingredientList);
        //Se lo asignamos al recyclerview
        ingredientRecyclerView.setAdapter(ingredientAdapter);
    }

    /*Métodos de la Toolbar*/

    //Elimina la receta
    private void deleteRecipe(){
        //Construyo un alertdialog con título y mensaje
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RecipeDetails.this);
        alertBuilder.setTitle(R.string.recipe_fragment_alert_dialog_title);
        alertBuilder.setMessage(R.string.recipe_fragment_alert_dialog_message);
        //Añado la funcionalidad del botón confirmar
        alertBuilder.setPositiveButton(R.string.recipe_fragment_alert_dialog_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //Si es una recta favorita mando error y cierro el dialog
                    if(isFav){
                        Toast.makeText(RecipeDetails.this, R.string.recipe_detail_delete_fav_error, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    } else { //En caso contrario
                        BBDD_Helper db = new BBDD_Helper(RecipeDetails.this);
                        //Borro la receta
                        BD_Operations.deleteRecipe(recipeSelected.getId(), db);
                        //Cambios la variable auxiliar a true
                        isDeleted = true;
                        //Paso un string cualquiera por SingletonMap
                        SingletonMap.getInstance().put(SHARE_DELETED_RECIPE, "true");
                        //Muestro mensaje de eliminación con éxito y destruyo la actividad volviendo a la anterior
                        Toast.makeText(RecipeDetails.this, R.string.recipe_detail_deleted, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e){
                    //En caso de cualquier error muestro mensaje y cierro el dialogo
                    Toast.makeText(RecipeDetails.this, R.string.recipe_detail_deleted_error, Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        //Añado funcionalidad al botón cancelar
        alertBuilder.setNegativeButton(R.string.recipe_fragment_alert_dialog_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Cierror el dialog
                dialogInterface.dismiss();
            }
        });
        //Construyo y muestro el dialog
        alertBuilder.create();
        alertBuilder.show();
    }

    //Editar receta
    private void editRecipe(){
        //Destruyo la actividad en la que estoy
        finish();
        //Paso por el SingletonMap la receta que voy a editar junto con sus listas de ingredientes y pasos asociadas
        SingletonMap.getInstance().put(SHARE_RECIPE_KEY, recipeSelected);
        SingletonMap.getInstance().put(SHARE_INGLIST_KEY, ingredientList);
        SingletonMap.getInstance().put(SHARE_STEPLIST_KEY, stepList);
        //Empiezo la actividad
        Intent i = new Intent(RecipeDetails.this, AddUpdateRecipeActivity.class);
        //Aseguro que es para editar
        i.putExtra("edit", true);
        startActivity(i);
    }

    //Actualizar el estado del icon fav al pulsar según su estado actual
    private void fav(){
        isFav = !isFav;
        updateFavIcon();
    }

    //Actualiza el icono de fav al ser pulsado
    private void updateFavIcon(){
        //Si esta en estado favorita, ponemos el icono relleno
        //En caso contrario, ponemos el icono sin relleno y con borde
        if(isFav) favIcon.setIcon(R.drawable.ic_favourite) .setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        else favIcon.setIcon(R.drawable.ic_no_favourite);
    }
}