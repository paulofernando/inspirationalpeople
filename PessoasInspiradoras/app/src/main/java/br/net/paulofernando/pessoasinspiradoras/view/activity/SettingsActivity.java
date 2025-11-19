package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.databinding.ActivitySettingsBinding;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

public class SettingsActivity extends AppCompatActivity {

    public final static String PREF_KEY = "pref_key";

    private ActivitySettingsBinding binding;
    private boolean changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btCancel.setOnClickListener(v -> cancelSettings());
        binding.btSave.setOnClickListener(v -> saveSettings());

        if (PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_KEY, "").
                equals("")) {
            binding.inputLayoutCurrentPassword.setVisibility(View.GONE);
        } else {
            binding.inputLayoutCurrentPassword.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        binding.etPassword.addTextChangedListener(textWatcher);
        binding.etNewPassword.addTextChangedListener(textWatcher);
        binding.etConfirmNewPassword.addTextChangedListener(textWatcher);
    }

    void cancelSettings() {
        this.finish();
    }

    void saveSettings() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_KEY, "").
                equals(binding.etPassword.getText().toString())) {
            if (binding.etNewPassword.getText().toString().equals(binding.etConfirmNewPassword.getText().toString())) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().
                        putString(PREF_KEY, binding.etNewPassword.getText().toString()).commit();
                //Toast.makeText(this, getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
                binding.etPassword.setText("");
                binding.etNewPassword.setText("");
                binding.etConfirmNewPassword.setText("");
                changed = false;
                this.finish();
            } else {
                Utils.showErrorDialog(this, getResources().getString(R.string.error),
                        getResources().getString(R.string.password_different));
                changed = true;
            }
        } else {
            binding.etPassword.requestFocus();
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
