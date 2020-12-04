package es.android.dacooker.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddRecipePagerAdapter extends FragmentPagerAdapter {

    //Listas de títulos y de fragmentos a mostrar
    ArrayList<String> titleList = new ArrayList<>();
    List<Fragment> fragmentList = new ArrayList<>();

    //Constructor
    public AddRecipePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    //Añade un fragmento con un título
    public void addFragment(Fragment fragment, String title){
        titleList.add(title);
        fragmentList.add(fragment);
    }

    /*Métodos auxiliares necesarios*/
    @NonNull
    @Override
    //Devuelve el fragmento dependiendo de la posición
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    //Devuelve el tamaño de la lista de fragmentos
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    //Devuelve el título del fragmento dependiendo de la posición
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

}
