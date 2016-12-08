package br.net.paulofernando.pessoasinspiradoras.view.fragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Inspiracao;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.view.activity.AddInspirationActivity;
import br.net.paulofernando.pessoasinspiradoras.view.activity.EditPersonActivity;
import br.net.paulofernando.pessoasinspiradoras.view.activity.PopupImageActivity;
import br.net.paulofernando.pessoasinspiradoras.view.adapter.TabPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.resource;

public class PagerInspirationsFragment extends FragmentActivity implements
        ActionBar.TabListener {

    
    long personId;
    private ViewPager viewPager;
    @BindView(R.id.person_name_detail_pager) TextView personName;
    @BindView(R.id.medal_selected_person_pager) ImageView medal;
    @BindView(R.id.photo_selected_person_pager) ImageView photo;
    @BindView(R.id.back_person_pager) ImageView btnBack;
    @BindView(R.id.edit_person_pager) ImageView btnEditPerson;

    private TabPagerAdapter tabPagerAdapter;
    private Person person;
    private List<Inspiracao> listInspirations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_inspirations);
        ButterKnife.bind(this);

        person = getIntent().getParcelableExtra(getResources().getString(R.string.person_details));
        personName.setText(person.name);
        personId = person.id;

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlurBehind.getInstance().execute(PagerInspirationsFragment.this, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = PopupImageActivity.getStartIntent(PagerInspirationsFragment.this, person);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerInspirationsFragment.this.finish();
            }
        });

        btnEditPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPersonData();
            }
        });

        personName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPersonData();
            }
        });

        loadInspirations();

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),
                listInspirations);

        viewPager = (ViewPager) findViewById(R.id.pager_inspirations);
        viewPager.setAdapter(tabPagerAdapter);

        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);

        FloatingActionButton myFab = (FloatingActionButton)  findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addInspiration();
            }
        });

    }

    public static Intent getStartIntent(Context context, Person person, ImageView imageView) {
        Intent intent = new Intent(context, PagerInspirationsFragment.class);

        imageView.buildDrawingCache();
        Bitmap image = imageView.getDrawingCache();

        Bundle extras = new Bundle();
        extras.putParcelable(context.getResources().getString(R.string.person_photo), image);
        extras.putParcelable(context.getResources().getString(R.string.person_details), person);
        intent.putExtras(extras);

        return intent;
    }

    private void editPersonData() {
        Intent intent = EditPersonActivity.getStartIntent(this, person);
        /*String transitionName = context.getString(R.string.cover_name);
        ActivityOptions transitionActivityOptions = ActivityOptions.
                makeSceneTransitionAnimation((Activity) context, binding.coverIv, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());*/
        startActivity(intent);
    }

    public void loadInspirations() {
        DatabaseHelper helper = new DatabaseHelper(this);
        listInspirations = helper.getInspirationData(personId);
        helper.close();
    }

    private void updateMedal(int amountInspirations) {
        if (amountInspirations >= 9) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                medal.setImageDrawable(getResources().getDrawable(R.drawable.nine_plus_white));
            } else {
                medal.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.nine_plus_white, null));
            }
            medal.setVisibility(View.VISIBLE);
        } else if (amountInspirations >= 6) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                medal.setImageDrawable(getResources().getDrawable(R.drawable.six_plus_white));
            } else {
                medal.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.six_plus_white, null));
            }
            medal.setVisibility(View.VISIBLE);
        } else if (amountInspirations >= 3) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                medal.setImageDrawable(getResources().getDrawable(R.drawable.three_plus_white));
            } else {
                medal.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.three_plus_white, null));
            }
            medal.setVisibility(View.VISIBLE);
        } else {
            medal.setVisibility(View.INVISIBLE);
        }
    }

    public void updateData() {
        DatabaseHelper helper = new DatabaseHelper(this);

        person = helper.getPerson(personId);

        personName.setText(person.name);
        photo.setImageBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length));

        int currentPage = viewPager.getCurrentItem();
        int sizeBeforeUpdate = listInspirations.size(); //listInspiration is updated in the loadInspirations
        loadInspirations();

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), listInspirations);
        viewPager.setAdapter(tabPagerAdapter);

        if (sizeBeforeUpdate == listInspirations.size()) { //edited
            viewPager.setCurrentItem(currentPage);
        } else if (sizeBeforeUpdate > listInspirations.size()) { //deleted
            if (currentPage > 0) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        } else { //added
            if (listInspirations.size() > 0) {
                viewPager.setCurrentItem(listInspirations.size() - 1);
            }
        }

        //updateMedal(listInspirations.size());
        helper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    void addInspiration() {
        Intent i = new Intent(this, AddInspirationActivity.class);
        i.putExtra("id", personId);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        updateData();
        super.onResume();
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
}