package br.net.paulofernando.pessoasinspiradoras.model;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import br.net.paulofernando.pessoasinspiradoras.R;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "person")
public class PersonEntity implements Serializable {

	private static final long serialVersionUID = -6347760237875943686L;

	@DatabaseField(id=true)	
	public long id;
	
	@DatabaseField
	public String name;

	@DatabaseField
	public String phone;
	
	int amountInpirations = 0;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	public byte[] photo;
	
	public PersonEntity(String name, long personId, String phone) {	
    	this.name = name;
    	this.id = personId;
    	this.phone = phone;
    }
	
	public PersonEntity() {}
	
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
		if(getAmountInpirations() >= 9) {
			return R.drawable.nine_plus;			
		} else if(getAmountInpirations() >= 6) {
			return R.drawable.six_plus;
		} else if(getAmountInpirations() >= 3) {
			return R.drawable.three_plus;
		} else {
			return -1;
		}
	}
}
