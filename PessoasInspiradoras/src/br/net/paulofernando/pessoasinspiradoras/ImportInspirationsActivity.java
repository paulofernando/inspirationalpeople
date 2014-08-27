package br.net.paulofernando.pessoasinspiradoras;

import java.sql.SQLException;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.PersonView_;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.activity_import_inspirations)
public class ImportInspirationsActivity extends ActionBarActivity {
		
	@ViewById(R.id.layout_import)
	LinearLayout containerImports;
	
	@ViewById(R.id.btn_import_inspirations)
	Button btnImport;

	private DtoFactory dtoFactory;
	
	@AfterViews
	protected void init() {
		dtoFactory = (DtoFactory) getApplication();
		loadImports();
	}
	
	private void loadImports() {
		containerImports.removeAllViews();
		DatabaseHelper helper = new DatabaseHelper(this);
		//addPersons(helper.getPersonsData());
		Utils.restoreBackup(this);
		helper.close();
				
	}

	@Click(R.id.btn_import_inspirations)
	void addClick() {
		
	}

}
