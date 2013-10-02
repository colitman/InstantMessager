package server.com.netcracker.romenskiy;

import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import server.com.netcracker.romenskiy.gui.*;
import org.apache.log4j.*;
import org.apache.log4j.xml.*;
import server.com.netcracker.romenskiy.messages.*;

public class Server {

private static final Users users = new Users();
private static final Logger logger = Logger.getLogger("im.server");
private RoomsObserver o = new RoomsObserver();

	public static void main(String... args) {
		
		DOMConfigurator.configure("res/log4j.xml");
		ServerGUI gui = new ServerGUI(users);
		SwingUtilities.invokeLater(gui);
	
		logger.warn("Starting a server...");
		ServerSocket ss = null;
		int PORT = ServerConfig.PORT;
		Socket s = null;
		
		try {
			ss = new ServerSocket(PORT);
			logger.warn("Server started at " + PORT + " port");
		} catch (IOException ioe) {
			logger.error("Unable to create a ServerSocket.", ioe);
		}
		
		if (ss != null) {
			while(true) {
				try {
					s = ss.accept();
					ClientThread client = new ClientThread(s, users);			
				} catch (IOException ioe) {
					if(s != null) {
						try {
							s.close();
						} catch (Exception e) {
							logger.error("Failed to close a socket.", e);
						}
					}
					logger.error("Failed to connect a client.", ioe);
				}
			}
		}
	}
}