package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Inspiracao;
import br.net.paulofernando.pessoasinspiradoras.databinding.ActivityAddInspirationBinding;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PersonListFragment;

public class AddInspirationActivity extends AppCompatActivity {

    private ActivityAddInspirationBinding binding;

    private long personId;
    private DtoFactory dtoFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddInspirationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btAddInpirationCancel.setOnClickListener(v -> cancelSettings());
        binding.btAddInspirationSave.setOnClickListener(v -> save());

        personId = getIntent().getLongExtra("id", -1);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dtoFactory = (DtoFactory) getApplication();
    }

    void cancelSettings() {
        this.finish();
    }

    void save() {
        if (binding.etAddInspiration.getText().toString().equals("")) {
            Utils.showAlertDialog(this, getString(R.string.warning),
                    getString(R.string.empty_field_inspiration));
            return;
        } else {
            saveInspiration(binding.etAddInspiration.getText().toString());
        }

        this.finish();
    }

    private void saveInspiration(String inspiration) {
        if (inspiration.length() > 0) {
            Inspiracao inspirationEntity = new Inspiracao();
            inspirationEntity.inspiration = inspiration;
            inspirationEntity.idUser = personId;

            Dao<Inspiracao, Integer> iDao = dtoFactory.getInspirationDao();
            try {
                iDao.create(inspirationEntity);
                PersonListFragment.UPDATE_PERSON_LIST = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //layoutInspirations.addView(InspirationView_.build(inspirationEntity, this));
        } else {
            Utils.showAlertDialog(AddInspirationActivity.this, getResources().getString(R.string.warning),
                    getResources().getString(R.string.enter_the_inspiration));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
