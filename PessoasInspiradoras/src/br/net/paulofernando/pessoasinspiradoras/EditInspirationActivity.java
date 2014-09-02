package br.net.paulofernando.pessoasinspiradoras;

import java.sql.SQLException;

import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.EditText;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.activity_edit_inspiration)
public class EditInspirationActivity extends ActionBarActivity {
	
	@ViewById(R.id.et_add_inspiration)
	EditText etInpiration;
	
	private long inspirationId, userId;
	private DtoFactory dtoFactory;
	
	@AfterViews
	void init() {
		inspirationId = getIntent().getLongExtra("idInspiration", -1);
		userId = getIntent().getLongExtra("idInspiration", -1);
		etInpiration.setText(getIntent().getStringExtra("inspiration"));
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		dtoFactory = (DtoFactory) getApplication();
	}
	
	@Click(R.id.bt_add_inpiration_cancel)
	void cancelSettings() {
		this.finish();
	}	
	
	@Click(R.id.bt_add_inspiration_save)
	void save() {
		if(etInpiration.getText().toString().equals("")) {
			Utils.showAlertDialog(this, getString(R.string.warning),
					getString(R.string.empty_field_inspiration));
			return;
		} else {
			updateInspiration(etInpiration.getText().toString());
		}
		
		this.finish();
	}
		
	private void updateInspiration(String inspiration) {
		if(inspiration.length() > 0) {			
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
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}		
	
}
