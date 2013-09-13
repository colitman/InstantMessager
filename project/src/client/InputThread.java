package client;

import java.net.*;
import java.util.*;
import java.io.*;
import java.text.*;

import javax.swing.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import util.xml.*;

public class InputThread extends Observable implements Runnable {
	
	private Socket socket;
	private InputStream input;
	
	public InputThread(Socket s) throws IOException {
		socket = s;
		
		input = socket.getInputStream();
	}
	
	public void run() {
		try {
			receive();
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(null, "Problems with a server");
		} catch (ParserConfigurationException parserException) {
			parserException.printStackTrace();
		} catch (SAXException saxException) {
			saxException.printStackTrace();
		} catch (ParseException dateException) {
			dateException.printStackTrace();
		}
	}
	
	public void receive() throws IOException, ParserConfigurationException, SAXException, ParseException {
		while (true) {
			Message message = null;
			
			Operations.receiveMessage(message, input);
			
			setChanged();
			notifyObservers(message);
		}
	}
		
}