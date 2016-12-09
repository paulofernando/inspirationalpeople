package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import br.net.paulofernando.pessoasinspiradoras.R;

public class AboutActivity extends Activity {

    private static String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = "v. " + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(getString(R.string.version) + " " + version);
    }

}
