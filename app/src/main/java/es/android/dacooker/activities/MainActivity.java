package es.android.dacooker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

    //Main Fragments
    private final RecipeFragment recipeFragment = new RecipeFragment();
    private final MostUsedFragment mostUsedFragment = new MostUsedFragment();
    private final FavouritesFragment favouritesFragment = new FavouritesFragment();

    MenuItem searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_activity_app_bar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_recipes);
        bottomNavigationView.getMenu().getItem(1).setCheckable(true).setChecked(true);

        initNotifications();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_recipes, menu);
        searchIcon = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.main_menu_search){
            showFilterSearchAlertDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if(itemID == R.id.menu_recipes){
            changeFragment(recipeFragment, R.string.recipes_label);
            if(searchIcon != null && !searchIcon.isVisible()) searchIcon.setVisible(true);
            return true;
        } else if(itemID == R.id.menu_most_used){
            changeFragment(mostUsedFragment, R.string.most_used_recipes_label);
            searchIcon.setVisible(false);
            return true;
        } else if(itemID == R.id.menu_favourites) {
            changeFragment(favouritesFragment, R.string.favourites_recipes_label);
            searchIcon.setVisible(false);
            return true;

        } else {
            return false;
        }
    }

    private void changeFragment(Fragment fragmentToChange, int title){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentToChange).commit();
        setTitle(title);
    }

    public void initNotifications(){
        NotificationsPush.createNotifyChannel(this, getString(R.string.notification_channel_name),
                getString(R.string.notification_channel_description));
    }

    private void showFilterSearchAlertDialog(){
        CustomDialog dialog = new CustomDialog();
        dialog.show(getSupportFragmentManager(), "CustomDialog");
    }

    @Override
    public void sendResultList(List<RecipeModel> resultList, String filter, String search) {
        SingletonMap.getInstance().put("SHARE_RESULT_LIST_KEY", resultList);
        SingletonMap.getInstance().put("SHARE_FILTER_SEARCH", new String[]{filter, search});
        recipeFragment.onResume();
    }
}