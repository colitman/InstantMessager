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
	private static final String FULL_HISTORY = "fullHistory";
	private static final String CONNECT = "connectUser";

	public static Message receive(DataInputStream input) throws IOException {
		
		String receive = input.readUTF();
		Schema schema = getSchema();
		Document document = null;
		try {
			document = getDocumentBuilder(schema).parse(new InputSource(new StringReader(receive)));
		} catch (SAXException se) {
			se.printStackTrace();
			return null;
		}
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
			case FULL_HISTORY:
				return receiveFullHistory(document);
			default:
				return null;
		}
		
	}
	
	public static void saveServerHistory(LinkedList<MessageType> message, File file) {
		Document doc = null;
		try{
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement("server_history");
			doc.appendChild(root);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			for(MessageType m:message) {
				Element mes = doc.createElement("message");
				root.appendChild(mes);
				Element date = doc.createElement("date");
				Element from = doc.createElement("from");
				Element to = doc.createElement("to");
				Element text = doc.createElement("text");
				mes.appendChild(date);
				mes.appendChild(from);
				mes.appendChild(to);
				mes.appendChild(text);
				date.appendChild(doc.createTextNode(dateFormat.format(m.getTime())));
				from.appendChild(doc.createTextNode(m.getFromUser()));
				to.appendChild(doc.createTextNode(m.getToUser()));
				text.appendChild(doc.createTextNode(m.getMessage()));
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
			
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(file);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(doc), new StreamResult(fos));
		} catch (TransformerConfigurationException tce) {
			tce.printStackTrace();
		} catch (TransformerException te) {
			te.printStackTrace();
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
			try {
				if(fos != null) {
					fos.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	public static String[] readExistingRooms() {
		File folder = new File("server_history");
		return folder.list();
	}
	
	public static ArrayList<MessageType> readHistoryFile(String fileName) {
		ArrayList<MessageType> list = new ArrayList<MessageType>();
		File file = new File(fileName);
		Schema schema = null;
		Document doc = null;
		
		try {
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		schema = schemaFactory.newSchema(new File("res/history.xsd"));
		doc = getDocumentBuilder(schema).parse(file);
		} catch (SAXException se) {
			se.printStackTrace();
			return list;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return list;
		}
		
		Element root = doc.getDocumentElement();
		NodeList messages = root.getChildNodes();
		for(int i = 0; i < messages.getLength(); i++) {
			Element messageElement = (Element) messages.item(i);
			NodeList childs = messageElement.getChildNodes();
			Element timeElement = (Element) childs.item(0);
			String time = timeElement.getFirstChild().getNodeValue();
			Element fromElement = (Element) childs.item(1);
			String from = fromElement.getFirstChild().getNodeValue();
			Element toElement = (Element) childs.item(2);
			String to = toElement.getFirstChild().getNodeValue();
			Element textElement = (Element) childs.item(3);
			String text = textElement.getFirstChild().getNodeValue();

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = formatter.parse(time);
			} catch (ParseException pe) {
				pe.printStackTrace();
				date = new Date();
			}
		
			MessageType message = new MessageType(date, from, to, text);
			list.add(message);
		}
		
		return list;
	}

	public static void sendMessage(MessageType message, DataOutputStream output) throws IOException {
		
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
			
		Element root = document.createElement("document");
		document.appendChild(root);
		Element messageElement = document.createElement("message");
		root.appendChild(messageElement);
		Element time = document.createElement("date");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		time.appendChild(document.createTextNode(dateFormat.format(new Date())));
		messageElement.appendChild(time);	
		Element from = document.createElement("from");
		from.appendChild(document.createTextNode(message.getFromUser()));
		messageElement.appendChild(from);
		Element to = document.createElement("to");
		to.appendChild(document.createTextNode(message.getToUser()));
		messageElement.appendChild(to);
		Element text = document.createElement("text");
		text.appendChild(document.createTextNode(message.getMessage()));
		messageElement.appendChild(text);

		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static Message receiveMessage(Document document) {
			
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

		Message message  = MessageFactory.getInstance().newMessage("SimpleMessage");
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(time);
		} catch (ParseException pe) {
			pe.printStackTrace();
			date = new Date();
		}
		
		message.setValue(new MessageType(date, from, to, text));
		return message;
	}
	
	public static Message receiveUserNamesList(Document document) {
		
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
	
	public static void sendUserNamesList(List<String> userNames, DataOutputStream output) throws IOException {
		
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
	
	public static void sendAuthorize(String userName, DataOutputStream output) throws IOException {
			
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
	
	public static Message receiveAuthorize(Document document) {
				
		Element root = document.getDocumentElement();
		Element authorize = (Element) root.getFirstChild();
		Element user = (Element) authorize.getFirstChild();
		
		Message message  = MessageFactory.getInstance().newMessage("AuthorizeMessage");
		message.setValue(user.getFirstChild().getNodeValue());
		return message;
	}
	
	public static void sendAnswer(String answer, DataOutputStream output) throws IOException {
	
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
	
	public static Message receiveAnswer(Document document) {
		
		Element root = document.getDocumentElement();
		Element answerElement = (Element) root.getFirstChild();
		Element code = (Element) answerElement.getFirstChild();
		
		Message message  = MessageFactory.getInstance().newMessage("AnswerMessage");
		message.setValue(code.getFirstChild().getNodeValue());
		return message;
	}
	
	public static void sendHistory(List<String> messages, DataOutputStream output) throws IOException {
		
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
		
		Element root = document.createElement("document");
		document.appendChild(root);
		Element history = document.createElement("history");
		root.appendChild(history);
		for (String str : messages) {
			Element message = document.createElement("message");
			message.appendChild(document.createTextNode(str));
			history.appendChild(message);
		}
		
		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static Message receiveHistory(Document document) {
		
		Element root = document.getDocumentElement();
		Element history = (Element) root.getFirstChild();
		List<String> messages = new ArrayList<String>();
		NodeList nodeList = history.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			messages.add(nodeList.item(i).getFirstChild().getNodeValue());
		}
		
		Message message  = MessageFactory.getInstance().newMessage("HistoryMessage");
		message.setValue(messages);
		return message;
	}
	
	public static void sendConnectUser(String name, DataOutputStream output) throws IOException {
		
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
		
		Element root = document.createElement("document");
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
	
	public static Message receiveConnectUser(Document document) {
		
		Element root = document.getDocumentElement();
		Element connectUser = (Element) root.getFirstChild();
		Element nameElement = (Element) connectUser.getFirstChild();
		
		Message message  = MessageFactory.getInstance().newMessage("ConnectUserMessage");
		message.setValue(nameElement.getFirstChild().getNodeValue());
		return message;
	}
	
	public static void sendFullHistory(Map<String, List<String>> history, DataOutputStream output) throws IOException {
		Schema schema = getSchema();
		
		Document document = getDocumentBuilder(schema).newDocument();
		
		Element root = document.createElement("document");
		document.appendChild(root);
		Element fullHistory = document.createElement("fullHistory");
		root.appendChild(fullHistory);
		Element amount = document.createElement("amount");
		amount.appendChild(document.createTextNode(String.valueOf(history.size())));
		fullHistory.appendChild(amount);
		Set<String> keys = history.keySet();
		for (String key : keys) {
			Element historyElement = document.createElement("history");
			List<String> list = history.get(key);
			for (String str : list) {
				Element message = document.createElement("message");
				message.appendChild(document.createTextNode(str + "\n"));
				historyElement.appendChild(message);
			}
			fullHistory.appendChild(historyElement);
		}
		
		DOMSource source = validate(document, schema);
		
		String send = DOMtoString(source);
		output.writeUTF(send);
		output.flush();
	}
	
	public static Message receiveFullHistory(Document document) {
		Element root = document.getDocumentElement();
		Element fullHistoryElement = (Element) root.getFirstChild();
		Element amountElement = (Element) fullHistoryElement.getFirstChild();
		int amount = Integer.valueOf(amountElement.getFirstChild().getNodeValue());
		Map<String, List<String>> fullHistory = new Hashtable<String, List<String>>();
		for (Element history = (Element) fullHistoryElement.getNextSibling(); history != null; history = (Element) fullHistoryElement.getNextSibling()) {
			List<String> list = new ArrayList<>();
			for (Element message = (Element) history.getFirstChild(); message != null; message = (Element) history.getNextSibling()) {
				list.add(message.getFirstChild().getNodeValue());
			}
			fullHistory.put(history.getTagName(), list);
		}
		
		Message message = MessageFactory.getInstance().newMessage("fullHistoryMessage");
		message.setValue(fullHistory);
		return message;
	}
	
	private static DocumentBuilder getDocumentBuilder(Schema schema) {
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			
			builderFactory.setNamespaceAware(true);
			builderFactory.setIgnoringElementContentWhitespace(true);
			
			builderFactory.setSchema(schema);
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			return builder;
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return null;
		}
	}
	
	private static Schema getSchema() {
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = schemaFactory.newSchema(new File("res/document.xsd"));
			return schema;
		} catch (SAXException se) {
			se.printStackTrace();
			return null;
		}
	}
	
	private static DOMSource validate(Document document, Schema schema) {
		DOMSource source = null;
		try{
			source = new DOMSource(document);
			Validator validator = schema.newValidator();
			validator.validate(source);
			return source;
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
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
        } catch(TransformerException ex) {
           ex.printStackTrace();
           return null;
        }
	}
}