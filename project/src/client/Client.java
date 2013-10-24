package client;

import static client.properties.ClientProperties.*;

import java.net.*;
import java.io.*;

import javax.swing.*;

import util.xml.*;
import util.xml.message.*;

import client.gui.*;
import client.thread.*;

import org.apache.log4j.*;
import org.apache.log4j.xml.*;

public class Client {
	
	private static final Logger logger = Logger.getLogger("im.client");;
	
	public static void main(String[] args) {
		
		DOMConfigurator.configure("res/log4j.xml");
		
		Socket socket = null;
		PipedWriter writer = null;
		PipedReader reader = null;
		
		try {
			logger.info("Creating socket...");
			socket = new Socket(IP, PORT);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			logger.info("Authorization...");
			String name = JOptionPane.showInputDialog("Enter name");
			do {
				Operations.sendAuthorize(name, out);
				Message answer = Operations.receive(in);
				String code = (String)answer.getValue();
				if (!code.equals("AUTH_FAIL")) {
					break;
				}
				name = JOptionPane.showInputDialog("Enter another name");
			} while (true);
			
			writer = new PipedWriter();
			reader = new PipedReader(writer);
			
			PrintWriter bufWriter = new PrintWriter(new BufferedWriter(writer), true);
			BufferedReader bufReader = new BufferedReader(reader);
			
			logger.info("Creating GUI...");
			ClientGUI gui = new ClientGUI(bufWriter, name, out);			
			SwingUtilities.invokeLater(gui);
			
			logger.info("Creating output thread...");
			OutputThread output = new OutputThread(out, bufReader);
			Thread outputThread = new Thread(output);
			outputThread.start();
			
			logger.info("Creating input thread...");
			InputThread input = new InputThread(in);
			Thread inputThread = new Thread(input);
			inputThread.start();
			
			input.addObserver(gui);
			
		} catch (Exception e) {
			logger.error("Can't connect to server");

			try {
				socket.close();
				writer.close();
				reader.close();
			} catch (IOException ioe) {
				logger.error("Can't close socket");
				ioe.printStackTrace();
			}
		} 
	}
}