package es.android.dacooker.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;

public class AddRecipePagerAdapter extends FragmentStatePagerAdapter {

    //private ArrayList<String> arrayList = new ArrayList<>();
    //private List<Fragment> fragmentList = new ArrayList<>();

    public AddRecipePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    /* Implementar Si al cambiar fragment se pierden datos
    public void addFragment(Fragment fragment, String title){
        arrayList.add(title);
        fragmentList.add(fragment);
    }
     */

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0: return new AddRecipeFragment();
            case 1: return new AddIngredientFragment();
            case 2: return new AddStepFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0: return "Recipe";
            case 1: return "Ingredients";
            case 2: return "Steps";
        }
        return null;
    }

}
