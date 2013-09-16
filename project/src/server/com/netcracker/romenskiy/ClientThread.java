package server.com.netcracker.romenskiy;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.log4j.*;
import server.com.netcracker.romenskiy.messages.*;

import util.xml.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class ClientThread extends Thread implements Observer, ServerInterface {
	private Socket s = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private Users users;
	private static final Logger logger = Logger.getLogger("im.server");
	private History history;
	private String userName = "guest";
	private Messages messages = null;
	
	public ClientThread(Socket socket, History history, Users users) throws IOException {
		
		logger.warn("New client is trying to connect to the chat...");
		logger.info("Assigning a socket to a new client...");
		this.s = socket;
		
		logger.info("Getting the input stream...");
		in = new DataInputStream(s.getInputStream());
		
		logger.info("Getting the output stream...");
		out = new DataOutputStream(s.getOutputStream());
		
		this.users = users;
		
		this.history = history;
		
		start();
		logger.warn("Connection established with [" + this + "]");
	}
	
	public void run() {
		
		prepareClient();

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
		
		Message message = null;
		
		try {
			message = Operations.receiveMessage(in);
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
		try {
			Operations.sendMessage(message, out);
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
	
	private void setClientName(String userName) {
		this.userName = userName;
	}
	
	private void prepareClient() {
		try {
			logger.info("Waiting for client's name");
			setClientName(Operations.receiveAuthorize(in));
			logger.info("Username received");
			
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
			
			logger.info("Sending the list of users.");
			Operations.sendUserNamesList(users.getUserNames(), out);
			logger.info("Userlist has been sent");
			
		} catch (SAXException se) {
			logger.error("Unable to read XML Schema", se);
		} catch (IOException io) {
			logger.error("IO Exception", io);
		} catch (ParserConfigurationException pce) {
			logger.error("ParcerConfigurationException", pce);
		} catch (TransformerConfigurationException tce) {
			logger.error("TransformerConfigurationException", tce);
		} catch (TransformerException te) {
			logger.error("TransformerException", te);
		}
	}
	
	public String toString() {
		return getClientName() + "[" + s.getInetAddress() + "]";
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