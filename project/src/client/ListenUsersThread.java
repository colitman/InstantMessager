package client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import util.xml.*;

import javax.swing.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class ListenUsersThread extends Observable implements Runnable {
	
	private Socket socket;
	private InputStream input;
	
	public ListenUsersThread(Socket socket) throws IOException {
		this.socket = socket;
		
		input = socket.getInputStream();
	}
	
	@Override
	public void run() {
		try {
			receive();
		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Problems with a server");
		} catch (InterruptedException interruptedException) {
			JOptionPane.showMessageDialog(null, "Interrupted exception thrown");
		} catch (ParserConfigurationException parserException) {
			parserException.printStackTrace();
		} catch (SAXException saxException) {
			saxException.printStackTrace();
		}
	}
	
	private void receive() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
		while (true) {
			List<String> users = new ArrayList<String>();
			
			Operations.receiveUserNamesList(users, input);
			
			setChanged();
			notifyObservers(users);
			
			TimeUnit.SECONDS.sleep(5);
		}
	}
}