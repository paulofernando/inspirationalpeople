package br.net.paulofernando.pessoasinspiradoras;

import android.app.Activity;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends Activity {

	public final static String PREF_KEY = "pref_key";
	private static final int MENU_BACKUP = 0;
	private static final int MENU_RESTORE = 1;
	
	@ViewById(R.id.tv_password)
	TextView tvPassword;
	
	@ViewById(R.id.et_password)
	EditText etPassword;
	
	@ViewById(R.id.et_new_password)
	EditText etNewPassword;
	
	@ViewById(R.id.et_confirm_new_password)
	EditText etConfirmNewPassword;
	
	private boolean changed;
	
	@Click(R.id.bt_cancel)
	void cancelSettings() {
		this.finish();
	}
	
	@AfterViews
	void init() {
		if(PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_KEY, "").
				equals("")) {
			tvPassword.setVisibility(View.GONE);
			etPassword.setVisibility(View.GONE);			
		} else {
			tvPassword.setVisibility(View.VISIBLE);
			etPassword.setVisibility(View.VISIBLE);
		}
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		TextWatcher textWatcher = new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				changed = true;				
			}
		};
		
		etPassword.addTextChangedListener(textWatcher);
		etNewPassword.addTextChangedListener(textWatcher);
		etConfirmNewPassword.addTextChangedListener(textWatcher);
	}
	
	@Click(R.id.bt_save)
	void saveSettings() {
		if(PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_KEY, "").
				equals(etPassword.getText().toString())) {
			if(etNewPassword.getText().toString().equals(etConfirmNewPassword.getText().toString())) {
				PreferenceManager.getDefaultSharedPreferences(this).edit().
					putString(PREF_KEY, etNewPassword.getText().toString()).commit();
				Toast.makeText(this, getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
				etPassword.setText("");
				etNewPassword.setText("");
				etConfirmNewPassword.setText("");
				changed = false;
				this.finish();
			} else {
				Toast.makeText(this, getString(R.string.password_different), Toast.LENGTH_SHORT).show();
				changed = true;
			}
		} else {
			etPassword.requestFocus();
			Toast.makeText(this, getString(R.string.password_incorrect), Toast.LENGTH_SHORT).show();
			changed = true;
		}		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if(changed) {
					Utils.showConfirmDialog(
							this,
							getString(R.string.data_not_saved_title),
							getString(R.string.save_data_question),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									 saveSettings();
								}
							});
				} else {
					this.finish();
				}
				return true;
			case R.id.menu_backup:
				 DatabaseHelper helper = new DatabaseHelper(this);
				 //Utils.showSharePopup(this, helper.backup());
				 Utils.saveBackupInFile(this, helper.backup());
				 return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {  
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
		
		/*menu.add(0, MENU_BACKUP, Menu.NONE, R.string.menu_backup).setIcon(android.R.drawable.ic_menu_save)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, MENU_RESTORE, Menu.NONE, R.string.menu_restore).setIcon(android.R.drawable.ic_menu_upload)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);*/
        //return true;
    }
}
