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
			
			System.out.println("log counter: " + errorLog.size());
			if (showLog) {
				for (int i = 0; i < errorLog.size(); i++) {
					System.out.println("i: " + i + ", " + errorLog.get(i));
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
		
		if (localName.equals("member")) {
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
		
		if (atts.getValue("name") != "") {
			if (atts.getValue("id") != "") {
				if (atts.getValue("x") != "") {
					if (atts.getValue("y") != "") {
						if (atts.getValue("type").equals("person")) {
							newElement = new Person(atts.getValue("name"), Integer.parseInt(atts.getValue("id")), Integer.parseInt(atts.getValue("x")), Integer.parseInt(atts.getValue("y")));
						}
						else if (atts.getValue("type").equals("document")) {
							newElement = new Document(atts.getValue("name"), Integer.parseInt(atts.getValue("id")), Integer.parseInt(atts.getValue("x")), Integer.parseInt(atts.getValue("y")));
						}
						else if (atts.getValue("type").equals("activity")) {
							newElement = new Activity(atts.getValue("name"), Integer.parseInt(atts.getValue("id")), Integer.parseInt(atts.getValue("x")), Integer.parseInt(atts.getValue("y")));
						}
						else {
							errorLog.add(("Kein Typ bei: " + localName + ", " + atts.getValue("name")));
						}
					}
					else {
						errorLog.add("Kein Y-Wert bei: " + localName + ", " + atts.getValue("name"));
					}
				}
				else {
					errorLog.add("Kein X-Wert bei: " + localName + ", " + atts.getValue("name"));
				}
			}
			else {
				errorLog.add("Kein id-Wert bei: " + localName + ", " + atts.getValue("name"));
			}
		}
		else {
			errorLog.add("Kein name-Wert bei: " + localName + ", " + atts.getValue("name"));
		}
		
		if (newElement != null) {
			network.addVertex(newElement);
			System.out.println("Vertex hinzugefügt: " + newElement);
		}
		else {
			errorLog.add("Kein Member hinzugefügt: " + localName + ", " + atts.getValue("name"));
		}
	}
	
	private void createEdge(String uri, String localName, String qName, Attributes atts) {
		
		Edge edge = null;
		Vertex from = network.getVertexByName(atts.getValue("from"));
		Vertex to = network.getVertexByName(atts.getValue("to"));
		if (from == null) {
			errorLog.add("Zu " + atts.getValue("from") + " existiert kein Vertex");
		}
		if (to == null) {
			errorLog.add("Zu " + atts.getValue("to") + " existiert kein Vertex");
		}
		if (atts.getValue("id") != "") {
			if (atts.getValue("from") != "") {
				if (atts.getValue("to") != "") {
					if ((from != null) && (to != null)) {
						edge = new Edge(Integer.parseInt(atts.getValue("id")), from, to, -1);
					}
				}
				else {
					errorLog.add("Kein to-Wert bei: " + localName + ", ID: " + Integer.parseInt(atts.getValue("id")));
				}
			}
			else {
				errorLog.add("Kein from-Wert bei: " + localName + ", ID: " + Integer.parseInt(atts.getValue("id")));
			}
		}
		else {
			errorLog.add("Kein id-Wert bei Edge: " + localName + ", ID: " + Integer.parseInt(atts.getValue("id")));
		}
		
		if (edge != null) {
			network.addEdge(edge);
			System.out.println("Edge hinzugefügt: " + edge);
		}
		else {
			errorLog.add("Keine Edge hinzugefügt: " + localName + ", ID: " + Integer.parseInt(atts.getValue("id")));
		}
	}
	
	public Network getData() {
		return this.network;
	}
}
