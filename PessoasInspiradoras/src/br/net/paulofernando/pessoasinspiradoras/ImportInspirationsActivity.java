package br.net.paulofernando.pessoasinspiradoras;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.ImportEntity;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.parser.PersonParser;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.ImportInspirationsView_;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

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
		
		createFields(Utils.importPeopleFromXML(this));		
		helper.close();
				
	}
	
	private void createFields(List<PersonParser> people) {
		DatabaseHelper helper = new DatabaseHelper(this);
		
		for(PersonParser person: people) {
			ImportEntity importEntity = new ImportEntity();
			importEntity.setName(person.getName());
			importEntity.setPersonId(Long.parseLong(person.getId()));
			importEntity.setAmountInpirations(person.inspirations.size());
			
			if(helper.getPerson(person.name) != null) {
				importEntity.setMerged(true);
			}
			
			containerImports.addView(ImportInspirationsView_.build(importEntity, this));
		}
	}
	
	@Click(R.id.btn_import_inspirations)
	void addClick() {
		
	}

}
