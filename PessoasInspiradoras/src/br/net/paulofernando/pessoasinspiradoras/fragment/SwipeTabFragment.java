package br.net.paulofernando.pessoasinspiradoras.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.EditInspirationActivity;
import br.net.paulofernando.pessoasinspiradoras.EditInspirationActivity_;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.image.ImageFromSentence;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

public class SwipeTabFragment extends Fragment {
    
    private TextView tv;
    
    private InspiracaoEntity inspirationEntity;
    private ImageView btEdit, btDelete, btShare;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        inspirationEntity = new InspiracaoEntity();
        
        inspirationEntity.inspiration = bundle.getString("inspiration");
        inspirationEntity.id = bundle.getLong("id");
        inspirationEntity.idUser = bundle.getLong("idUser");        
    } 

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_inspirations_tab, null);
        tv = (TextView) view.findViewById(R.id.tab_inspirations);                
        tv.setText(inspirationEntity.inspiration);
        
        btEdit = (ImageView) view.findViewById(R.id.bt_edit_inpiration_intern);
		btDelete = (ImageView) view.findViewById(R.id.bt_delete_inspiration_intern);
		btShare = (ImageView) view.findViewById(R.id.bt_share_inspiration_intern);

		RelativeLayout parentButtons = (RelativeLayout) view.findViewById(R.id.inside_menu_inspiration_intern);
		
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
			}
		});
		
		btShare.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				sharedCurrentInspiration();		
			}
		});

        return view;
    }
	
	private void deleteCurrentInspiration() {
		Utils.showConfirmDialog(getActivity(), this.getString(R.string.delete_inspiration_title),
				getString(R.string.delete_inspiration_question),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatabaseHelper helper = new DatabaseHelper(SwipeTabFragment.this.getActivity());
						helper.deleteInspirationById(inspirationEntity.id);
						
						((PagerInspirations)getActivity()).updateData();
						
						helper.close();
					}
				});
	}
	
	private void editCurrentInspiration() {
		Intent intent = new Intent(getActivity(), EditInspirationActivity_.class);
		intent.putExtra("idInspiration", inspirationEntity.id);
		intent.putExtra("idUser", inspirationEntity.idUser);
		intent.putExtra("inspiration", inspirationEntity.inspiration);
		startActivityForResult(intent, EditInspirationActivity.EDIT_INSPIRATION);
	}
	
	private void sharedCurrentInspiration() {
		new ImageFromSentence(getActivity()).getImageFromSentence(inspirationEntity.id, 640, 640);		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == EditInspirationActivity.EDIT_INSPIRATION) {
	        // Inspiration edited
	    	if((data != null) && data.getBooleanExtra("return", true)) {
//	    		viewPager.removeViewAt(viewPager.getCurrentItem());
//				PagerInspirations.this.updateData();
	        }
	    }
	}
    
    public void setText(String text) {
    	tv.setText(text);
    }
   
    
}