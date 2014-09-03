package br.net.paulofernando.pessoasinspiradoras.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.net.paulofernando.pessoasinspiradoras.R;

public class SwipeTabFragment extends Fragment {

    private String tab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        tab = bundle.getString("tab");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_inspirations_tab, null);
        TextView tv = (TextView) view.findViewById(R.id.tab_inspirations);
        tv.setText(tab);
        return view;
    }
}