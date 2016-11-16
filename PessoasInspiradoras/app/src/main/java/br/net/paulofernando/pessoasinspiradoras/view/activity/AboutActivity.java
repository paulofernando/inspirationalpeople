package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import br.net.paulofernando.pessoasinspiradoras.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(getString(R.string.version) + " " + getString(R.string.version_number));
    }

}
