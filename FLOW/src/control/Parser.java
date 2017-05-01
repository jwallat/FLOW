package control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import model.Activity;
import model.Document;
import model.Edge;
import model.Network;
import model.Person;
import model.Vertex;

public class Parser extends DefaultHandler {

	private InputSource inputSource;
	
	private File file;
	private Network network;
	
	private List<String> errorLog;
	private boolean showLog = true;
	
	public Parser(File file){
		try {
			this.file = file;
			FileReader fr = new FileReader(file);
			inputSource = new InputSource(fr);
		} 
		catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
		network = new Network();
		errorLog = new ArrayList<String>();
	}
	
	public void parse() {
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();

			xmlReader.setContentHandler(this);
			
			System.out.println("************************* PARSER **************************");
			System.out.println("***********************************************************");
			xmlReader.parse(inputSource);
			System.out.println("***********************************************************");
			System.out.println("*********************** PARSER ENDE ***********************\n\n");	
			
			if (showLog) {
				for (int i = 0; i < errorLog.size(); i++) {
					System.out.println(errorLog.get(i));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("\nEs gab ein Problem beim parsen der datei: " + file.getName() + ", Program wird beendet");
			System.out.println("Bitte Halten Sie sich an das vorgegebene Schema");
			System.exit(0);
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		if (localName.equals("person")) {
			createMember(uri, localName, qName, atts);
			return;
		}
		if (localName.equals("edge")) {
			createEdge(uri, localName, qName, atts);
			return;
		}
	}
	
	private void createMember(String uri, String localName, String qName, Attributes atts) {
		Vertex newElement = null;
		String name = atts.getValue("name"); 
		int id = Integer.parseInt(atts.getValue("id"));
		int x = Integer.parseInt(atts.getValue("x"));
		int y = Integer.parseInt(atts.getValue("y"));
		String type = atts.getValue("type");
		if (atts.getValue("name") != null) {
			if (atts.getValue("id") != null) {
				if (atts.getValue("x") != null) {
					if (atts.getValue("y") != null) {
						if (type == "person") {
							newElement = new Person(name, id, x, y);
						}
						else if (type == "document") {
							newElement = new Document(name, id, x, y);
						}
						else if (type == "activity") {
							newElement = new Activity(name, id, x , y);
						}
						else {
							errorLog.add("Kein Typ bei: " + localName + ", " + name);
							//System.out.println("Typ nicht erkannt bei: " + localName + ", " + name);
						}
					}
					else {
						errorLog.add("Kein Y-Wert bei: " + localName + ", " + name);
					}
				}
				else {
					errorLog.add("Kein X-Wert bei: " + localName + ", " + name);
				}
			}
			else {
				errorLog.add("Kein id-Wert bei: " + localName + ", " + name);
			}
		}
		else {
			errorLog.add("Kein name-Wert bei: " + localName + ", " + name);
		}
		
		if (newElement != null) {
			network.addVertex(newElement);
			System.out.println("Vertex hinzugefügt: " + newElement);
		}
		else {
			errorLog.add("Kein Member hinzugefügt: " + localName + ", " + name);
		}
	}
	
	private void createEdge(String uri, String localName, String qName, Attributes atts) {
		
		Edge edge = null;
		Vertex from = network.getVertexByName(atts.getValue("from"));
		Vertex to = network.getVertexByName(atts.getValue("to"));
		int id = Integer.parseInt(atts.getValue("id"));
		
		if (atts.getValue("id") != null) {
			if (atts.getValue("from") != null) {
				if (atts.getValue("to") != null) {
					edge = new Edge(id, from, to, -1);
				}
				else {
					errorLog.add("Kein to-Wert bei: " + localName + ", ID: " + id);
				}
			}
			else {
				errorLog.add("Kein from-Wert bei: " + localName + ", ID: " + id);
			}
		}
		else {
			errorLog.add("Kein id-Wert bei Edge: " + localName + ", ID: " + id);
		}
		
		if (edge != null) {
			network.addEdge(edge);
			System.out.println("Edge hinzugefügt: " + edge);
		}
		else {
			errorLog.add("Keine Edge hinzugefügt: " + localName + ", ID: " + id);
		}
	}
	
	public Network getData() {
		return this.network;
	}
}
