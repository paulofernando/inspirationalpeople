package br.net.paulofernando.pessoasinspiradoras.view.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.data.entity.Inspiracao;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.SwipeTabFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Inspiracao> inspirations;

    public TabPagerAdapter(FragmentManager fm, List<Inspiracao> inspirations) {
        super(fm);
        this.inspirations = new ArrayList<Inspiracao>(inspirations);
    }

    @Override
    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();

        bundle.putString("inspiration", inspirations.get(index).inspiration);
        bundle.putLong("id", inspirations.get(index).id);
        bundle.putLong("idUser", inspirations.get(index).idUser);

        SwipeTabFragment swipeTabFragment = new SwipeTabFragment();
        swipeTabFragment.setArguments(bundle);
        return swipeTabFragment;
    }

    @Override
    public int getCount() {
        return inspirations.size();
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}