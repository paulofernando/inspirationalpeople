package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPersonActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_CROP = 2;

    @BindView(R.id.add_person_photo) ImageView photo;
    @BindView(R.id.et_add_person_name) EditText etPersonName;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private boolean changed;
    private Bitmap bmp = null;
    private Uri outputUri;
    private DtoFactory dtoFactory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        ButterKnife.bind(this);

        dtoFactory = (DtoFactory) getApplication();
        photo.setImageDrawable(getResources().getDrawable(R.drawable.person));

        etPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changed = true;
            }
        });

        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.bt_add_cancel)
    void cancelSettings() {
        this.finish();
    }

    @OnClick(R.id.bt_add_save)
    void save() {
        if (etPersonName.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.empty_field_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (savePerson()) {
            changed = false;
            this.finish();
        }
    }

    private boolean savePerson() {
        DatabaseHelper helper = new DatabaseHelper(this);
        if (helper.getPerson(etPersonName.getText().toString()) != null) {
            Utils.showErrorDialog(this, getString(R.string.error), getString(R.string.name_registered));
            return false;
        }

        Dao<Person, Integer> pDao = dtoFactory.getPersonDao();

        Person person;
        try {
            // person = new Person(SimpleCrypto.encrypt(Utils.key, name),
            // id, SimpleCrypto.encrypt(Utils.key, phoneNumber));
            person = new Person(etPersonName.getText().toString(), Calendar.getInstance().getTimeInMillis(), "");

            if (bmp != null) {
                person.setPhoto(Utils.getByteArrayFromBitmap(bmp));
            } else {
                person.setPhoto(Utils.getPhotoByResource(R.drawable.person, this));
            }

            try {
                pDao.create(person);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("AddPerson", "Error on addPerson");
                return false;
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return true;
    }

    @OnClick(R.id.add_person_photo)
    void changePhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
        changed = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = result.getData();
            outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(selectedImage, outputUri).asSquare().start(this);

        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
                if (bmp.getWidth() > 256) {
                    bmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);
                }

                photo.setImageBitmap(bmp);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (changed) {
                    Utils.showConfirmDialog(
                            this,
                            getString(R.string.data_not_saved_title),
                            getString(R.string.data_not_saved_question),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    save();
                                    AddPersonActivity.this.finish();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddPersonActivity.this.finish();
                                }
                            });
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
