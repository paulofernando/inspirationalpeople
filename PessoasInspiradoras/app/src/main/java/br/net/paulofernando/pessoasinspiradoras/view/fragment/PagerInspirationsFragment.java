package br.net.paulofernando.pessoasinspiradoras.view.fragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;

import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Inspiracao;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.databinding.PagerInspirationsBinding;
import br.net.paulofernando.pessoasinspiradoras.databinding.ContentPagerInspirationsBinding;
import br.net.paulofernando.pessoasinspiradoras.view.activity.AddInspirationActivity;
import br.net.paulofernando.pessoasinspiradoras.view.activity.EditPersonActivity;
import br.net.paulofernando.pessoasinspiradoras.view.activity.PopupImageActivity;
import br.net.paulofernando.pessoasinspiradoras.view.adapter.TabPagerAdapter;

public class PagerInspirationsFragment extends FragmentActivity implements
        ActionBar.TabListener {

    
    long personId;
    private PagerInspirationsBinding binding;
    private ContentPagerInspirationsBinding contentBinding;

    private TabPagerAdapter tabPagerAdapter;
    private Person person;
    private List<Inspiracao> listInspirations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PagerInspirationsBinding.inflate(getLayoutInflater());
        contentBinding = ContentPagerInspirationsBinding.bind(binding.getRoot());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            person = getIntent().getParcelableExtra(getResources().getString(R.string.person_details), Person.class);
        } else {
            person = getIntent().getParcelableExtra(getResources().getString(R.string.person_details));
        }
        contentBinding.personNameDetailPager.setText(person.name);
        personId = person.id;

        contentBinding.photoSelectedPersonPager.setOnClickListener(new View.OnClickListener() {
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

        contentBinding.backPersonPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerInspirationsFragment.this.finish();
            }
        });

        contentBinding.editPersonPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPersonData();
            }
        });

        contentBinding.personNameDetailPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPersonData();
            }
        });

        loadInspirations();

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),
                listInspirations);

        contentBinding.pagerInspirations.setAdapter(tabPagerAdapter);

        contentBinding.indicator.setViewPager(contentBinding.pagerInspirations);

        FloatingActionButton myFab = (FloatingActionButton)  findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addInspiration();
            }
        });

    }

    public static Intent getStartIntent(Context context, Person person) {
        Intent intent = new Intent(context, PagerInspirationsFragment.class);

        Bundle extras = new Bundle();
        extras.putParcelable(context.getResources().getString(R.string.person_details), person);
        intent.putExtras(extras);

        return intent;
    }

    private void editPersonData() {
        Intent intent = EditPersonActivity.getStartIntent(this, person);
        startActivity(intent);
    }

    public void loadInspirations() {
        DatabaseHelper helper = new DatabaseHelper(this);
        listInspirations = helper.getInspirationData(personId);
        helper.close();
    }

    private void updateMedal(int amountInspirations) {
        if (amountInspirations >= 9) {
            contentBinding.medalSelectedPersonPager.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nine_plus_white));
            contentBinding.medalSelectedPersonPager.setVisibility(View.VISIBLE);
        } else if (amountInspirations >= 6) {
            contentBinding.medalSelectedPersonPager.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.six_plus_white));
            contentBinding.medalSelectedPersonPager.setVisibility(View.VISIBLE);
        } else if (amountInspirations >= 3) {
            contentBinding.medalSelectedPersonPager.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.three_plus_white));
            contentBinding.medalSelectedPersonPager.setVisibility(View.VISIBLE);
        } else {
            contentBinding.medalSelectedPersonPager.setVisibility(View.INVISIBLE);
        }
    }

    public void updateData() {
        DatabaseHelper helper = new DatabaseHelper(this);

        person = helper.getPerson(personId);

        contentBinding.personNameDetailPager.setText(person.name);
        contentBinding.photoSelectedPersonPager.setImageBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length));

        int currentPage = contentBinding.pagerInspirations.getCurrentItem();
        int sizeBeforeUpdate = listInspirations.size(); //listInspiration is updated in the loadInspirations
        loadInspirations();

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), listInspirations);
        contentBinding.pagerInspirations.setAdapter(tabPagerAdapter);

        if (sizeBeforeUpdate == listInspirations.size()) { //edited
            contentBinding.pagerInspirations.setCurrentItem(currentPage);
        } else if (sizeBeforeUpdate > listInspirations.size()) { //deleted
            if (currentPage > 0) {
                contentBinding.pagerInspirations.setCurrentItem(currentPage - 1);
            }
        } else { //added
            if (listInspirations.size() > 0) {
                contentBinding.pagerInspirations.setCurrentItem(listInspirations.size() - 1);
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
        return contentBinding.pagerInspirations;
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        contentBinding.pagerInspirations.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
