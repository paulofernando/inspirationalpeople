package br.net.paulofernando.pessoasinspiradoras;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.activity_import_inspirations)
public class ImportInspirationsActivity extends ActionBarActivity {
		
	@ViewById(R.id.layout_import)
	LinearLayout containerImports;
	
	@ViewById(R.id.btn_import_inspirations)
	Button btnImport;
	
	@ViewById(R.id.date_backup)
	TextView lastModifiedView;

	private DtoFactory dtoFactory;
	List<PersonParser> importedPeople;
	
	List<String> duplicatedInspirationsToDelete = new ArrayList<String>();
	
	@AfterViews
	protected void init() {
		dtoFactory = (DtoFactory) getApplication();
		loadImports();
		
		Date lastModified = new Date(Utils.getBackupLastModified(this));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		String formattedDate = sdf.format(lastModified);
		lastModifiedView.setText(this.getResources().getString(R.string.date_backup) + " " +
				formattedDate);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void loadImports() {
		containerImports.removeAllViews();
		DatabaseHelper helper = new DatabaseHelper(this);
		importedPeople = Utils.importPeopleFromXML(this);
		createFields(importedPeople);		
		helper.close();
		
		if(containerImports.getChildCount() == 0) {
			Utils.showAlertDialog(this, this.getResources().getString(R.string.warning),
					this.getResources().getString(R.string.no_data_to_import));
			btnImport.setEnabled(false);
		}
		
	}
	
	private void createFields(List<PersonParser> people) {
		DatabaseHelper helper = new DatabaseHelper(this);
		
		for(PersonParser person: people) {			
			ImportEntity importEntity = new ImportEntity();
			importEntity.setName(person.getName());
			importEntity.setPersonId(person.getPersonId());
						
			PersonEntity personEntity = helper.getPerson(person.name);
			
			if(personEntity != null) {
				importEntity.setMerged(true);
				if(hasNewInspirations(person, personEntity)) {
					person.inspirations.removeAll(duplicatedInspirationsToDelete);
					importEntity.setAmountInpirations(person.inspirations.size());
					containerImports.addView(ImportInspirationsView_.build(importEntity, this));					
				}
			} else {
				person.inspirations.removeAll(duplicatedInspirationsToDelete);
				importEntity.setAmountInpirations(person.inspirations.size());
				containerImports.addView(ImportInspirationsView_.build(importEntity, this));
			}
			
			
			duplicatedInspirationsToDelete.clear();
		}
		
		
		
		helper.close();
	}
	
	/**
	 * Search if there are new inspiration to save and delete the duplicated inspirations
	 * @param personParser Person imported
	 * @param personEntity Person saved
	 * @return if there are new inspiration
	 */
	private boolean hasNewInspirations(PersonParser personParser, PersonEntity personEntity) {
		DatabaseHelper helper = new DatabaseHelper(this);
				
		List<InspiracaoEntity> userInspirations = helper.getInspirationData(personEntity.id);
		
		boolean newInspiration = false;
		int countFound = 0;
		for(String importedInspiration: personParser.getInspirations()) {
			for(InspiracaoEntity inspirationEntity: userInspirations) {				
				if(importedInspiration.equals(inspirationEntity.inspiration)) {
					/* Put the inspiration in a List to delete later */
					duplicatedInspirationsToDelete.add(importedInspiration);
					countFound++;
					break;
				}
			}
		}
		
		if(countFound < personParser.getInspirations().size()) {
			newInspiration = true;
		}
		
		helper.close();
		if(newInspiration) {
			return true;
		} else {		
			return false;
		}
	}
	
	@Click(R.id.btn_import_inspirations)
	void addClick() {
		DatabaseHelper helper = new DatabaseHelper(this);
		
		for (int i = 0; i < containerImports.getChildCount(); i++) {			
		    if (((ImportInspirationsView) containerImports.getChildAt(i)).isChecked()) {
		    	ImportEntity importEntity = ((ImportInspirationsView) containerImports.getChildAt(i)).getImportPerson();
		    			    		
	    		long personId;
	    		
	    		if(importEntity.isMerged()) {
	    			//get the id of the person with the same name of the imported data
	    			personId = helper.getPerson(importEntity.getName()).id;
	    		} else {
	    			personId = createPerson(importEntity);
	    		}
	    		
	    		if(personId != -1) {
		    		PersonParser personToImport = getPersonToImportByName(importEntity.getName());
		    		//---------------- Adding inspirations ---------------------
		    		for(String inspiration: personToImport.inspirations) {
		    			InspiracaoEntity inspirationEntity = new InspiracaoEntity();
		    			inspirationEntity.inspiration = inspiration;
		    			inspirationEntity.idUser = personId;
		    					
		    			Dao<InspiracaoEntity, Integer> iDao = dtoFactory.getInspirationDao();    	
		    	    	try {
		    				iDao.create(inspirationEntity);
		    			} catch (SQLException e) {
		    				e.printStackTrace();
		    			}
		    		}
	    		}	
		    	
		    }
		}
		helper.close();
		finish();
	}
	
	/**
	 * Creates and save a new person data int the database
	 * @param importEntity Imported data
	 * @return the id of the person or -1 if a error occurred
	 */
	private long createPerson(ImportEntity importEntity) {
		Dao<PersonEntity, Integer> pDao = dtoFactory.getPersonDao();		    		
		PersonEntity person;
		long personId = Calendar.getInstance().getTimeInMillis();
		try {			
			person = new PersonEntity(importEntity.getName(), personId, "");
			
			Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.person);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			person.setPhoto(byteArray);			
			
			try {
				pDao.create(person);
				return personId;
			} catch (SQLException e) {
				e.printStackTrace();
				Log.e("AddPerson", "Error on addPerson");				
			}			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return -1;
	}
	
	private PersonParser getPersonToImportByName(String personName) {
		for(PersonParser pp: importedPeople) {
			if(pp.name.equals(personName))
				return pp;
		}
		return null;
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
