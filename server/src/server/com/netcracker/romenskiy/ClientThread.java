package server.com.netcracker.romenskiy;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.log4j.*;
import server.com.netcracker.romenskiy.messages.*;

public class ClientThread extends Thread implements Observer, ServerInterface {
	private Socket s = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private Users users;
	private static final Logger logger = Logger.getLogger("im.server");
	private History history;
	
	public ClientThread(Socket socket, History history, Users users) throws IOException {
		
		logger.warn("New client is trying to connect to the chat...");
		logger.info("Assigning a socket to a new client...");
		this.s = socket;
		
		logger.info("Getting the input stream...");
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		
		logger.info("Getting the output stream...");
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
		
		this.users = users;
		
		this.history = history;
		
		start();
		logger.warn("Connection established with [" + this + "]");
	}
	
	public void run() {
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
		String message = in.readLine();
		
		//Just for testing the approach. Change when possible
		String NAME = "Thread-";
		Messages receiver;
		
		for(int i = 2; i <= history.size()+1; i = i+2) {
			String NAME2 = NAME + i;
			receiver = history.get(NAME2);
			receiver.add(message);
		}
	}
	
	public void send(String message) throws IOException {
		out.println(message);
	}
	
	public String toString() {
		return s.getInetAddress() + ", " + super.getName();
	}
	
	public String getClientName() {
		return super.getName();
	}
	
	public void update(Observable messages, Object message) {
		try {
			send((String)message);
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