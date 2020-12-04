package es.android.dacooker.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.fragments.AddIngredientFragment;
import es.android.dacooker.fragments.AddRecipeFragment;
import es.android.dacooker.fragments.AddStepFragment;

public class AddRecipePagerAdapter extends FragmentPagerAdapter {

    ArrayList<String> titleList = new ArrayList<>();
    List<Fragment> fragmentList = new ArrayList<>();

    public AddRecipePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){
        titleList.add(title);
        fragmentList.add(fragment);
    }

    //Utilities
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

}
