package br.net.paulofernando.pessoasinspiradoras;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;

import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

@EActivity(R.layout.activity_add_inspiration)
public class AddInspirationActivity extends AppCompatActivity {

    @ViewById(R.id.et_add_inspiration)
    EditText etInpiration;

    private long personId;
    private DtoFactory dtoFactory;

    @AfterViews
    void init() {
        personId = getIntent().getLongExtra("id", -1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dtoFactory = (DtoFactory) getApplication();
    }

    @Click(R.id.bt_add_inpiration_cancel)
    void cancelSettings() {
        this.finish();
    }

    @Click(R.id.bt_add_inspiration_save)
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
