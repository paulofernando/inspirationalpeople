package br.net.paulofernando.pessoasinspiradoras;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.soundcloud.android.crop.Crop;

@EActivity(R.layout.activity_edit_person)
public class EditPersonActivity extends Activity {

	public static final int RESULT_LOAD_IMAGE = 1;
	public static final int RESULT_CROP = 2;
	private long personId;
		
	@ViewById(R.id.person_photo)
	ImageView photo;
	
	@ViewById(R.id.et_person_name)
	EditText etPersonName;
	
	private Bitmap bmp;
	private Uri outputUri;
	
	@AfterViews
	void init() {
		personId = getIntent().getLongExtra("id", -1);
		byte[] photoRaw = getIntent().getByteArrayExtra("photo");
		photo.setImageBitmap(BitmapFactory.decodeByteArray(photoRaw, 0,
				photoRaw.length));
		etPersonName.setText((getIntent().getStringExtra("name")));
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Click(R.id.bt_edit_cancel)
	void cancelSettings() {
		this.finish();
	}
	
	@Click(R.id.bt_edit_save)
	void save() {
		DatabaseHelper helper = new DatabaseHelper(this);
		if(bmp != null) {
			helper.updatePersonById(personId, etPersonName.getText().toString(), 
					Utils.getByteArrayFromBitmap(bmp));
		} else {
			helper.updatePersonById(personId, etPersonName.getText().toString());
		}
		this.finish();
	}
	
	@Click(R.id.person_photo)
	void changePhoto() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
			Uri selectedImage = result.getData();
			outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
			new Crop(selectedImage).output(outputUri).asSquare().start(this);
						
		} else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
	}
	
	private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
        	try {
        		bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
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
				this.finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}		
	
}
