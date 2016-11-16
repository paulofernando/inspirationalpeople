package br.net.paulofernando.pessoasinspiradoras.data.entity;

import java.io.Serializable;

public class ImportEntity implements Serializable {

    private static final long serialVersionUID = 4286995530273921037L;

    private String name;
    private int amountInpirations = 0;
    private boolean merged = false;
    private long personId;
    private byte[] photo;

    public ImportEntity(String name, long personId) {
        this.name = name;
        this.personId = personId;
    }

    public ImportEntity() {
    }

    public int getAmountInpirations() {
        return amountInpirations;
    }

    public void setAmountInpirations(int amountInpirations) {
        this.amountInpirations = amountInpirations;
    }

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }


}
