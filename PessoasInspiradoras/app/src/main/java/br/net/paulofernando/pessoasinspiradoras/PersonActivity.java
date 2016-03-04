package br.net.paulofernando.pessoasinspiradoras;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.image.ImageFromSentence;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.InspirationView_;

@EActivity(R.layout.activity_person)
public class PersonActivity extends AppCompatActivity {

    @ViewById(R.id.photo_selected_person)
    ImageView photo;

    @ViewById(R.id.medal_selected_person)
    ImageView medal;

    @ViewById(R.id.add_inspiration)
    Button btnAdddInspiration;

    @ViewById(R.id.person_name_detail)
    TextView personName;
    long personId;

    @ViewById(R.id.layout_inspirations)
    LinearLayout layoutInspirations;
    private PersonEntity person;

    public PersonActivity() {
    }

    @AfterViews
    void init() {
        personId = getIntent().getLongExtra("id", -1);
        personName.setText(getIntent().getStringExtra("name"));

        //loadInspirations();

        if (personName.getText().toString().equals("")) {
            btnAdddInspiration.setEnabled(false);
        }
    }

    private void updateMedal(int amountInspirations) {
        if (amountInspirations >= 9) {
            medal.setImageDrawable(getResources().getDrawable(R.drawable.nine_plus_white));
            medal.setVisibility(View.VISIBLE);
        } else if (amountInspirations >= 6) {
            medal.setImageDrawable(getResources().getDrawable(R.drawable.six_plus_white));
            medal.setVisibility(View.VISIBLE);
        } else if (amountInspirations >= 3) {
            medal.setImageDrawable(getResources().getDrawable(R.drawable.three_plus_white));
            medal.setVisibility(View.VISIBLE);
        } else {
            medal.setVisibility(View.INVISIBLE);
        }
    }

    public void loadInspirations() {
        layoutInspirations.removeAllViews();
        DatabaseHelper helper = new DatabaseHelper(this);

        List<InspiracaoEntity> list = helper.getInspirationData(personId);
        for (InspiracaoEntity inspiration : list) {
            layoutInspirations.addView(InspirationView_.build(inspiration, this));
        }

        updateMedal(list.size());
        helper.close();
    }

    void deletePerson() {

        Utils.showConfirmDialog(
                this,
                getString(R.string.delete_contact_title),
                getString(R.string.delete_contact_question),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ((PreferenceManager.getDefaultSharedPreferences(PersonActivity.this).getString(SettingsActivity.PREF_KEY, "").equals(""))) {
                            deletePersonData();
                            finish();
                        } else {
                            final EditText input = new EditText(PersonActivity.this);
                            input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            input.setId(android.R.id.edit);
                            input.setLines(1);
                            new AlertDialog.Builder(PersonActivity.this)
                                    .setTitle(getString(R.string.enter_password)).setView(input)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            try {
                                                if (input.getText().toString().equals(
                                                        PreferenceManager.getDefaultSharedPreferences(PersonActivity.this).getString(SettingsActivity.PREF_KEY, null))) {
                                                    deletePersonData();
                                                    finish();
                                                } else {
                                                    Utils.showErrorDialog(PersonActivity.this, getResources().getString(R.string.error),
                                                            getResources().getString(R.string.wrong_password));
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).show();
                        }
                    }
                });

    }

    private void deletePersonData() {
        DatabaseHelper helper = new DatabaseHelper(
                PersonActivity.this);
        helper.deletePersonById(personId);
        helper.deleteAllInspirationsByUserId(personId);
        helper.close();
    }

    @Click(R.id.add_inspiration)
    void addInspiration() {
        Intent i = new Intent(this, AddInspirationActivity_.class);
        i.putExtra("id", personId);
        startActivity(i);
    }


    private void updateData() {
        DatabaseHelper helper = new DatabaseHelper(this);
        person = helper.getPerson(personId);
        personName.setText(person.name);
        photo.setImageBitmap(new ImageFromSentence(this).getCroppedBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length)));
        loadInspirations();
        helper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_person, menu);
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.add_inspiration));
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_inspiration:
                addInspiration();
                return true;
            case R.id.menu_delete_person:
                deletePerson();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        updateData();
        super.onResume();
    }

    @Click(R.id.photo_selected_person)
    void clickPhoto() {
        editPerson();
    }

    @Click(R.id.person_name_detail)
    void clickName() {
        editPerson();
    }

    void editPerson() {
        Intent intent = new Intent(this, EditPersonActivity_.class);
        intent.putExtra("name", personName.getText());
        intent.putExtra("photo", person.photo);
        intent.putExtra("id", personId);
        startActivity(intent);
    }

}
