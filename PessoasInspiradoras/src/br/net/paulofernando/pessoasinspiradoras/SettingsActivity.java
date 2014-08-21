package br.net.paulofernando.pessoasinspiradoras;

import android.app.Activity;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends Activity {

	public final static String PREF_KEY = "pref_key";
	
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
				Toast.makeText(this, "Password changed!", Toast.LENGTH_SHORT).show();
				etPassword.setText("");
				etNewPassword.setText("");
				etConfirmNewPassword.setText("");
				changed = false;
				this.finish();
			} else {
				Toast.makeText(this, "Passwords are different!", Toast.LENGTH_SHORT).show();
				changed = true;
			}
		} else {
			etPassword.requestFocus();
			Toast.makeText(this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
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
							"Data not saved",
							"Do you want to save the data?",
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
