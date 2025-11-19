package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
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
import br.net.paulofernando.pessoasinspiradoras.databinding.ActivityAddPersonBinding;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

public class AddPersonActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_CROP = 2;
    private static final int PERMISSION_REQUEST_READ_MEDIA = 100;

    private ActivityAddPersonBinding binding;
    private boolean changed;
    private Bitmap bmp = null;
    private Uri outputUri;
    private DtoFactory dtoFactory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPersonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dtoFactory = (DtoFactory) getApplication();
        binding.addPersonPhoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.person));

        binding.btAddCancel.setOnClickListener(v -> cancelSettings());
        binding.btAddSave.setOnClickListener(v -> save());
        binding.addPersonPhoto.setOnClickListener(v -> changePhoto());

        binding.etAddPersonName.addTextChangedListener(new TextWatcher() {
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

        setSupportActionBar(binding.toolbar);
    }

    void cancelSettings() {
        this.finish();
    }

    void save() {
        if (binding.etAddPersonName.getText().toString().equals("")) {
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
        if (helper.getPerson(binding.etAddPersonName.getText().toString()) != null) {
            Utils.showErrorDialog(this, getString(R.string.error), getString(R.string.name_registered));
            return false;
        }

        Dao<Person, Integer> pDao = dtoFactory.getPersonDao();

        Person person;
        try {
            // person = new Person(SimpleCrypto.encrypt(Utils.key, name),
            // id, SimpleCrypto.encrypt(Utils.key, phoneNumber));
            person = new Person(binding.etAddPersonName.getText().toString(), Calendar.getInstance().getTimeInMillis(), "");

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

    void changePhoto() {
        if (checkStoragePermission()) {
            pickImage();
        } else {
            requestStoragePermission();
        }
        changed = true;
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    PERMISSION_REQUEST_READ_MEDIA);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_MEDIA);
        }
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_MEDIA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
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

                binding.addPersonPhoto.setImageBitmap(bmp);

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
