package es.android.dacooker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.adapters.RecyclerViewAdapter;
import es.android.dacooker.dialogManager.CustomDialog;
import es.android.dacooker.fragments.CustomFragment;
import es.android.dacooker.fragments.MostUsedFragment;
import es.android.dacooker.fragments.RecipeFragment;
import es.android.dacooker.interfaces.RecipeClickListener;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.NotificationsPush;
import es.android.dacooker.utilities.SingletonMap;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CustomDialog.OnDialogInputListener {

    //Main Fragments
    private final RecipeFragment recipeFragment = new RecipeFragment();
    private final MostUsedFragment mostUsedFragment = new MostUsedFragment();
    private final CustomFragment customFragment = new CustomFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_recipes);
        bottomNavigationView.getMenu().getItem(1).setCheckable(true).setChecked(true);

        initNotifications();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.main_menu_search){
            //Open Dialog for Filter Searching Recipes
            showFilterSearchAlertDialog();
            return true;
        } else if(itemID == R.id.main_menu_settings) {
            //Start Setting Activity
            return true;
        } else if(itemID == R.id.main_menu_undo_search) {
            undoFilters();
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
            return true;
        } else if(itemID == R.id.menu_most_used){
            changeFragment(mostUsedFragment, R.string.most_used_recipes_label);
            return true;
        } else if(itemID == R.id.menu_custom) {
            changeFragment(customFragment, R.string.custom_recipes_label);
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

    private void undoFilters(){
        sendResultList(null);
        recipeFragment.onResume();
    }

    @Override
    public void sendResultList(List<RecipeModel> resultList) {
        SingletonMap.getInstance().put("SHARE_RESULT_LIST_KEY", resultList);
        recipeFragment.onResume();
    }
}