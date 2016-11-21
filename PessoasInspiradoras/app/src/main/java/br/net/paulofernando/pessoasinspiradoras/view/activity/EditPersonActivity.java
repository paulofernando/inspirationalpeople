package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
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
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPersonActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_CROP = 2;

    @BindView(R.id.person_photo) ImageView photo;
    @BindView(R.id.et_person_name) EditText etPersonName;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private boolean changed;

    private Bitmap bmp;
    private Uri outputUri;
    private byte[] photoRaw;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);
        ButterKnife.bind(this);

        person = (Person) getIntent().getParcelableExtra(getResources().getString(R.string.person_details));
        bmp = getIntent().getExtras().getParcelable(getResources().getString(R.string.person_photo));

        photo.setImageBitmap(bmp);

        etPersonName.setText((getIntent().getStringExtra("name")));

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public static Intent getStartIntent(Context context, Person person, ImageView imageView) {
        Intent intent = new Intent(context, EditPersonActivity.class);

        imageView.buildDrawingCache();
        Bitmap image = imageView.getDrawingCache();

        Bundle extras = new Bundle();
        extras.putParcelable(context.getResources().getString(R.string.person_photo), image);
        extras.putParcelable(context.getResources().getString(R.string.person_details), person);
        intent.putExtras(extras);

        return intent;
    }

    @OnClick(R.id.bt_edit_cancel)
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

    @OnClick(R.id.bt_edit_save)
    void save() {
        DatabaseHelper helper = new DatabaseHelper(this);
        if (bmp != null) {
            helper.updatePersonById(person.id, etPersonName.getText().toString(),
                    Utils.getByteArrayFromBitmap(bmp));
        } else {
            helper.updatePersonById(person.id, etPersonName.getText().toString());
        }
        changed = false;
        helper.close();
        this.finish();
    }

    @OnClick(R.id.bt_edit_photo)
    void changePhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @OnClick(R.id.person_photo)
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

                photo.setImageBitmap(bmp);
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
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.content_discard_white));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelSettings();
                return true;
            case R.id.delete_person:
                deletePerson();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
