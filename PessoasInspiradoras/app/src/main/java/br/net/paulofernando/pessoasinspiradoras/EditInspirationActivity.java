package br.net.paulofernando.pessoasinspiradoras;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;

import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

@EActivity(R.layout.activity_edit_inspiration)
public class EditInspirationActivity extends AppCompatActivity {

    public static final int EDIT_INSPIRATION = 0;

    @ViewById(R.id.et_add_inspiration)
    EditText etInpiration;

    private long inspirationId, userId;
    private DtoFactory dtoFactory;

    private boolean changed;

    @AfterViews
    void init() {
        inspirationId = getIntent().getLongExtra("idInspiration", -1);
        userId = getIntent().getLongExtra("idInspiration", -1);
        etInpiration.setText(getIntent().getStringExtra("inspiration"));

        etInpiration.addTextChangedListener(new TextWatcher() {
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dtoFactory = (DtoFactory) getApplication();
    }

    @Click(R.id.bt_add_inpiration_cancel)
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

    @Click(R.id.bt_add_inspiration_save)
    void save() {
        if (etInpiration.getText().toString().equals("")) {
            Utils.showAlertDialog(this, getString(R.string.warning),
                    getString(R.string.empty_field_inspiration));
            return;
        } else {
            updateInspiration(etInpiration.getText().toString());
        }
        changed = false;
        this.returnScreen(true);
    }

    private void updateInspiration(String inspiration) {
        if (inspiration.length() > 0) {
            DatabaseHelper helper = new DatabaseHelper(this);
            helper.updateInspirationById(inspirationId, inspiration);

            InspiracaoEntity inspirationEntity = new InspiracaoEntity();
            inspirationEntity.inspiration = inspiration;
            inspirationEntity.idUser = userId;

            Dao<InspiracaoEntity, Integer> iDao = dtoFactory.getInspirationDao();
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
