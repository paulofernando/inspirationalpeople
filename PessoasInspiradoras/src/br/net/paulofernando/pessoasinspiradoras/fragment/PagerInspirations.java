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
import android.graphics.Rect;
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
import android.view.TouchDelegate;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.AddInspirationActivity_;
import br.net.paulofernando.pessoasinspiradoras.EditInspirationActivity;
import br.net.paulofernando.pessoasinspiradoras.EditInspirationActivity_;
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
	
	private ImageView btEdit, btDelete, btShare;

	private List<InspiracaoEntity> listInspirations;
	long personId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pager_inspirations);
		personName = (TextView) findViewById(R.id.person_name_detail_pager);
		medal = (ImageView) findViewById(R.id.medal_selected_person_pager);
		photo = (ImageView) findViewById(R.id.photo_selected_person_pager);
		
		btEdit = (ImageView) findViewById(R.id.bt_edit_inpiration_tab);
		btDelete = (ImageView) findViewById(R.id.bt_delete_inspiration_tab);
		btShare = (ImageView) findViewById(R.id.bt_share_inspiration_tab);

		RelativeLayout parentButtons = (RelativeLayout) findViewById(R.id.inside_menu_inspiration_tab);
		
		parentButtons.post(new Runnable() {
            public void run() {
                // Post in the parent's message queue to make sure the parent
                // lays out its children before we call getHitRect()
                
            	float HSpace = getResources().getDimension(R.dimen.inspiration_buttons_horizontal_space);
                float VSpace = getResources().getDimension(R.dimen.inspiration_buttons_vertical_space);
            	
            	Rect delegateAreaEdit = new Rect();                
                btEdit.getHitRect(delegateAreaEdit);
                
                delegateAreaEdit.top -= VSpace;
                delegateAreaEdit.bottom += VSpace;
                delegateAreaEdit.left -= HSpace;
                delegateAreaEdit.right += HSpace;
                
                Rect delegateAreaDelete = new Rect();                
                btDelete.getHitRect(delegateAreaDelete);
                
                delegateAreaDelete.top -= VSpace;
                delegateAreaDelete.bottom += VSpace;
                delegateAreaDelete.left -= HSpace;
                delegateAreaDelete.right += HSpace;
                
                Rect delegateAreaShare = new Rect();                
                btShare.getHitRect(delegateAreaShare);
                
                delegateAreaShare.top -= VSpace;
                delegateAreaShare.bottom += VSpace;
                delegateAreaShare.left -= HSpace;
                delegateAreaShare.right += HSpace;
                
                TouchDelegate expandedAreaEdit = new TouchDelegate(delegateAreaEdit, btEdit);
                TouchDelegate expandedAreaDelete = new TouchDelegate(delegateAreaDelete, btDelete);
                TouchDelegate expandedAreaShare = new TouchDelegate(delegateAreaShare, btShare);
                
                // give the delegate to an ancestor of the view we're delegating the area to
                if (View.class.isInstance(btEdit.getParent())) {
                    ((View) btEdit.getParent())
                            .setTouchDelegate(expandedAreaEdit);
                }
                
                if (View.class.isInstance(btDelete.getParent())) {
                    ((View) btDelete.getParent())
                            .setTouchDelegate(expandedAreaDelete);
                }
                
                if (View.class.isInstance(btShare.getParent())) {
                    ((View) btShare.getParent())
                            .setTouchDelegate(expandedAreaShare);
                }
            };
        });
		
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
		
		btDelete.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				deleteCurrentInspiration();				
			}
		});
		
		btEdit.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				editCurrentInspiration();
				updateData();				
			}
		});
		
		btShare.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				sharedCurrentInspiration();		
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
	
	private void deleteCurrentInspiration() {
		Utils.showConfirmDialog(this, this.getString(R.string.delete_inspiration_title),
				getString(R.string.delete_inspiration_question),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatabaseHelper helper = new DatabaseHelper(PagerInspirations.this);
						helper.deleteInspirationById(listInspirations.get(viewPager.getCurrentItem()).id);

						viewPager.removeViewAt(viewPager.getCurrentItem());
						PagerInspirations.this.updateData();
						
						helper.close();
					}
				});
	}
	
	private void editCurrentInspiration() {
		Intent intent = new Intent(this, EditInspirationActivity_.class);
		intent.putExtra("idInspiration", listInspirations.get(viewPager.getCurrentItem()).id);
		intent.putExtra("idUser", listInspirations.get(viewPager.getCurrentItem()).idUser);
		intent.putExtra("inspiration", listInspirations.get(viewPager.getCurrentItem()).inspiration);
		startActivityForResult(intent, EditInspirationActivity.EDIT_INSPIRATION);
	}
	
	private void sharedCurrentInspiration() {
		new ImageFromSentence(this).getImageFromSentence(listInspirations.get(viewPager.getCurrentItem()).id, 640, 640);		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == EditInspirationActivity.EDIT_INSPIRATION) {
	        // Inspiration edited
	    	if((data != null) && data.getBooleanExtra("return", true)) {
	    		viewPager.removeViewAt(viewPager.getCurrentItem());
				PagerInspirations.this.updateData();
	        }
	    }
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

	private void updateData() {
		DatabaseHelper helper = new DatabaseHelper(this);
		PersonEntity person = helper.getPerson(personId);
		personName.setText(person.name);
		photo.setImageBitmap(new ImageFromSentence(this)
				.getCroppedBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length)));
		
		loadInspirations();
		tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), listInspirations);		
		viewPager.setAdapter(tabPagerAdapter);
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