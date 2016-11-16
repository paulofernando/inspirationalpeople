package br.net.paulofernando.pessoasinspiradoras.data.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.entity.InspiracaoEntity;
import br.net.paulofernando.pessoasinspiradoras.data.entity.PersonEntity;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "db_mobtur.db";
    private static final int DATABASE_VERSION = 1;

    private Context contex;

    private Dao<PersonEntity, String> simplePersonDao = null;
    private Dao<InspiracaoEntity, String> simpleInspirationDao = null;

    private RuntimeExceptionDao<PersonEntity, String> simpleRuntimePersonDao = null;
    private RuntimeExceptionDao<InspiracaoEntity, String> simpleRuntimeInspirationDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.contex = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            try {
                TableUtils.createTable(connectionSource, PersonEntity.class);
                TableUtils
                        .createTable(connectionSource, InspiracaoEntity.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                Log.e("DataseHelper", "Erro on onCreate");
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            try {
                TableUtils
                        .dropTable(connectionSource, PersonEntity.class, true);
                TableUtils.dropTable(connectionSource, InspiracaoEntity.class,
                        true);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                Log.e("DataseHelper", "Erro on onUpgrade");
            }
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public List<PersonEntity> getPersonsData() {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<PersonEntity, String> simpleDao = helper.getPersonSimpleDataDao();
        List<PersonEntity> list = simpleDao.queryForAll();
        return list;
    }

    public List<InspiracaoEntity> getInspirationData() {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<InspiracaoEntity, String> simpleDao = helper.getInspirationSimpleDataDao();
        List<InspiracaoEntity> list = simpleDao.queryForAll();
        return list;
    }

    public List<InspiracaoEntity> getInspirationData(long idUser) {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<InspiracaoEntity, String> simpleDao = helper.getInspirationSimpleDataDao();
        List<InspiracaoEntity> list = simpleDao.queryForAll();
        List<InspiracaoEntity> userInspiration = new ArrayList<InspiracaoEntity>();
        for (InspiracaoEntity inspiracao : list) {
            if (inspiracao.idUser == idUser) {
                userInspiration.add(inspiracao);
            }
        }
        return userInspiration;
    }

    public Bitmap getPhotoByUserId(long idUser) {
        PersonEntity person = getPerson(idUser);
        return BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length);
    }

    public PersonEntity getPerson(long id) {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<PersonEntity, String> simpleDao = helper.getPersonSimpleDataDao();
        return simpleDao.queryForId(String.valueOf(id));
    }

    public PersonEntity getPerson(String name) {
        List<PersonEntity> people = getPersonsData();
        for (PersonEntity person : people) {
            if (person.name.equals(name)) {
                return person;
            }
        }
        return null;
    }

    public InspiracaoEntity getInspiration(long id) {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<InspiracaoEntity, String> simpleDao = helper.getInspirationSimpleDataDao();
        return simpleDao.queryForId(String.valueOf(id));
    }

    public Dao<PersonEntity, String> getPersonDao() throws SQLException {
        if (simplePersonDao == null) {
            try {
                simplePersonDao = getDao(PersonEntity.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return simplePersonDao;
    }

    public Dao<InspiracaoEntity, String> getInspirationDao() throws SQLException {
        if (simpleInspirationDao == null) {
            try {
                simpleInspirationDao = getDao(InspiracaoEntity.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return simpleInspirationDao;
    }

    public RuntimeExceptionDao<PersonEntity, String> getPersonSimpleDataDao() {
        if (simpleRuntimePersonDao == null) {
            simpleRuntimePersonDao = getRuntimeExceptionDao(PersonEntity.class);
        }
        return simpleRuntimePersonDao;
    }

    public RuntimeExceptionDao<InspiracaoEntity, String> getInspirationSimpleDataDao() {
        if (simpleRuntimeInspirationDao == null) {
            simpleRuntimeInspirationDao = getRuntimeExceptionDao(InspiracaoEntity.class);
        }
        return simpleRuntimeInspirationDao;
    }

    public boolean deletePersonById(long id) {
        Dao<PersonEntity, String> simpleDao = getPersonDao();
        DeleteBuilder<PersonEntity, String> deleteBuilder = simpleDao.deleteBuilder();
        try {
            deleteBuilder.where().eq("id", id);
            deleteBuilder.delete();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseHekper", "Error on delete person");
            return false;
        }
        return true;
    }

    public boolean deleteInspirationById(long id) {
        Dao<InspiracaoEntity, String> simpleInspirationDao = getInspirationDao();
        DeleteBuilder<InspiracaoEntity, String> deleteBuilder = simpleInspirationDao.deleteBuilder();
        try {
            deleteBuilder.where().eq("id", id);
            deleteBuilder.delete();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseHekper", "Error on delete inspiration");
            return false;
        }
        return true;
    }

    public boolean deleteAllInspirationsByUserId(long id) {
        Dao<InspiracaoEntity, String> simpleInspirationDao = getInspirationDao();
        DeleteBuilder<InspiracaoEntity, String> deleteBuilder = simpleInspirationDao.deleteBuilder();
        try {
            deleteBuilder.where().eq("idUser", id);
            deleteBuilder.delete();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseHekper", "Error on delete all inspiration");
            return false;
        }
        return true;
    }

    public boolean updatePersonById(long id, String name, byte[] photo) {
        Dao<PersonEntity, String> simpleDao = getPersonDao();
        UpdateBuilder<PersonEntity, String> updateBuilder = simpleDao.updateBuilder();
        try {
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue("name", name);
            updateBuilder.updateColumnValue("photo", photo);

            updateBuilder.update();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseHekper", "Error on update person");
            return false;
        }
        return true;
    }

    public boolean updatePersonById(long id, String name) {
        Dao<PersonEntity, String> simpleDao = getPersonDao();
        UpdateBuilder<PersonEntity, String> updateBuilder = simpleDao.updateBuilder();
        try {
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue("name", name);
            updateBuilder.update();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseHekper", "Error on update person");
            return false;
        }
        return true;
    }

    public boolean updateInspirationById(long id, String newInspiration) {
        Dao<InspiracaoEntity, String> simpleDao = getInspirationDao();
        UpdateBuilder<InspiracaoEntity, String> updateBuilder = simpleDao.updateBuilder();
        try {
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue("inspiration", newInspiration);
            updateBuilder.update();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseHekper", "Error on update inspiration");
            return false;
        }
        return true;
    }

    public String backup() {
        //--------------- loading data -------------------
        List<PersonEntity> people = getPersonsData();
        //------------------------------------------------

        if (people.size() == 0) {
            Utils.showAlertDialog(contex, contex.getResources().getString(R.string.warning),
                    contex.getResources().getString(R.string.no_data_backup));
            return null;
        }

        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            xmlSerializer.startTag("", "backup");
            for (PersonEntity person : people) {
                xmlSerializer.startTag("", "person");
                xmlSerializer.startTag("", "id");
                xmlSerializer.text(String.valueOf(person.id));
                xmlSerializer.endTag("", "id");

                xmlSerializer.startTag("", "name");
                xmlSerializer.text(String.valueOf(person.name));
                xmlSerializer.endTag("", "name");

                xmlSerializer.startTag("", "photo");
                xmlSerializer.text(new String(Base64.encode(person.photo, Base64.DEFAULT)));
                xmlSerializer.endTag("", "photo");
                List<InspiracaoEntity> inspirations = getInspirationData(person.id);
                if (inspirations.size() > 0) {
                    for (InspiracaoEntity inspiration : inspirations) {
                        xmlSerializer.startTag("", "inspiration");
                        xmlSerializer.attribute("", "id", String.valueOf(inspiration.id));
                        xmlSerializer.text(inspiration.inspiration);
                        xmlSerializer.endTag("", "inspiration");
                    }
                }
                xmlSerializer.endTag("", "person");
            }
            xmlSerializer.endTag("", "backup");
            xmlSerializer.endDocument();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }

    @Override
    public void close() {
        super.close();
    }
}
