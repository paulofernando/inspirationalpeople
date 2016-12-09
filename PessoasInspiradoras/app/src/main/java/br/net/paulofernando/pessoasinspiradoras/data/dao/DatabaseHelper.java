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
import br.net.paulofernando.pessoasinspiradoras.data.entity.Inspiracao;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "db_mobtur.db";
    private static final int DATABASE_VERSION = 1;

    private Context contex;

    private Dao<Person, String> simplePersonDao = null;
    private Dao<Inspiracao, String> simpleInspirationDao = null;

    private RuntimeExceptionDao<Person, String> simpleRuntimePersonDao = null;
    private RuntimeExceptionDao<Inspiracao, String> simpleRuntimeInspirationDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.contex = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            try {
                TableUtils.createTable(connectionSource, Person.class);
                TableUtils
                        .createTable(connectionSource, Inspiracao.class);
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
                        .dropTable(connectionSource, Person.class, true);
                TableUtils.dropTable(connectionSource, Inspiracao.class,
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

    public List<Person> getPersonsData() {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<Person, String> simpleDao = helper.getPersonSimpleDataDao();
        List<Person> list = simpleDao.queryForAll();
        return list;
    }

    public List<Inspiracao> getInspirationData() {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<Inspiracao, String> simpleDao = helper.getInspirationSimpleDataDao();
        List<Inspiracao> list = simpleDao.queryForAll();
        return list;
    }

    public List<Inspiracao> getInspirationData(long idUser) {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<Inspiracao, String> simpleDao = helper.getInspirationSimpleDataDao();
        List<Inspiracao> list = simpleDao.queryForAll();
        List<Inspiracao> userInspiration = new ArrayList<Inspiracao>();
        for (Inspiracao inspiracao : list) {
            if (inspiracao.idUser == idUser) {
                userInspiration.add(inspiracao);
            }
        }
        return userInspiration;
    }

    public Bitmap getPhotoByUserId(long idUser) {
        Person person = getPerson(idUser);
        return BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length);
    }

    public Person getPerson(long id) {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<Person, String> simpleDao = helper.getPersonSimpleDataDao();
        return simpleDao.queryForId(String.valueOf(id));
    }

    public Person getPerson(String name) {
        List<Person> people = getPersonsData();
        for (Person person : people) {
            if (person.name.equals(name)) {
                return person;
            }
        }
        return null;
    }

    public Inspiracao getInspiration(long id) {
        DatabaseHelper helper = new DatabaseHelper(contex);
        RuntimeExceptionDao<Inspiracao, String> simpleDao = helper.getInspirationSimpleDataDao();
        return simpleDao.queryForId(String.valueOf(id));
    }

    public Dao<Person, String> getPersonDao() throws SQLException {
        if (simplePersonDao == null) {
            try {
                simplePersonDao = getDao(Person.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return simplePersonDao;
    }

    public Dao<Inspiracao, String> getInspirationDao() throws SQLException {
        if (simpleInspirationDao == null) {
            try {
                simpleInspirationDao = getDao(Inspiracao.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return simpleInspirationDao;
    }

    public RuntimeExceptionDao<Person, String> getPersonSimpleDataDao() {
        if (simpleRuntimePersonDao == null) {
            simpleRuntimePersonDao = getRuntimeExceptionDao(Person.class);
        }
        return simpleRuntimePersonDao;
    }

    public RuntimeExceptionDao<Inspiracao, String> getInspirationSimpleDataDao() {
        if (simpleRuntimeInspirationDao == null) {
            simpleRuntimeInspirationDao = getRuntimeExceptionDao(Inspiracao.class);
        }
        return simpleRuntimeInspirationDao;
    }

    public boolean deletePersonById(long id) {
        Dao<Person, String> simpleDao = getPersonDao();
        DeleteBuilder<Person, String> deleteBuilder = simpleDao.deleteBuilder();
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
        Dao<Inspiracao, String> simpleInspirationDao = getInspirationDao();
        DeleteBuilder<Inspiracao, String> deleteBuilder = simpleInspirationDao.deleteBuilder();
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
        Dao<Inspiracao, String> simpleInspirationDao = getInspirationDao();
        DeleteBuilder<Inspiracao, String> deleteBuilder = simpleInspirationDao.deleteBuilder();
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
        Dao<Person, String> simpleDao = getPersonDao();
        UpdateBuilder<Person, String> updateBuilder = simpleDao.updateBuilder();
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
        Dao<Person, String> simpleDao = getPersonDao();
        UpdateBuilder<Person, String> updateBuilder = simpleDao.updateBuilder();
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
        Dao<Inspiracao, String> simpleDao = getInspirationDao();
        UpdateBuilder<Inspiracao, String> updateBuilder = simpleDao.updateBuilder();
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
        List<Person> people = getPersonsData();
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
            for (Person person : people) {
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
                List<Inspiracao> inspirations = getInspirationData(person.id);
                if (inspirations.size() > 0) {
                    for (Inspiracao inspiration : inspirations) {
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
