package br.net.paulofernando.pessoasinspiradoras;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.fragment.PagerInspirations;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

@EActivity(R.layout.activity_edit_person)
public class EditPersonActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_CROP = 2;

    @ViewById(R.id.person_photo)
    ImageView photo;

    @ViewById(R.id.et_person_name)
    EditText etPersonName;

    private long personId;
    private boolean changed;

    private Bitmap bmp;
    private Uri outputUri;
    private byte[] photoRaw;

    private Toolbar toolbar;

    @AfterViews
    void init() {
        personId = getIntent().getLongExtra("id", -1);
        photoRaw = getIntent().getByteArrayExtra("photo");

        photo.setImageBitmap(BitmapFactory.decodeByteArray(photoRaw, 0,
                photoRaw.length));

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Click(R.id.bt_edit_cancel)
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

    @Click(R.id.bt_edit_save)
    void save() {
        DatabaseHelper helper = new DatabaseHelper(this);
        if (bmp != null) {
            helper.updatePersonById(personId, etPersonName.getText().toString(),
                    Utils.getByteArrayFromBitmap(bmp));
        } else {
            helper.updatePersonById(personId, etPersonName.getText().toString());
        }
        changed = false;
        helper.close();
        this.finish();
    }

    @Click(R.id.bt_edit_photo)
    void changePhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Click(R.id.person_photo)
    void viewPhoto() {
        BlurBehind.getInstance().execute(this, new OnBlurCompleteListener() {
            @Override
            public void onBlurComplete() {
                Intent intent = new Intent(new Intent(EditPersonActivity.this, PopupImage.class));
                intent.putExtra("photo", photoRaw);
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

                            Intent i = new Intent(EditPersonActivity.this, Dashboard_.class);
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

                                                            Intent i = new Intent(EditPersonActivity.this, Dashboard_.class);
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
        helper.deletePersonById(personId);
        helper.deleteAllInspirationsByUserId(personId);
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
