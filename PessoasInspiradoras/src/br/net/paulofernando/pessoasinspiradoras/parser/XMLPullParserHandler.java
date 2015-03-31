package br.net.paulofernando.pessoasinspiradoras.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Base64;

public class XMLPullParserHandler {
	
	List<PersonParser> people;
    private PersonParser person;
    private String text;
 
    public XMLPullParserHandler() {
    	people = new ArrayList<PersonParser>();
    }
 
    public List<PersonParser> getEmployees() {
        return people;
    }
 
    public List<PersonParser> parse(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null; 
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
 
            parser.setInput(is, null);
 
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagname.equalsIgnoreCase("person")) {
                    	person = new PersonParser();
                    }
                    break;
 
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
 
                case XmlPullParser.END_TAG:
                    if (tagname.equalsIgnoreCase("person")) {
                        people.add(person);
                    } else if (tagname.equalsIgnoreCase("inspiration")) {
                        person.addInspitation(text);
                    } else if (tagname.equalsIgnoreCase("id")) {
                    	person.setPersonId(text);
                    } else if (tagname.equalsIgnoreCase("name")) {
                    	person.setName(text);
                    } else if (tagname.equalsIgnoreCase("photo")) {
                    	person.setPhoto(Base64.decode(text.getBytes(), Base64.DEFAULT));
                    }
                    
                    break;
 
                default:
                    break;
                }
                eventType = parser.next();
            }
 
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        return people;
    }

}

