package br.net.paulofernando.pessoasinspiradoras;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.backup.Backup;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.PersonView_;

@EActivity(R.layout.activity_main)
public class Dashboard extends AppCompatActivity {

    @ViewById(R.id.layout_dashboard)
    LinearLayout dashboard;

    @ViewById(R.id.no_inspiration)
    LinearLayout noInspiration;

    private DtoFactory dtoFactory;

    private Toolbar toolbar;

    @AfterViews
    protected void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        dtoFactory = (DtoFactory) getApplication();
        loadPersons();
    }

    private void loadPersons() {
        dashboard.removeAllViews();
        DatabaseHelper helper = new DatabaseHelper(this);
        addPersons(helper.getPersonsData());
        helper.close();

        if (dashboard.getChildCount() > 0) {
            noInspiration.setVisibility(View.GONE);
        } else {
            noInspiration.setVisibility(View.VISIBLE);
        }
    }

    void addPerson() {
        Intent intent = new Intent(this, AddPersonActivity_.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver()
                            .query(uri,
                                    new String[]{
                                            ContactsContract.Data.DISPLAY_NAME,
                                            ContactsContract.Data.CONTACT_ID,
                                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.Contacts.PHOTO_ID},
                                    null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String name = c.getString(0);
                        String id = c.getString(1);
                        String number = c.getString(2);
                        String photoId = c.getString(3);
                        addPerson(name, Long.parseLong(id), number, photoId);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    public void addPerson(String name, long id, String phoneNumber,
                          String photoId) {
        Dao<PersonEntity, Integer> pDao = dtoFactory.getPersonDao();

        PersonEntity person;
        try {
            person = new PersonEntity(name, id, phoneNumber);
            person.setPhoto(Utils.getPhotoByResource(R.drawable.person, this));
            try {
                pDao.create(person);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("Dashboard", "Error on addSelectedPerson");
            }

            dashboard.addView(PersonView_.build(person, this));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void addPersons(List<PersonEntity> list) {
        DatabaseHelper helper = new DatabaseHelper(this);
        for (PersonEntity person : list) {
            person.setAmountInpirations(helper.getInspirationData(person.id)
                    .size());
            dashboard.addView(PersonView_.build(person, this));
        }
        helper.close();
    }

    @Override
    protected void onResume() {
        loadPersons();
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
                Intent i = new Intent(this, SettingsActivity_.class);
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
                    Intent i2 = new Intent(this, ImportInspirationsActivity_.class);
                    startActivity(i2);
                } else {
                    Utils.showAlertDialog(this, this.getResources().getString(R.string.file_not_found),
                            this.getResources().getString(R.string.backup_not_found));
                }
                return true;
            case R.id.menu_about:
                Intent i3 = new Intent(this, About.class);
                startActivity(i3);
                return true;

        }
        return false;
    }

}
