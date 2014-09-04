package br.net.paulofernando.pessoasinspiradoras.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.model.InspiracaoEntity;

public class SwipeTabFragment extends Fragment {
    
    private TextView tv;
    
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
    
    public void setText(String text) {
    	tv.setText(text);
    }
   
    
}