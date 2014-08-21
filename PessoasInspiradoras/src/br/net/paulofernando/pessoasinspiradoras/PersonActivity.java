package br.net.paulofernando.pessoasinspiradoras;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.InspirationView_;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.activity_person)
public class PersonActivity extends Activity {

	private static final int MENU_DELETE_PERSON = 0;
	
	@ViewById(R.id.photo_selected_person)
	ImageView photo;

	@ViewById(R.id.add_inspiration)
	Button btnAdddInspiration;
	
	@ViewById(R.id.person_name_detail)
	TextView personName;
	
	long personId;
	
	private DtoFactory dtoFactory;

	@ViewById(R.id.layout_inspirations)
	LinearLayout layoutInspirations;

	public PersonActivity() {}

	@AfterViews
	void init() {
		personId = getIntent().getLongExtra("id", -1);
		byte[] photoRaw = getIntent().getByteArrayExtra("photo");
		photo.setImageBitmap(BitmapFactory.decodeByteArray(photoRaw, 0,
				photoRaw.length));
		
		try {
			personName.setText(getIntent().getStringExtra("name"));
			//personName.setText(SimpleCrypto.decrypt(Utils.key, getIntent().getStringExtra("name")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadInspirations();
		
		dtoFactory = (DtoFactory) getApplication();
		if(personName.getText().toString().equals("")) {			
			btnAdddInspiration.setEnabled(false);
		}
				
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void loadInspirations() {
		layoutInspirations.removeAllViews();
    	DatabaseHelper helper = new DatabaseHelper(this);    	
    	
    	List<InspiracaoEntity> list = helper.getInspirationData(personId);
    	for(InspiracaoEntity inspiration: list) {
    		layoutInspirations.addView(InspirationView_.build(inspiration, this));
    	}
    	    	
    	helper.close();
	}

	void deletePerson() {
		Utils.showConfirmDialog(
				this,
				"Delete contact",
				"Do you want to delete this contact? All data about it will be lost!",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						final EditText input = new EditText(PersonActivity.this);
						input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
						input.setTransformationMethod(PasswordTransformationMethod.getInstance());						
						input.setId(android.R.id.edit);
						input.setLines(1);
						new AlertDialog.Builder(PersonActivity.this)
								.setTitle("Inform the password").setView(input)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int whichButton) {
					                	try {
											if(input.getText().toString().equals(
													PreferenceManager.getDefaultSharedPreferences(PersonActivity.this).getString(SettingsActivity.PREF_KEY, null))) {
												
												DatabaseHelper helper = new DatabaseHelper(
														PersonActivity.this);
												helper.deletePersonById(personId);						
												helper.deleteAllInspirationsByUserId(personId);
												
												finish();
											} else {
												Toast.makeText(PersonActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
											}
											
										} catch (Exception e) {
											e.printStackTrace();
										}
					                }
					            }).show();
					}
				});
	}

	@Click(R.id.add_inspiration)
	void addInspiration() {
		final EditText input = new EditText(PersonActivity.this);
		input.setId(android.R.id.edit);
		input.setLines(2);
		new AlertDialog.Builder(PersonActivity.this)
				.setTitle("What is the new inspiration?").setView(input)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	try {
							saveInspiration(input.getText().toString());
							//saveInspiration(SimpleCrypto.encrypt(Utils.key, input.getText().toString()));
						} catch (Exception e) {
							e.printStackTrace();
						}
	                }
	            }).show();		
	}
	
	private void saveInspiration(String inspiration) {
		if(inspiration.length() > 0) {
			InspiracaoEntity inspirationEntity = new InspiracaoEntity();
			inspirationEntity.inspiration = inspiration;
			inspirationEntity.idUser = personId;
					
			Dao<InspiracaoEntity, Integer> iDao = dtoFactory.getInspirationDao();    	
	    	try {
				iDao.create(inspirationEntity);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    	layoutInspirations.addView(InspirationView_.build(inspirationEntity, this));
		} else {
			Toast.makeText(this, "You need inform the inspiration", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void updateData() {
		DatabaseHelper helper = new DatabaseHelper(this);
		PersonEntity person =  helper.getPerson(personId);
		personName.setText(person.name);
		photo.setImageBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length));
	}
		
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {        
        menu.add(0, MENU_DELETE_PERSON, Menu.NONE, R.string.menu_delete_person).setIcon(android.R.drawable.ic_menu_delete)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);        
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_DELETE_PERSON:	
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
	void editPerson() {
		Intent intent = new Intent(this, EditPersonActivity_.class);
		intent.putExtra("name", getIntent().getStringExtra("name"));
		intent.putExtra("photo", getIntent().getByteArrayExtra("photo"));
		intent.putExtra("id", personId);
		startActivity(intent);
	}

}
