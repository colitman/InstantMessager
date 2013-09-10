package server.com.netcracker.romenskiy;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.log4j.*;
import server.com.netcracker.romenskiy.messages.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class ClientThread extends Thread implements Observer, ServerInterface {
	private Socket s = null;
	private /* BufferedReader */InputStream in = null;
	private /* PrintWriter  */OutputStream out = null;
	private Users users;
	private static final Logger logger = Logger.getLogger("im.server");
	private History history;
	private String userName = "guest";
	public Messages messages = null;
	
	public ClientThread(Socket socket, History history, Users users) throws IOException {
		
		logger.warn("New client is trying to connect to the chat...");
		logger.info("Assigning a socket to a new client...");
		this.s = socket;
		
		logger.info("Getting the input stream...");
		in = /* new BufferedReader(new InputStreamReader( */s.getInputStream()/* )) */;
		
		logger.info("Getting the output stream...");
		out = /* new PrintWriter(new BufferedWriter(new OutputStreamWriter( */s.getOutputStream()/* )), true) */;
		
		this.users = users;
		
		this.history = history;
		
		start();
		logger.warn("Connection established with [" + this + "]");
	}
	
	public void run() {
		try {
			logger.info("Waiting for client's name");
			XMLUtils.receiveAuthorize(userName, in);
			logger.info("Username received");
		} catch (SAXException se) {
			logger.error("Unable to read XML Schema", se);
		} catch (IOException io) {
			logger.error("IO Exception", io);
		} catch (ParserConfigurationException pce) {
			logger.error("ParcerConfigurationException", pce);
		}
		
		users.add(this);
		logger.info("User has been added to the userlist.");
		
		messages = new Messages();
		messages.addObserver(this);
		logger.info("Message list created");
		
		if(!history.containsKey(userName)) {
			history.put(userName, messages);
		} else {
			messages = history.get(userName);
		}
		logger.info("Message list assigned to history");
		
		try {
			logger.info("Sending the list of users.");
			XMLUtils.sendUserNamesList(users.getUserNames(), out);
			logger.info("Userlist has been sent");
		} catch (SAXException se) {
			logger.error("Unable to read XML Schema", se);
		} catch (IOException io) {
			logger.error("IO Exception (sendusernames)", io);
		} catch (ParserConfigurationException pce) {
			logger.error("ParcerConfigurationException", pce);
		} catch (TransformerConfigurationException tce) {
			logger.error("TransformerConfigurationException", tce);
		} catch (TransformerException te) {
			logger.error("TransformerException", te);
		}

		logger.info("Starting normal session.");
		try {
			while(true) {
				this.receive();
			}
		} catch (IOException ioe) {
			logger.warn("Client " + this + " has been disconnected.");
			users.remove(this);
		} finally {
			try {
				if (s != null && !s.isClosed()) {
					s.close();
					logger.warn("Socket with "  + this + " has been closed.");
				}
			} catch (IOException ioe) {
				logger.error("Socket has not been closed.", ioe);
			}
		}
	}
	
	public void receive() throws IOException {
		
		Message message = new Message();
		
		try {
			XMLUtils.receiveMessage(message, in);
			logger.info("New message received.");
		} catch (SAXException se) {
			logger.error("Unable to read XML Schema", se);
		} catch (ParserConfigurationException pce) {
			logger.error("ParcerConfigurationException", pce);
		} catch (ParseException pe) {
			logger.error("ParseException", pe);
		}
		
		String receiver = message.getToUser();
		history.get(receiver).add(message);
	}
	
	public void send(Message message) throws IOException {
		//out.println(message);
		
		try {
			XMLUtils.sendMessage(message, out);
		} catch (SAXException se) {
			logger.error("Unable to read XML Schema", se);
		} catch (ParserConfigurationException pce) {
			logger.error("ParcerConfigurationException", pce);
		} catch (TransformerConfigurationException tce) {
			logger.error("TransformerConfigurationException", tce);
		} catch (TransformerException te) {
			logger.error("TransformerException", te);
		} catch (ParseException pe) {
			logger.error("ParseException", pe);
		}
	}
	
	public String toString() {
		return /* s.getInetAddress() + ", " +  */getClientName();
	}
	
	public String getClientName() {
		return userName;
	}
	
	public void update(Observable messages, Object message) {
		try {
			send((Message)message);
		} catch (IOException io) {
			if (out != null) {
				try {
					out.close();
				} catch (Exception ioe) {
					logger.error("Failed to close the output stream", ioe);
				}
			}
			
			logger.error("Impossible to send messages", io);
		}
	}
}