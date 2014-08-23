package br.net.paulofernando.pessoasinspiradoras.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.PersonActivity;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.view_inpiration)
public class InspirationView extends LinearLayout {

	@ViewById(R.id.componentInspirationView)
	RelativeLayout component;
	
	@ViewById(R.id.inspiration)
	TextView inspiration;
	
	@ViewById(R.id.inspiration_marker)
	ImageView marker;

	@ViewById(R.id.inside_menu_inspiration)
	RelativeLayout insideMenu;

	@ViewById(R.id.bt_back_inpiration)
	ImageView btBackInspiration;
	
	@ViewById(R.id.bt_edit_inpiration)
	ImageView btEditInspiration;
	
	@ViewById(R.id.bt_delete_inspiration)
	ImageView btDeleteInspiration;
	
	InspiracaoEntity inspirationEntity;

	private Context context;
	
	public InspirationView(InspiracaoEntity inspirationEntity, Context context) {
		super(context);
		this.context = context;
		this.inspirationEntity = inspirationEntity;
	}

	@AfterViews
	void init() {
		try {
			inspiration.setText(inspirationEntity.inspiration);
			//inspiration.setText(SimpleCrypto.decrypt(Utils.key, inspirationEntity.inspiration));
			this.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					inspiration.setAlpha(0.2f);
					marker.setAlpha(0.2f);
					insideMenu.setVisibility(View.VISIBLE);				
				}
			});
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Click(R.id.bt_back_inpiration) 
	void btBackInspiration() {
		inspiration.setAlpha(1f);
		marker.setAlpha(1f);
		insideMenu.setVisibility(View.GONE);
	}
	
	@Click(R.id.bt_delete_inspiration)
	void btDeleteClicked() {
		deleteInspiration();
	}
	
	@Click(R.id.bt_edit_inpiration)
	void btEditInspiration() {
		final EditText input = new EditText(context);
		input.setText(inspiration.getText().toString());
		input.setId(android.R.id.edit);
		input.setLines(2);
		new AlertDialog.Builder(context)
				.setTitle(context.getString(R.string.edit_inspiration)).setView(input)				
				.setIcon(android.R.drawable.ic_dialog_info)
				.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	try {
	                		DatabaseHelper helper = new DatabaseHelper(context);
	                		helper.updateInspirationById(inspirationEntity.id, input.getText().toString());
	                		inspiration.setText(input.getText().toString());
	                		inspiration.setAlpha(1f);
	                		marker.setAlpha(1f);
	                		insideMenu.setVisibility(View.GONE);
						} catch (Exception e) {
							e.printStackTrace();
						}
	                }
	            }).show();
	}

	private void deleteInspiration() {
		Utils.showConfirmDialog(getContext(), context.getString(R.string.delete_inspiration_title),
				context.getString(R.string.delete_inspiration_question),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatabaseHelper helper = new DatabaseHelper(getContext());
						helper.deleteInspirationById(inspirationEntity.id);
						((PersonActivity) getContext()).loadInspirations();
					}
				});
	}

}
