package br.net.paulofernando.pessoasinspiradoras.data.dao;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import br.net.paulofernando.pessoasinspiradoras.data.entity.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.data.entity.PersonEntity;

public class DtoFactory extends Application {

    private SharedPreferences preferences;
    private DatabaseHelper databaseHelper = null;

    private Dao<PersonEntity, Integer> personDAO = null;
    private Dao<InspiracaoEntity, Integer> inspirationDAO = null;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        databaseHelper = new DatabaseHelper(this);
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public Dao<PersonEntity, Integer> getPersonDao() throws SQLException {
        if (personDAO == null) {
            try {
                personDAO = databaseHelper.getDao(PersonEntity.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                Log.e("DtaFactory", "Erro on getPersonDao()");
            }
        }
        return personDAO;
    }

    public Dao<InspiracaoEntity, Integer> getInspirationDao() throws SQLException {
        if (inspirationDAO == null) {
            try {
                inspirationDAO = databaseHelper.getDao(InspiracaoEntity.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                Log.e("DtaFactory", "Erro on getInspirationDao()");
            }
        }
        return inspirationDAO;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
} 