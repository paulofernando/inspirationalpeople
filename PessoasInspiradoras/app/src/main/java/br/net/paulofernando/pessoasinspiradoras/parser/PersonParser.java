package br.net.paulofernando.pessoasinspiradoras.parser;

import java.util.ArrayList;
import java.util.List;

public class PersonParser {

    /**
     * Person's name
     */
    public String name;
    /**
     * Person's id
     */
    public long id;
    /**
     * Person's inspirations
     */
    public List<String> inspirations = new ArrayList<String>();

    public byte[] photo;

    public PersonParser() {
    }

    public void addInspitation(String inspiration) {
        inspirations.add(inspiration);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPersonId() {
        return id;
    }

    public void setPersonId(String id) {
        this.id = Long.parseLong(id);
    }

    public List<String> getInspirations() {
        return inspirations;
    }

    public void setInspirations(List<String> inspirations) {
        this.inspirations = inspirations;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

}
