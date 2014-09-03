package br.net.paulofernando.pessoasinspiradoras.fragment;

import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.image.ImageFromSentence;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.view.InspirationView_;

import com.viewpagerindicator.CirclePageIndicator;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PagerInspirations extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private TextView personName;
    private ImageView medal, photo;
    private TabPagerAdapter tabPagerAdapter;

    long personId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_inspirations);
              
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager_inspirations);
        viewPager.setAdapter(tabPagerAdapter);

        personName = (TextView) findViewById(R.id.person_name_detail_pager);
        medal = (ImageView) findViewById(R.id.medal_selected_person_pager);
        photo = (ImageView) findViewById(R.id.photo_selected_person_pager);
        
        CirclePageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);

        personId = getIntent().getLongExtra("id", -1);
		
		try {
			personName.setText(getIntent().getStringExtra("name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//loadInspirations();
		updateData();
    }

    public void loadInspirations() {
		//layoutInspirations.removeAllViews();
    	DatabaseHelper helper = new DatabaseHelper(this);    	
    	
    	List<InspiracaoEntity> list = helper.getInspirationData(personId);
    	/*for(InspiracaoEntity inspiration: list) {
    		layoutInspirations.addView(InspirationView_.build(inspiration, this));
    	}*/
    	
    	updateMedal(list.size());    	
    	helper.close();
	}
    
    private void updateMedal(int amountInspirations) {
		if(amountInspirations >= 9) {
			medal.setImageDrawable(getResources().getDrawable(R.drawable.nine_plus_white));
			medal.setVisibility(View.VISIBLE);
		} else if(amountInspirations >= 6) {
			medal.setImageDrawable(getResources().getDrawable(R.drawable.six_plus_white));
			medal.setVisibility(View.VISIBLE);
		} else if(amountInspirations >= 3) {
			medal.setImageDrawable(getResources().getDrawable(R.drawable.three_plus_white));
			medal.setVisibility(View.VISIBLE);
		} else {
			medal.setVisibility(View.INVISIBLE);
		}
	}
    
    private void updateData() {
		DatabaseHelper helper = new DatabaseHelper(this);
		PersonEntity person =  helper.getPerson(personId);
		personName.setText(person.name);		
		photo.setImageBitmap(new ImageFromSentence(this).getCroppedBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length)));
		loadInspirations();
		helper.close();
	}
    
    @Override
	protected void onResume() {
		updateData();
		super.onResume();
	}
    
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) { }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
}