package br.net.paulofernando.pessoasinspiradoras;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_login)
public class Login extends Activity {

	@ViewById(R.id.ed_chave)
	EditText edChave;
	
	private float downXValue, downYValue;
	
	@AfterViews
	void init() {
		
		if(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.PREF_KEY, "").equals("")) {
			Intent intent = new Intent(Login.this, Dashboard_.class);
			startActivity(intent);
		}
		
		edChave.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(isPassCorrect()) {					
					Intent intent = new Intent(Login.this, Dashboard_.class);
					startActivity(intent);
					edChave.setText("");
				}				
			}
		});
	}
	
	@Click(R.id.btn_add_person)
	void entrar() {
		if(isPassCorrect()) {
			edChave.setText("");
			Intent intent = new Intent(this, Dashboard_.class);
			startActivity(intent);
		} else {
			Toast.makeText(this, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
		}
	}
		
	private boolean isPassCorrect() {
		return edChave.getText().toString().equals(
				PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.PREF_KEY, ""));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
		        // store the X value when the user's finger was pressed down
		        downXValue = event.getX();
		        downYValue = event.getY();
		        break;
		    }
		
		    case MotionEvent.ACTION_UP: {
		        // Get the X value when the user released his/her finger
		        float currentX = event.getX();
		        float currentY = event.getY();
		        // check if horizontal or vertical movement was bigger
		
		        if (Math.abs(downXValue - currentX) > Math.abs(downYValue
		                - currentY)) {
		            // going backwards: pushing stuff to the right
		            if (downXValue < currentX) {
		                Log.v("", "right");
		            }
		
		            // going forwards: pushing stuff to the left
		            if (downXValue > currentX) {
		                Log.v("", "left");
		                entrar();
		                return true;
		            }
		
		        } else {
		            if (downYValue < currentY) {
		                Log.v("", "down");		
		            }
		            
		            if (downYValue > currentY) {
		                Log.v("", "up");		
		            }
		        }
		        break;
		    }
		}
		return false;
	}
}
