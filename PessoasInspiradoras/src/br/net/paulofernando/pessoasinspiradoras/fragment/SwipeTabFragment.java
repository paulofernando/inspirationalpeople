package br.net.paulofernando.pessoasinspiradoras.fragment;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.EditInspirationActivity_;
import br.net.paulofernando.pessoasinspiradoras.PersonActivity;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.image.ImageFromSentence;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

import com.googlecode.androidannotations.annotations.Click;

public class SwipeTabFragment extends Fragment {
    
    private TextView tv;
    private RelativeLayout insideMenu;
    
    private InspiracaoEntity inspirationEntity;
    
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
        return view;
    }
   
    
}