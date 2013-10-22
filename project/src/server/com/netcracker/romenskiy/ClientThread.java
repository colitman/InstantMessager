package server.com.netcracker.romenskiy;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.log4j.*;
import server.com.netcracker.romenskiy.messages.*;

import util.xml.*;
import util.xml.message.*;

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
	private String userName = "guest";
	private boolean disabled = false;
	
	private Map<Integer, Room> rooms;
	private Rooms rooms1;
	
	public ClientThread(Socket socket, Users users, Rooms rooms1) throws IOException {
		
		logger.warn("New client is trying to connect to the chat...");
		logger.info("Assigning a socket to a new client...");
		this.s = socket;
		
		logger.info("Getting the input stream...");
		in = new DataInputStream(s.getInputStream());
		
		logger.info("Getting the output stream...");
		out = new DataOutputStream(s.getOutputStream());
		
		this.users = users;
		users.addObserver(this);
		
		this.rooms = new Hashtable<Integer, Room>();
		this.rooms1 = rooms1;
		
		start();
		logger.warn("Connection with new user is established.");
	}
	
	public void run() {
		try {
			prepareClient();
			logger.info("Starting normal session with " + getClientName());
			while(true) {
				this.receive();
			}
		} catch (IOException ioe) {
			logger.warn("Client " + this + " has been disconnected.");
			this.setDisabled(true);
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
		Message message = Operations.receive(in);
		logger.info("New message received.");
		
		processMessage(message);		
	}
	
	private void processMessage(Message message) throws IOException {
	
		if (message.getType().equals("SimpleMessage")) {
			MessageType messageType = (MessageType)message.getValue();
			String receiver = messageType.getToUser();
			String sender = messageType.getFromUser();
			logger.info("Searching room for " + receiver + " and " + sender);
			Room room = rooms1.getRoom(sender, receiver);
			if(!this.rooms.containsKey(room.hashCode())) {
				logger.info("ClientThread does not know this room");
				this.rooms.put(room.hashCode(), room);
				logger.info("ClientThread has remembered this room");
				room.addObserver(this);
				logger.info("Sender Thread was added as an observer for this room");
				room.addObserver(users.get(receiver));
				logger.info("Receiver Thread was added as an observer for this room");
			}
			logger.info("ClientThread knows this room");
			room.add(messageType);
		}
		
		if (message.getType().equals("ConnectUserMessage")) {
			String user = (String)message.getValue();
			Room room = rooms1.getRoom(this.getClientName(), user);
			Operations.sendHistory(room.getLastFive(), out);
			logger.info("History sent for " + this.getClientName() + " and " + user);
		}
	}
	
	public String toString() {
		return getClientName() + " [" + s.getInetAddress() + "]";
	}
	
	public String getClientName() {
		return userName;
	}
	
	public void update(Observable source, Object object) {
		if(source instanceof Users) {
			if(!this.isDisabled()) {
				try {
					Operations.sendUserNamesList(users.getUserNames(), out);
				} catch (IOException io) {
					logger.error("IO Exception", io);
				}
			}
		}
		
		if (source instanceof Room) {
			try{
				Operations.sendMessage((MessageType)object, out);
			} catch (IOException ioe) {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
						logger.error("Failed to close the output stream", e);
					}
				}
				logger.error("Impossible to send messages", ioe);
			}
		}
	}
	
	private void setDisabled(boolean state) {
		this.disabled = state;
	}
	
	private boolean isDisabled() {
		return this.disabled;
	}
	
	private void setClientName(String userName) {
		this.userName = userName;
	}
	
	private void prepareClient() throws IOException {
		Message mes = null;
		String name = null;
		do {
			logger.info("Waiting for client's name");
			mes = Operations.receive(in);
			name = (String)mes.getValue();
			if(!users.contain(name)) {
				logger.info("Username \"" + name + "\" accepted");
				Operations.sendAnswer("AUTH_ACCEPT", out);
				break;
			}
			logger.info("Username \"" + name + "\" rejected");
			Operations.sendAnswer("AUTH_FAIL", out);
		} while(true);
		
		setClientName(name);
		logger.info("Username for " + s.getInetAddress() + " received: " + userName);
		
		users.add(this);
		logger.info("User " + getClientName() + " has been added to the userlist.");
		
		logger.info("Sending the list of users.");
		Operations.sendUserNamesList(users.getUserNames(), out);
		logger.info("Userlist has been sent");
	}
}