package com.alex.mychat.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AdapterViewPager extends FragmentStateAdapter {

    private ArrayList<Fragment> fragmentos;
    private ArrayList<String> titulos;

    public AdapterViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.fragmentos= new ArrayList<>();
        this.titulos= new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentos.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentos.size();
    }

    public void addFragmento(Fragment fragment, String titulo){
        fragmentos.add(fragment);
        titulos.add(titulo);
    }

    public ArrayList<Fragment> getFragmentos() {
        return fragmentos;
    }

    public ArrayList<String> getTitulos() {
        return titulos;
    }
}
