package br.net.paulofernando.pessoasinspiradoras;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.util.RequestTask;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.PersonView_;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.activity_main)
public class Dashboard extends Activity {

	private static final int MENU_BACKUP = 0;
	private static final int MENU_SETTING = 1;
	
	
	@ViewById(R.id.layout_dashboard)
	LinearLayout dashboard;

	@ViewById(R.id.no_inspiration)
	LinearLayout noInspiration;
	
	@ViewById(R.id.btn_add_person)
	Button btnAddPerson;

	private DtoFactory dtoFactory;

	@AfterViews
	protected void init() {
		dtoFactory = (DtoFactory) getApplication();
		loadPersons();
	}
	
	private void loadPersons() {
		dashboard.removeAllViews();
		DatabaseHelper helper = new DatabaseHelper(this);
		addPersons(helper.getPersonsData());
		helper.close();
		
		if(dashboard.getChildCount() > 0) {
			noInspiration.setVisibility(View.GONE);
		} else {
			noInspiration.setVisibility(View.VISIBLE);
		}
	}

	@Click(R.id.btn_add_person)
	void addClick() {						
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if (data != null) {
			Uri uri = data.getData();

			if (uri != null) {
				Cursor c = null;
				try {
					c = getContentResolver()
							.query(uri,
									new String[] {
											ContactsContract.Data.DISPLAY_NAME,
											ContactsContract.Data.CONTACT_ID,
											ContactsContract.CommonDataKinds.Phone.NUMBER,
											ContactsContract.Contacts.PHOTO_ID },
									null, null, null);

					if (c != null && c.moveToFirst()) {
						String name = c.getString(0);
						String id = c.getString(1);
						String number = c.getString(2);
						String photoId = c.getString(3);
						addPerson(name, Long.parseLong(id), number, photoId);
					}
				} finally {
					if (c != null) {
						c.close();
					}
				}
			}
		}
	}

	public void addPerson(String name, long id, String phoneNumber,
			String photoId) {
		Dao<PersonEntity, Integer> pDao = dtoFactory.getPersonDao();
		
		PersonEntity person;
		try {
			// person = new PersonEntity(SimpleCrypto.encrypt(Utils.key, name),
			// id, SimpleCrypto.encrypt(Utils.key, phoneNumber));
			person = new PersonEntity(name, id, phoneNumber);
			person.setPhoto(Utils.getPhotoByResource(R.drawable.person, this));
			try {
				pDao.create(person);
			} catch (SQLException e) {
				e.printStackTrace();
				Log.e("Dashboard", "Error on addSelectedPerson");
			}

			dashboard.addView(PersonView_.build(person, this));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void addPersons(List<PersonEntity> list) {
		DatabaseHelper helper = new DatabaseHelper(this);
		for (PersonEntity person : list) {
			person.setAmountInpirations(helper.getInspirationData(person.id)
					.size());
			dashboard.addView(PersonView_.build(person, this));
		}
	}

	@Override
	protected void onResume() {
		loadPersons();
		super.onResume();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {        
		menu.add(0, MENU_BACKUP, Menu.NONE, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_save)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(1, MENU_SETTING, Menu.NONE, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_SETTING:
				Intent i = new Intent(this, SettingsActivity_.class);			 
				startActivity(i);			 			
				return true;
			case MENU_BACKUP:
				 DatabaseHelper helper = new DatabaseHelper(this);
				 Utils.showSharePopup(this, helper.backup());
				 return true;
		}
		return false;
	}

}
