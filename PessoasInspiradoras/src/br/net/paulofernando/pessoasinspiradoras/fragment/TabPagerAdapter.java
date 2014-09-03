package br.net.paulofernando.pessoasinspiradoras.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();
        String tab = String.valueOf(index);
        
        //bundle.putString("tab",tab);
        SwipeTabFragment swipeTabFragment = new SwipeTabFragment();
        swipeTabFragment.setArguments(bundle);
        return swipeTabFragment;
    }
    
    @Override
    public CharSequence getPageTitle (int position) {
        return "Your static title";
    }

    @Override
    public int getCount() {
        return 3;
    }
}