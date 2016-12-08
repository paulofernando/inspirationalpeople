package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.backup.Backup;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.data.entity.ImportEntity;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Inspiracao;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.util.parser.PersonParser;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.util.parser.XMLPullParserHandler;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PersonListFragment;
import br.net.paulofernando.pessoasinspiradoras.view.widget.ImportInspirationsView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportInspirationsActivity extends AppCompatActivity {

    @BindView(R.id.layout_import) LinearLayout containerImports;
    @BindView(R.id.import_scrollview) ScrollView scrollView;
    @BindView(R.id.loading_import_text) TextView tvLoading;
    @BindView(R.id.buttons_add_inspiration) LinearLayout buttons;
    @BindView(R.id.btn_import_inspirations) Button btnImport;
    @BindView(R.id.btn_cancel_import_inspirations) Button btnCancel;
    @BindView(R.id.date_backup) TextView lastModifiedView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nothing_to_import) LinearLayout noImportContainer;

    List<PersonParser> importedPeople = new ArrayList<>();
    List<String> duplicatedInspirationsToDelete = new ArrayList<String>();
    private DtoFactory dtoFactory;
    private Backup backup;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_inspirations);
        ButterKnife.bind(this);

        dtoFactory = (DtoFactory) getApplication();
        backup = new Backup(this);

        Date lastModified = new Date(backup.getLocalBackupLastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        String formattedDate = sdf.format(lastModified);
        lastModifiedView.setText(this.getResources().getString(R.string.date_backup) + " " + formattedDate);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadImports();
    }

    @OnClick(R.id.btn_cancel_import_inspirations)
    void cancelSettings() {
        this.finish();
    }

    private void loadImports() {
        final DatabaseHelper helper = new DatabaseHelper(ImportInspirationsActivity.this);
        new AsyncTask<Void, Integer, List<PersonParser>>() {
            protected List<PersonParser> doInBackground(Void... filePaths) {
                return backup.importPeopleFromLocalXML();
            }

            protected void onProgressUpdate(Integer... progress) {

            }

            protected void onPostExecute(List<PersonParser> result) {
                containerImports.removeAllViews();
                createFields(result);
                helper.close();

                if (containerImports.getChildCount() == 0) {
                    noImportContainer.setVisibility(View.VISIBLE);
                    btnImport.setEnabled(false);
                } else {
                    buttons.setVisibility(View.VISIBLE);
                }

                tvLoading.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            }
        }.execute();


        runOnUiThread(new Runnable() {
            public void run() {

            }
        });
    }

    private void createFields(List<PersonParser> people) {
        DatabaseHelper helper = new DatabaseHelper(this);
        importedPeople.addAll(people);
        for (PersonParser person : people) {
            ImportEntity importEntity = new ImportEntity();
            importEntity.setName(person.getName());
            importEntity.setPersonId(person.getPersonId());
            importEntity.setPhoto(person.getPhoto());

            Person personEntity = helper.getPerson(person.name);

            if (personEntity != null) {
                importEntity.setMerged(true);
                if (hasNewInspirations(person, personEntity)) {
                    person.inspirations.removeAll(duplicatedInspirationsToDelete);
                    importEntity.setAmountInpirations(person.inspirations.size());
                    containerImports.addView(new ImportInspirationsView(importEntity, this));
                }
            } else {
                person.inspirations.removeAll(duplicatedInspirationsToDelete);
                importEntity.setAmountInpirations(person.inspirations.size());
                containerImports.addView(new ImportInspirationsView(importEntity, this));
            }


            duplicatedInspirationsToDelete.clear();
        }


        helper.close();
    }

    /**
     * Search if there are new inspiration to save and delete the duplicated inspirations
     *
     * @param personParser Person imported
     * @param personEntity Person saved
     * @return if there are new inspiration
     */
    private boolean hasNewInspirations(PersonParser personParser, Person personEntity) {
        DatabaseHelper helper = new DatabaseHelper(this);

        List<Inspiracao> userInspirations = helper.getInspirationData(personEntity.id);

        boolean newInspiration = false;
        int countFound = 0;
        for (String importedInspiration : personParser.getInspirations()) {
            for (Inspiracao inspirationEntity : userInspirations) {
                if (importedInspiration.equals(inspirationEntity.inspiration)) {
                    /* Put the inspiration in a List to delete later */
                    duplicatedInspirationsToDelete.add(importedInspiration);
                    countFound++;
                    break;
                }
            }
        }

        if (countFound < personParser.getInspirations().size()) {
            newInspiration = true;
        }

        helper.close();
        if (newInspiration) {
            return true;
        } else {
            return false;
        }
    }

    @OnClick(R.id.btn_import_inspirations)
    void addClick() {
        DatabaseHelper helper = new DatabaseHelper(this);

        for (int i = 0; i < containerImports.getChildCount(); i++) {
            if (((ImportInspirationsView) containerImports.getChildAt(i)).isChecked()) {
                ImportEntity importEntity = ((ImportInspirationsView) containerImports.getChildAt(i)).getImportPerson();

                long personId;

                if (importEntity.isMerged()) {
                    //get the id of the person with the same name of the imported data
                    personId = helper.getPerson(importEntity.getName()).id;
                } else {
                    personId = createPerson(importEntity);
                }

                if (personId != -1) {
                    PersonParser personToImport = getPersonToImportByName(importEntity.getName());
                    //---------------- Adding inspirations ---------------------
                    for (String inspiration : personToImport.inspirations) {
                        Inspiracao inspirationEntity = new Inspiracao();
                        inspirationEntity.inspiration = inspiration;
                        inspirationEntity.idUser = personId;

                        Dao<Inspiracao, Integer> iDao = dtoFactory.getInspirationDao();
                        try {
                            iDao.create(inspirationEntity);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        PersonListFragment.UPDATE_PERSON_LIST = true;
        helper.close();
        finish();
    }

    /**
     * Creates and save a new person data int the database
     * @param importEntity Imported data
     * @return the id of the person or -1 if a error occurred
     */
    /**
     * Creates and save a new person data int the database
     *
     * @param importEntity Imported data
     * @return the id of the person or -1 if a error occurred
     */
    private long createPerson(ImportEntity importEntity) {
        Dao<Person, Integer> pDao = dtoFactory.getPersonDao();
        Person person;
        long personId = Calendar.getInstance().getTimeInMillis();
        try {
            person = new Person(importEntity.getName(), personId, "");

            if (importEntity.getPhoto() != null) {
                //Set image to size 256x256
                Bitmap bmp = Utils.bitmapToMinimunSize(BitmapFactory.decodeByteArray(importEntity.getPhoto(), 0, importEntity.getPhoto().length), 256);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                person.setPhoto(byteArray);
            } else {
                Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.person);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                person.setPhoto(byteArray);
            }

            try {
                pDao.create(person);
                return personId;
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("AddPerson", "Error on addPerson");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return -1;
    }

    private PersonParser getPersonToImportByName(String personName) {
        for (PersonParser pp : importedPeople) {
            if (pp.name.equals(personName))
                return pp;
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
