package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.backup.Backup;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PersonListFragment;
import br.net.paulofernando.pessoasinspiradoras.view.widget.PersonView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity {

    //@BindView(R.id.no_inspiration) LinearLayout noInspiration;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private DtoFactory dtoFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        dtoFactory = (DtoFactory) getApplication();
    }

    /*private void loadPersons() {
        dashboard.removeAllViews();
        DatabaseHelper helper = new DatabaseHelper(this);
        addPersons(helper.getPersonsData());
        helper.close();

        if (dashboard.getChildCount() > 0) {
            noInspiration.setVisibility(View.GONE);
        } else {
            noInspiration.setVisibility(View.VISIBLE);
        }
    }*/

    void addPerson() {
        Intent intent = new Intent(this, AddPersonActivity.class);
        startActivity(intent);
    }

    public void addPerson(String name, long id, String phoneNumber,
                          String photoId) {
        Dao<Person, Integer> pDao = dtoFactory.getPersonDao();

        Person person;
        try {
            person = new Person(name, id, phoneNumber);
            person.setPhoto(Utils.getPhotoByResource(R.drawable.person, this));
            try {
                pDao.create(person);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("DashboardActivity", "Error on addSelectedPerson");
            }

            //dashboard.addView(new PersonView(person, this));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void addPersons(List<Person> list) {
        DatabaseHelper helper = new DatabaseHelper(this);
        for (Person person : list) {
            person.setAmountInpirations(helper.getInspirationData(person.id)
                    .size());
            //dashboard.addView(new PersonView(person, this));
        }
        helper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.add_person));
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Backup backup;
        switch (item.getItemId()) {
            case R.id.menu_add_person:
                addPerson();
                return true;
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_backup:
                DatabaseHelper helper = new DatabaseHelper(this);
                backup = new Backup(this);
                backup.saveLocalBackupInFile(this, helper.backup());
                helper.close();
                return true;
            case R.id.menu_restore:
                backup = new Backup(this);
                if (backup.hasLocalBackup()) {
                    Intent i2 = new Intent(this, ImportInspirationsActivity.class);
                    startActivity(i2);
                } else {
                    Utils.showAlertDialog(this, this.getResources().getString(R.string.file_not_found),
                            this.getResources().getString(R.string.backup_not_found));
                }
                return true;
            case R.id.menu_about:
                Intent i3 = new Intent(this, AboutActivity.class);
                startActivity(i3);
                return true;

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

}
