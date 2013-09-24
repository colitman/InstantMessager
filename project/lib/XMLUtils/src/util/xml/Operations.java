package util.xml;

import java.text.*;
import java.util.*;
import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import util.xml.message.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Contains methods to send/receive messages, authorization, active users list, history by XML
 */
public class Operations {

	private static final String SIMPLE = "message";
	private static final String AUTH = "authorize";
	private static final String USERS = "userList";
	private static final String ANSWER = "answer";
	private static final String HISTORY = "history";
	private static final String CONNECT = "connectUser";

	public static Message receive(DataInputStream input) throws ParserConfigurationException, IOException, ParseException, SAXException {
		
		String receive = input.readUTF();
		Schema schema = getSchema();
		Document document = getDocumentBuilder(schema).parse(new InputSource(new StringReader(receive)));
		Element type = (Element)document.getDocumentElement().getFirstChild();
		String typeName = type.getTagName();
		
		switch(typeName) {
			case SIMPLE:
				return receiveMessage(document);
			case AUTH:
				return receiveAuthorize(document);
			case USERS:
				return receiveUserNamesList(document);
			case ANSWER:
				return receiveAnswer(document);
			case HISTORY:
				return receiveHistory(document);
			case CONNECT:
				return receiveConnectUser(document);
			default:
				return null;
		}
		
	}

	public static void sendMessage(MessageType message, DataOutputStream output) throws ParserConfigurationException,
			TransformerConfigurationException, ParseException, TransformerException, SAXException, IOException {
		
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
			
		Element root = document.createElement("document");
		document.appendChild(root);
		Element messageElement = document.createElement("message");
		root.appendChild(messageElement);
		Element time = document.createElement("time");
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		time.appendChild(document.createTextNode(dateFormat.format(new Date())));
		messageElement.appendChild(time);	
		Element to = document.createElement("to");
		to.appendChild(document.createTextNode(message.getToUser()));
		messageElement.appendChild(to);
		Element from = document.createElement("from");
		from.appendChild(document.createTextNode(message.getFromUser()));
		messageElement.appendChild(from);
		Element text = document.createElement("text");
		text.appendChild(document.createTextNode(message.getMessage()));
		messageElement.appendChild(text);

		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static Message receiveMessage(Document document) throws ParserConfigurationException,
			IOException, ParseException, SAXException {
			
		Element root = document.getDocumentElement();
		Element messageElement = (Element) root.getFirstChild();
		NodeList childs = messageElement.getChildNodes();
		Element timeElement = (Element) childs.item(0);
		String time = timeElement.getFirstChild().getNodeValue();
		Element fromElement = (Element) childs.item(1);
		String from = fromElement.getFirstChild().getNodeValue();
		Element toElement = (Element) childs.item(2);
		String to = toElement.getFirstChild().getNodeValue();
		Element textElement = (Element) childs.item(3);
		String text = textElement.getFirstChild().getNodeValue();

		Message message  = MessageFactory.getInstance().newMessage(SIMPLE);
		message.setValue(new MessageType(DateFormat.getDateInstance().parse(time), from, to, text));
		return message;
	}
	
	public static Message receiveUserNamesList(Document document) throws ParserConfigurationException,
			SAXException, IOException {
		
		Element root = document.getDocumentElement();		
		Element userList = (Element) root.getFirstChild();
		Element amount = (Element) userList.getFirstChild();
		int count = Integer.valueOf(amount.getFirstChild().getNodeValue());
		List<String> userNames = new ArrayList<String>();
		NodeList nodeList = userList.getChildNodes();
		for (int i = 1; i <= count; i++) {
			Element element = (Element) nodeList.item(i);
			String userName = element.getFirstChild().getNodeValue();
			userNames.add(userName);
		}	
		
		Message message  = MessageFactory.getInstance().newMessage("UserListMessage");
		message.setValue(userNames);
		return message;
	}
	
	public static void sendUserNamesList(List<String> userNames, DataOutputStream output) throws ParserConfigurationException,
			TransformerConfigurationException, TransformerException, SAXException, IOException {
		
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
		
		Element root = document.createElement("document");
		document.appendChild(root);
		Element userList = document.createElement("userList");
		root.appendChild(userList);
		Element amount = document.createElement("amount");
		amount.appendChild(document.createTextNode(String.valueOf(userNames.size())));
		userList.appendChild(amount);
		for (int i = 0; i < userNames.size(); i++) {
			Element user = document.createElement("user");
			user.appendChild(document.createTextNode(userNames.get(i)));
			userList.appendChild(user);
		}
		
		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static void sendAuthorize(String userName, DataOutputStream output) throws ParserConfigurationException, SAXException, IOException, 
			TransformerConfigurationException, TransformerException {
			
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
		
		Element root = document.createElement("document");
		document.appendChild(root);
		Element authorize = document.createElement("authorize");
		root.appendChild(authorize);
		Element user = document.createElement("userName");
		user.appendChild(document.createTextNode(userName));
		authorize.appendChild(user);
		
		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static Message receiveAuthorize(Document document) throws SAXException, IOException, ParserConfigurationException {
				
		Element root = document.getDocumentElement();
		Element authorize = (Element) root.getFirstChild();
		Element user = (Element) authorize.getFirstChild();
		
		Message message  = MessageFactory.getInstance().newMessage("AuthorizeMessage");
		message.setValue(user.getFirstChild().getNodeValue());
		return message;
	}
	
	public static void sendAnswer(String answer, DataOutputStream output) throws ParserConfigurationException, SAXException, IOException,
		TransformerConfigurationException, TransformerException {
	
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
		
		Element root = document.createElement("document");
		document.appendChild(root);
		Element answerElement = document.createElement("answer");
		root.appendChild(answerElement);
		Element code = document.createElement("code");
		code.appendChild(document.createTextNode(answer));
		answerElement.appendChild(code);
		
		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static Message receiveAnswer(Document document) throws SAXException, IOException, ParserConfigurationException {
		
		Element root = document.getDocumentElement();
		Element answerElement = (Element) root.getFirstChild();
		Element code = (Element) answerElement.getFirstChild();
		
		Message message  = MessageFactory.getInstance().newMessage(ANSWER);
		message.setValue(code.getFirstChild().getNodeValue());
		return message;
	}
	
	public static void sendHistory(List<String> messages, DataOutputStream output) throws SAXException, IOException, ParserConfigurationException,
		TransformerConfigurationException, TransformerException {
		
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
		
		Element root = document.createElement("document");
		document.appendChild(root);
		Element history = document.createElement("history");
		root.appendChild(history);
		for (String str : messages) {
			Element message = document.createElement("message");
			message.appendChild(document.createTextNode(str));
			root.appendChild(message);
		}
		
		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static Message receiveHistory(Document document) throws SAXException, IOException, ParserConfigurationException {
		
		Element root = document.getDocumentElement();
		Element history = (Element) root.getFirstChild();
		List<String> messages = new ArrayList<String>();
		for (Element message = (Element) history.getFirstChild(); message != null; message = (Element) history.getNextSibling()) {
			messages.add(message.getFirstChild().getNodeValue());
		}
		
		Message message  = MessageFactory.getInstance().newMessage(HISTORY);
		message.setValue(messages);
		return message;
	}
	
	public static void sendConnectUser(String name, DataOutputStream output) throws SAXException, IOException, ParserConfigurationException,
		TransformerConfigurationException, TransformerException {
		
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
		
		Element root = document.createElement("root");
		document.appendChild(root);
		Element connectUser = document.createElement("connectUser");
		root.appendChild(connectUser);
		Element nameElement = document.createElement("name");
		nameElement.appendChild(document.createTextNode(name));
		connectUser.appendChild(nameElement);
		
		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static Message receiveConnectUser(Document document) throws SAXException, IOException, ParserConfigurationException {
		
		Element root = document.getDocumentElement();
		Element connectUser = (Element) root.getFirstChild();
		Element nameElement = (Element) connectUser.getFirstChild();
		
		Message message  = MessageFactory.getInstance().newMessage(CONNECT);
		message.setValue(nameElement.getFirstChild().getNodeValue());
		return message;
	}
	
	private static DocumentBuilder getDocumentBuilder(Schema schema) throws ParserConfigurationException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setSchema(schema);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		return builder;
	}
	
	private static Schema getSchema() throws SAXException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = schemaFactory.newSchema(new File("res/document.xsd"));
		return schema;
	}
	
	private static DOMSource validate(Document document, Schema schema) throws SAXException, IOException {
		DOMSource source = new DOMSource(document);
		Validator validator = schema.newValidator();
		validator.validate(source);
		return source;
	}
	
	private static void transform(DOMSource source, OutputStream output) throws TransformerConfigurationException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();	
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(source, new StreamResult(output));
	}
	
	private static String DOMtoString(DOMSource source) {
		try
        {
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer transformer = tf.newTransformer();
           transformer.transform(source, result);
           writer.flush();
           return writer.toString();
        }
        catch(TransformerException ex)
        {
           ex.printStackTrace();
           return null;
        }
	}
}