package br.net.paulofernando.pessoasinspiradoras.data.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import br.net.paulofernando.pessoasinspiradoras.R;

@DatabaseTable(tableName = "person")
public class Person implements Serializable {

    private static final long serialVersionUID = -6347760237875943686L;

    @DatabaseField(id = true)
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String phone;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    public byte[] photo;
    int amountInpirations = 0;

    public Person(String name, long personId, String phone) {
        this.name = name;
        this.id = personId;
        this.phone = phone;
    }

    public Person() {
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public int getAmountInpirations() {
        return amountInpirations;
    }

    public void setAmountInpirations(int amountInpirations) {
        this.amountInpirations = amountInpirations;
    }

    public int getMedal() {
        if (getAmountInpirations() >= 9) {
            return R.drawable.nine_plus;
        } else if (getAmountInpirations() >= 6) {
            return R.drawable.six_plus;
        } else if (getAmountInpirations() >= 3) {
            return R.drawable.three_plus;
        } else {
            return -1;
        }
    }
}
