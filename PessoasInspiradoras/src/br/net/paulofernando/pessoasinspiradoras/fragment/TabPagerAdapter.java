package br.net.paulofernando.pessoasinspiradoras.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;

public class TabPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<InspiracaoEntity> inspirations;
	
	/** 
	 * @param tabHeight height of the area where the inspiration text appears
	 */
	public TabPagerAdapter(FragmentManager fm, List<InspiracaoEntity> inspirations) {
        super(fm);
        this.inspirations = new ArrayList<InspiracaoEntity>(inspirations);
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
}