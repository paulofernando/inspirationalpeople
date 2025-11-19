package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.databinding.ActivityEditPersonBinding;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PersonListFragment;

public class EditPersonActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_CROP = 2;
    private static final int PERMISSION_REQUEST_READ_MEDIA = 100;

    private ActivityEditPersonBinding binding;
    private boolean changed;

    private Bitmap bmp;
    private Uri outputUri;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPersonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btEditCancel.setOnClickListener(v -> cancelSettings());
        binding.btEditSave.setOnClickListener(v -> save());
        binding.btEditPhoto.setOnClickListener(v -> changePhoto());
        binding.personPhoto.setOnClickListener(v -> viewPhoto());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            person = getIntent().getParcelableExtra(getResources().getString(R.string.person_details), Person.class);
        } else {
            person = getIntent().getParcelableExtra(getResources().getString(R.string.person_details));
        }
        binding.personPhoto.setImageBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length));
        binding.etPersonName.setText(person.name);

        binding.etPersonName.addTextChangedListener(new TextWatcher() {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public static Intent getStartIntent(Context context, Person person) {
        Intent intent = new Intent(context, EditPersonActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(context.getResources().getString(R.string.person_details), person);
        intent.putExtras(extras);
        return intent;
    }

    void cancelSettings() {
        if (changed) {
            Utils.showConfirmDialog(
                    this,
                    getString(R.string.data_not_saved_title),
                    getString(R.string.data_not_saved_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            save();
                            EditPersonActivity.this.finish();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditPersonActivity.this.finish();
                        }
                    });
        } else {
            finish();
        }
    }

    void save() {
        DatabaseHelper helper = new DatabaseHelper(this);
        if (bmp != null) {
            helper.updatePersonById(person.id, binding.etPersonName.getText().toString(),
                    Utils.getByteArrayFromBitmap(bmp));
        } else {
            helper.updatePersonById(person.id, binding.etPersonName.getText().toString());
        }
        changed = false;
        PersonListFragment.UPDATE_PERSON_LIST = true;
        helper.close();
        this.finish();
    }

    void changePhoto() {
        if (checkStoragePermission()) {
            pickImage();
        } else {
            requestStoragePermission();
        }
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

    void viewPhoto() {
        BlurBehind.getInstance().execute(this, new OnBlurCompleteListener() {
            @Override
            public void onBlurComplete() {
                Intent intent = PopupImageActivity.getStartIntent(EditPersonActivity.this, person);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = result.getData();
            outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(selectedImage, outputUri).asSquare().start(this);
            changed = true;
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
                int maxImageMeasure = (int) getResources().getDimension(R.dimen.max_image_measure);
                if (bmp.getWidth() > maxImageMeasure) {
                    bmp = Bitmap.createScaledBitmap(bmp, maxImageMeasure, maxImageMeasure, false);
                }

                binding.personPhoto.setImageBitmap(bmp);
                changed = true;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void deletePerson() {
        Utils.showConfirmDialogDangerous(this, getString(R.string.delete_contact_title),
                getString(R.string.delete_contact_question),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ((PreferenceManager.getDefaultSharedPreferences(
                                EditPersonActivity.this).getString(
                                SettingsActivity.PREF_KEY, "").equals(""))) {
                            deletePersonData();

                            Intent i = new Intent(EditPersonActivity.this, DashboardActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            final EditText input = new EditText(EditPersonActivity.this);
                            input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            input.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                            input.setId(android.R.id.edit);
                            input.setLines(1);
                            new AlertDialog.Builder(EditPersonActivity.this)
                                    .setTitle(getString(R.string.enter_password))
                                    .setView(input)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setNeutralButton(
                                            android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int whichButton) {
                                                    try {
                                                        if (input.getText().toString().equals(PreferenceManager
                                                                .getDefaultSharedPreferences(EditPersonActivity.this)
                                                                .getString(SettingsActivity.PREF_KEY, null))) {
                                                            deletePersonData();

                                                            Intent i = new Intent(EditPersonActivity.this, DashboardActivity.class);
                                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(i);
                                                        } else {
                                                            Utils.showErrorDialog(EditPersonActivity.this,
                                                                    getResources().getString(R.string.error),
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
        DatabaseHelper helper = new DatabaseHelper(EditPersonActivity.this);
        helper.deletePersonById(person.id);
        helper.deleteAllInspirationsByUserId(person.id);
        helper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_person, menu);
        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.content_discard_white));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            cancelSettings();
            return true;
        } else if (itemId == R.id.delete_person) {
            deletePerson();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
