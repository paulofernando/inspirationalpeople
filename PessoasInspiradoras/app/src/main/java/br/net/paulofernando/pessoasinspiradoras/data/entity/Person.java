package br.net.paulofernando.pessoasinspiradoras.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import br.net.paulofernando.pessoasinspiradoras.R;

@DatabaseTable(tableName = "person")
public class Person implements Parcelable, Serializable {

    private static final long serialVersionUID = -6347760237875943686L;

    @DatabaseField(id = true)
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String phone;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    public byte[] photo;
    private int amountInpirations = 0;

    public Person(String name, long personId, String phone) {
        this.name = name;
        this.id = personId;
        this.phone = phone;
    }

    public Person() {}

    protected Person(Parcel in) {
        id = in.readLong();
        name = in.readString();
        phone = in.readString();
        photo = in.createByteArray();
        amountInpirations = in.readInt();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeByteArray(photo);
        parcel.writeInt(amountInpirations);
    }
}
