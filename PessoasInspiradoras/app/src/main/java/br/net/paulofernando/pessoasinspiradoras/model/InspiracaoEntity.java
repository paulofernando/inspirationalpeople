package br.net.paulofernando.pessoasinspiradoras.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "inspiration")
public class InspiracaoEntity implements Serializable {

    private static final long serialVersionUID = 7495903875821643211L;

    @DatabaseField(generatedId = true)
    public Long id;

    @DatabaseField
    public String inspiration;

    @DatabaseField
    public Long idUser;

    public InspiracaoEntity() {
    }

}
