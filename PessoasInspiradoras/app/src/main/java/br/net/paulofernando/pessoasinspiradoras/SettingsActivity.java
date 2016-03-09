package br.net.paulofernando.pessoasinspiradoras;

import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import br.net.paulofernando.pessoasinspiradoras.util.Utils;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {

    public final static String PREF_KEY = "pref_key";

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

    private Toolbar toolbar;

    @AfterViews
    void init() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_KEY, "").
                equals("")) {
            etPassword.setVisibility(View.GONE);
        } else {
            etPassword.setVisibility(View.VISIBLE);
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        TextWatcher textWatcher = new TextWatcher() {
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
        };

        etPassword.addTextChangedListener(textWatcher);
        etNewPassword.addTextChangedListener(textWatcher);
        etConfirmNewPassword.addTextChangedListener(textWatcher);
    }

    @Click(R.id.bt_save)
    void saveSettings() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_KEY, "").
                equals(etPassword.getText().toString())) {
            if (etNewPassword.getText().toString().equals(etConfirmNewPassword.getText().toString())) {
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
                if (changed) {
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
