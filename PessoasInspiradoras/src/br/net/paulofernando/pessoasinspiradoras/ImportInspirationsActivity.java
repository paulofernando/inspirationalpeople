package br.net.paulofernando.pessoasinspiradoras;

import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.widget.Button;
import android.widget.LinearLayout;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.dao.DtoFactory;
import br.net.paulofernando.pessoasinspiradoras.model.ImportEntity;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.parser.PersonParser;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.ImportInspirationsView;
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
	List<PersonParser> importedPeople;
	
	@AfterViews
	protected void init() {
		dtoFactory = (DtoFactory) getApplication();
		loadImports();
	}
	
	private void loadImports() {
		containerImports.removeAllViews();
		DatabaseHelper helper = new DatabaseHelper(this);
		importedPeople = Utils.importPeopleFromXML(this);
		createFields(importedPeople);		
		helper.close();
				
	}
	
	private void createFields(List<PersonParser> people) {
		DatabaseHelper helper = new DatabaseHelper(this);
		
		for(PersonParser person: people) {			
			ImportEntity importEntity = new ImportEntity();
			importEntity.setName(person.getName());
			importEntity.setPersonId(person.getPersonId());
			importEntity.setAmountInpirations(person.inspirations.size());
			
			PersonEntity personEntity = helper.getPerson(person.name);
			
			if(personEntity != null) {
				importEntity.setMerged(true);
				if(hasNewInspirations(person, personEntity)) {
					containerImports.addView(ImportInspirationsView_.build(importEntity, this));
				}
			} else {
				containerImports.addView(ImportInspirationsView_.build(importEntity, this));				
			}
		}
		
		helper.close();
	}
	
	/**
	 * Search if there are new inspiration to save
	 * @param personParser Person imported
	 * @param personEntity Person saved
	 * @return if there are new inspiration
	 */
	private boolean hasNewInspirations(PersonParser personParser, PersonEntity personEntity) {
		DatabaseHelper helper = new DatabaseHelper(this);
		for(String importedInspiration: personParser.getInspirations()) {
			List<InspiracaoEntity> userInspirations = helper.getInspirationData(personParser.getPersonId());
			boolean found = false;
			for(InspiracaoEntity inspirationEntity: userInspirations) {				
				if(importedInspiration.equals(inspirationEntity.inspiration)) {
					found = true;
					break;				
				}
			}
			
			if(!found) {
				helper.close();
				return true;
			}
			
		}
		helper.close();
		return false;
	}
	
	@Click(R.id.btn_import_inspirations)
	void addClick() {
		DatabaseHelper helper = new DatabaseHelper(this);
		for (int i = 0; i < containerImports.getChildCount(); i++) {
		    if (((ImportInspirationsView) containerImports.getChildAt(i)).isChecked()) {
		    	ImportEntity importEntity = ((ImportInspirationsView) containerImports.getChildAt(i)).getImportPerson();
		    	if(importEntity.isMerged()) {
		    		
		    	}
		    }
		}
		helper.close();
	}

}
