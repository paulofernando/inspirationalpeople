package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.data.entity.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddInspirationActivity extends AppCompatActivity {

    @BindView(R.id.et_add_inspiration) EditText etInpiration;

    private long personId;
    private DtoFactory dtoFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inspiration);
        ButterKnife.bind(this);

        personId = getIntent().getLongExtra("id", -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dtoFactory = (DtoFactory) getApplication();
    }

    @OnClick(R.id.bt_add_inpiration_cancel)
    void cancelSettings() {
        this.finish();
    }

    @OnClick(R.id.bt_add_inspiration_save)
    void save() {
        if (etInpiration.getText().toString().equals("")) {
            Utils.showAlertDialog(this, getString(R.string.warning),
                    getString(R.string.empty_field_inspiration));
            return;
        } else {
            saveInspiration(etInpiration.getText().toString());
        }

        this.finish();
    }

    private void saveInspiration(String inspiration) {
        if (inspiration.length() > 0) {
            InspiracaoEntity inspirationEntity = new InspiracaoEntity();
            inspirationEntity.inspiration = inspiration;
            inspirationEntity.idUser = personId;

            Dao<InspiracaoEntity, Integer> iDao = dtoFactory.getInspirationDao();
            try {
                iDao.create(inspirationEntity);
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
