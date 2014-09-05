package br.net.paulofernando.pessoasinspiradoras.fragment;

import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.AddInspirationActivity_;
import br.net.paulofernando.pessoasinspiradoras.EditInspirationActivity;
import br.net.paulofernando.pessoasinspiradoras.EditPersonActivity_;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.SettingsActivity;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.image.ImageFromSentence;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

import com.viewpagerindicator.CirclePageIndicator;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PagerInspirations extends FragmentActivity implements
		ActionBar.TabListener {
	
	private ViewPager viewPager;
	private TextView personName;
	private ImageView medal, photo;
	private TabPagerAdapter tabPagerAdapter;
	
	private List<InspiracaoEntity> listInspirations;
	long personId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pager_inspirations);
		personName = (TextView) findViewById(R.id.person_name_detail_pager);
		medal = (ImageView) findViewById(R.id.medal_selected_person_pager);
		photo = (ImageView) findViewById(R.id.photo_selected_person_pager);
		
		personId = getIntent().getLongExtra("id", -1);
		personName.setText(getIntent().getStringExtra("name"));
		
		photo.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				editPersonData();
			}
		});
		
		personName.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				editPersonData();
			}
		});
				
		loadInspirations();

		tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),
				listInspirations);

		viewPager = (ViewPager) findViewById(R.id.pager_inspirations);
		viewPager.setAdapter(tabPagerAdapter);

		CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(viewPager);

	}

	private void editPersonData() {
		Intent intent = new Intent(this, EditPersonActivity_.class);
		intent.putExtra("name", getIntent().getStringExtra("name"));
		intent.putExtra("photo", getIntent().getByteArrayExtra("photo"));
		intent.putExtra("id", personId);
		startActivity(intent);
	}
		
	public void loadInspirations() {
		DatabaseHelper helper = new DatabaseHelper(this);
		listInspirations = helper.getInspirationData(personId);
		helper.close();
	}

	private void updateMedal(int amountInspirations) {
		if (amountInspirations >= 9) {
			medal.setImageDrawable(getResources().getDrawable(
					R.drawable.nine_plus_white));
			medal.setVisibility(View.VISIBLE);
		} else if (amountInspirations >= 6) {
			medal.setImageDrawable(getResources().getDrawable(
					R.drawable.six_plus_white));
			medal.setVisibility(View.VISIBLE);
		} else if (amountInspirations >= 3) {
			medal.setImageDrawable(getResources().getDrawable(
					R.drawable.three_plus_white));
			medal.setVisibility(View.VISIBLE);
		} else {
			medal.setVisibility(View.INVISIBLE);
		}
	}

	public void updateData() {
		DatabaseHelper helper = new DatabaseHelper(this);
		PersonEntity person = helper.getPerson(personId);
		personName.setText(person.name);
		photo.setImageBitmap(new ImageFromSentence(this)
				.getCroppedBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length)));
		
		int currentPage = viewPager.getCurrentItem();
		int sizeBeforeUpdate = listInspirations.size(); //listInspiration is updated in the loadInspirations
		loadInspirations();
				
		tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), listInspirations);		
		viewPager.setAdapter(tabPagerAdapter);
		
		if(sizeBeforeUpdate == listInspirations.size()) { //edited
			viewPager.setCurrentItem(currentPage);
		} else if(sizeBeforeUpdate > listInspirations.size()) { //deleted
			if(currentPage > 0) {
				viewPager.setCurrentItem(currentPage - 1);
			}
		} else { //added
			if(listInspirations.size() > 0) {
				viewPager.setCurrentItem(listInspirations.size() - 1);
			}
		}
		
		updateMedal(listInspirations.size());
		
		helper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_person, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_inspiration:
			addInspiration();
			return true;
		case R.id.menu_delete_person:
			deletePerson();
			return true;
		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	void deletePerson() {
		Utils.showConfirmDialog(this, getString(R.string.delete_contact_title),
				getString(R.string.delete_contact_question),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if ((PreferenceManager.getDefaultSharedPreferences(
								PagerInspirations.this).getString(
								SettingsActivity.PREF_KEY, "").equals(""))) {
							deletePersonData();
							finish();
						} else {
							final EditText input = new EditText(PagerInspirations.this);
							input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
							input.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
							input.setId(android.R.id.edit);
							input.setLines(1);
							new AlertDialog.Builder(PagerInspirations.this)
									.setTitle(getString(R.string.enter_password))
									.setView(input)
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setNeutralButton(
											android.R.string.ok,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													try {
														if (input.getText().toString().equals(PreferenceManager
																		.getDefaultSharedPreferences(PagerInspirations.this)
																		.getString(SettingsActivity.PREF_KEY,null))) {
															deletePersonData();
															finish();
														} else {
															Utils.showErrorDialog(PagerInspirations.this,
																	getResources().getString(R.string.error),
																	getResources().getString(R.string.wrong_password));
														}

													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											}).show();
						}
					}
				});

	}

	private void deletePersonData() {
		DatabaseHelper helper = new DatabaseHelper(PagerInspirations.this);
		helper.deletePersonById(personId);
		helper.deleteAllInspirationsByUserId(personId);
		helper.close();
	}

	void addInspiration() {
		Intent i = new Intent(this, AddInspirationActivity_.class);
		i.putExtra("id", personId);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		updateData();		
		super.onResume();
	}
	
	public ViewPager getViewPager() {
		return viewPager;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}