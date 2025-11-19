package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.backup.Backup;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.databinding.ActivityMainBinding;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PersonListFragment;

public class DashboardActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    PersonListFragment personListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        personListFragment = (PersonListFragment) getSupportFragmentManager().findFragmentById(R.id.person_fragment);
    }

    void addPerson() {
        Intent intent = new Intent(this, AddPersonActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        personListFragment.syncList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.add_person));
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Backup backup;
        int itemId = item.getItemId();
        if (itemId == R.id.menu_add_person) {
            addPerson();
            return true;
        } else if (itemId == R.id.menu_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (itemId == R.id.menu_backup) {
            DatabaseHelper helper = new DatabaseHelper(this);
            backup = new Backup(this);
            backup.saveLocalBackupInFile(this, helper.backup());
            helper.close();
            return true;
        } else if (itemId == R.id.menu_restore) {
            backup = new Backup(this);
            if (backup.hasLocalBackup()) {
                Intent i2 = new Intent(this, ImportInspirationsActivity.class);
                startActivity(i2);
            } else {
                Utils.showAlertDialog(this, this.getResources().getString(R.string.file_not_found),
                        this.getResources().getString(R.string.backup_not_found));
            }
            return true;
        } else if (itemId == R.id.menu_about) {
            Intent i3 = new Intent(this, AboutActivity.class);
            startActivity(i3);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

}
