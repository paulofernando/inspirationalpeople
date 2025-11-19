package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Inspiracao;
import br.net.paulofernando.pessoasinspiradoras.databinding.ActivityEditInspirationBinding;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PersonListFragment;

public class EditInspirationActivity extends AppCompatActivity {

    public static final int EDIT_INSPIRATION = 0;

    private ActivityEditInspirationBinding binding;

    private long inspirationId, userId;
    private DtoFactory dtoFactory;
    private boolean changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditInspirationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btAddInpirationCancel.setOnClickListener(v -> cancel());
        binding.btAddInspirationSave.setOnClickListener(v -> save());

        inspirationId = getIntent().getLongExtra("idInspiration", -1);
        userId = getIntent().getLongExtra("idInspiration", -1);
        binding.etAddInspiration.setText(getIntent().getStringExtra("inspiration"));

        binding.etAddInspiration.addTextChangedListener(new TextWatcher() {
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

        setSupportActionBar(binding.toolbar);
        dtoFactory = (DtoFactory) getApplication();
    }

    void cancel() {
        if (changed) {
            Utils.showConfirmDialog(
                    this,
                    getString(R.string.data_not_saved_title),
                    getString(R.string.data_not_saved_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            save();
                            returnScreen(true);
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            returnScreen(false);
                        }
                    });
        } else {
            returnScreen(false);
        }
    }

    void save() {
        if (binding.etAddInspiration.getText().toString().equals("")) {
            Utils.showAlertDialog(this, getString(R.string.warning),
                    getString(R.string.empty_field_inspiration));
            return;
        } else {
            updateInspiration(binding.etAddInspiration.getText().toString());
        }
        changed = false;
        PersonListFragment.UPDATE_PERSON_LIST = true;
        this.returnScreen(true);
    }

    private void updateInspiration(String inspiration) {
        if (inspiration.length() > 0) {
            DatabaseHelper helper = new DatabaseHelper(this);
            helper.updateInspirationById(inspirationId, inspiration);

            Inspiracao inspirationEntity = new Inspiracao();
            inspirationEntity.inspiration = inspiration;
            inspirationEntity.idUser = userId;

            Dao<Inspiracao, Integer> iDao = dtoFactory.getInspirationDao();
            try {
                iDao.create(inspirationEntity);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                helper.close();
            }
        } else {
            Utils.showAlertDialog(EditInspirationActivity.this, getResources().getString(R.string.warning),
                    getResources().getString(R.string.enter_the_inspiration));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void returnScreen(boolean success) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("return", success);
        setResult(EDIT_INSPIRATION, returnIntent);
        finish();
    }

}
