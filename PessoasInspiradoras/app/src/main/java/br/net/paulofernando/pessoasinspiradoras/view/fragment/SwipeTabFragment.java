package br.net.paulofernando.pessoasinspiradoras.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.net.paulofernando.pessoasinspiradoras.view.activity.EditInspirationActivity;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.util.ImageFromSentence;
import br.net.paulofernando.pessoasinspiradoras.data.entity.InspiracaoEntity;
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

                        ((PagerInspirationsFragment) getActivity()).updateData();

                        helper.close();
                    }
                });
    }

    private void editCurrentInspiration() {
        Intent intent = new Intent(getActivity(), EditInspirationActivity.class);
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
            if ((data != null) && data.getBooleanExtra("return", true)) {
//	    		((PagerInspirationsFragment)getActivity()).getViewPager().setCurrentItem(2);
            }
        }
    }

    public void setText(String text) {
        tv.setText(text);
    }


}