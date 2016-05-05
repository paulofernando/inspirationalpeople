package br.net.paulofernando.pessoasinspiradoras;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
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

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

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
        Intent intent = new Intent(new Intent(this, PopupImage.class));
        intent.putExtra("photo", photoRaw);
        startActivity(intent);
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
                if (bmp.getWidth() > 512) {
                    bmp = Bitmap.createScaledBitmap(bmp, 512, 512, false);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
