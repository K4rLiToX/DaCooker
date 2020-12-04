package es.android.dacooker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.dialogManager.CustomDialog;
import es.android.dacooker.fragments.FavouritesFragment;
import es.android.dacooker.fragments.MostUsedFragment;
import es.android.dacooker.fragments.RecipeFragment;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.utilities.NotificationsPush;
import es.android.dacooker.utilities.SingletonMap;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CustomDialog.OnDialogInputListener {

    //Singleton Keys
    private static final String SHARE_FILTER_SEARCH_KEY = "SHARE_FILTER_SEARCH";
    private static final String SHARE_RESULT_LIST_KEY = "SHARE_RESULT_LIST_KEY";

    //Fragmentos principales
    private final FavouritesFragment favouritesFragment = new FavouritesFragment();
    private final MostUsedFragment mostUsedFragment = new MostUsedFragment();
    private final RecipeFragment recipeFragment = new RecipeFragment();

    //Icono de búsqueda
    MenuItem searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creamos la toolbar personalizada
        Toolbar toolbar = findViewById(R.id.main_activity_app_bar);
        //La añadimos como principal
        setSupportActionBar(toolbar);

        //Incializamos el navegador inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //Seleccionamos que el fragmento inicial sea el de recetas
        bottomNavigationView.setSelectedItemId(R.id.menu_recipes);
        bottomNavigationView.getMenu().getItem(1).setCheckable(true).setChecked(true);

        //Inicializamos el servicio de notificaciones
        initNotifications();
    }

    @Override
    //Método para enviar los resultados de la búsqueda por filtros realizada en el dialogo personalizado al fragmento de recetas
    public void sendResultList(List<RecipeModel> resultList, String filter, String search) {
        SingletonMap.getInstance().put(SHARE_RESULT_LIST_KEY, resultList);
        SingletonMap.getInstance().put(SHARE_FILTER_SEARCH_KEY, new String[]{filter, search});
        //Ejecutamos el onResume del fragmento de recetas para actualizar el recyclerview
        recipeFragment.onResume();
    }

    /*Métodos de la toolbar*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflamos el menu de la toolbar
        getMenuInflater().inflate(R.menu.menu_main_recipes, menu);
        //Asignamos el icono de búsqueda
        searchIcon = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        //Si el id del icono coincide con el id del icono de búsqueda
        if(itemID == R.id.main_menu_search){
            //Mostramos dialog
            showFilterSearchAlertDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //Crea un dialogo personalizado y lo muestra
    private void showFilterSearchAlertDialog(){
        CustomDialog dialog = new CustomDialog();
        dialog.show(getSupportFragmentManager(), "CustomDialog");
    }

    /*Metodos de la navegación inferior*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if(itemID == R.id.menu_recipes){ //Si el id del icono coincide con el id de icono de recetas
            //Cambiamos el fragmento por el de recetas
            changeFragment(recipeFragment, R.string.recipes_label);
            //Si el icono de búsqueda de la appbar es distinto de nulo y no está visible, entonces lo ponemos visible
            if(searchIcon != null && !searchIcon.isVisible()) searchIcon.setVisible(true);
            return true;
        } else if(itemID == R.id.menu_most_used){ //Si el id del icono coincide con el id de icono de más cocinadas
            //Cambiamos el fragmento por el de más cocinadas
            changeFragment(mostUsedFragment, R.string.most_used_recipes_label);
            //Ocultamos el icono de búsqueda la appbar
            searchIcon.setVisible(false);
            return true;
        } else if(itemID == R.id.menu_favourites) { //Si el id del icono coincide con el id de icono de favoritas
            //Cambiamos el fragmento por el de favoritas
            changeFragment(favouritesFragment, R.string.favourites_recipes_label);
            //Ocultamos el icono de búsqueda la appbar
            searchIcon.setVisible(false);
            return true;
        } else {
            return false;
        }
    }

    //Cambia de fragmento y cambia el título de la appbar según el fragmento que se muestra
    private void changeFragment(Fragment fragmentToChange, int title){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentToChange).commit();
        setTitle(title);
    }


    //Crea un canal para las notificaciones
    public void initNotifications(){
        NotificationsPush.createNotifyChannel(this, getString(R.string.notification_channel_name),
                getString(R.string.notification_channel_description));
    }
}