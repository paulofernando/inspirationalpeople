package br.net.paulofernando.pessoasinspiradoras;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class About extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(getString(R.string.version) + " " + getString(R.string.version_number));
    }

}
