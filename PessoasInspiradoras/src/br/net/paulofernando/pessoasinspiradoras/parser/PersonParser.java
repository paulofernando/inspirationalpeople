package br.net.paulofernando.pessoasinspiradoras.parser;

import java.util.ArrayList;
import java.util.List;

public class PersonParser {

	public String name;
    public String id;
    public List<String> inspirations = new ArrayList<String>();
    
    public PersonParser() {}
    
    public void addInspitation(String inspiration) {
    	inspirations.add(inspiration);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getInspirations() {
		return inspirations;
	}

	public void setInspirations(List<String> inspirations) {
		this.inspirations = inspirations;
	}
	
}
