package br.net.paulofernando.pessoasinspiradoras;

import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
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
public class SettingsActivity extends ActionBarActivity {

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
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
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
				Utils.showErrorDialog(this, getResources().getString(R.string.error), 
						getResources().getString(R.string.password_different));
				changed = true;
			}
		} else {
			etPassword.requestFocus();
			Utils.showErrorDialog(this, getResources().getString(R.string.error), 
					getResources().getString(R.string.password_incorrect));			
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
}
