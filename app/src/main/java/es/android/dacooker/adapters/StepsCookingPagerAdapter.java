package es.android.dacooker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class StepsCookingPagerAdapter extends FragmentStatePagerAdapter {
    //Listas de fragmentos a mostrar
    List<Fragment> fragmentList = new ArrayList<>();

    //Constructor
    public StepsCookingPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    //Añade un fragmento pasado por parámetro a la lista de fragmentos
    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return this.fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return this.fragmentList.size();
    }
}
