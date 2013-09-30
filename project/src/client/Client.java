package client;

import java.net.*;
import java.io.*;

import javax.swing.*;

import util.xml.*;
import util.xml.message.*;

import client.gui.*;
import client.thread.*;

public class Client implements ClientInterface {
	
	public static void main(String[] args) {
		
		Socket socket = null;
		PipedWriter writer = null;
		PipedReader reader = null;
		
		try {
			
			socket = new Socket(IP, PORT);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			String name = JOptionPane.showInputDialog("Enter name");
			
			Operations.sendAuthorize(name, out);
			//while (!answer.contains("accept")) {
			//	name = JOptionPane.showInputDialog("Please enter another name");
			//	Operations.sendAuthorize(name, out);
			//	answer = Operations.receiveAnswer(in);
			//}
			
			writer = new PipedWriter();
			reader = new PipedReader(writer);
			
			PrintWriter bufWriter = new PrintWriter(new BufferedWriter(writer), true);
			BufferedReader bufReader = new BufferedReader(reader);
			
			ClientGUI gui = new ClientGUI(bufWriter, name, out);			
			SwingUtilities.invokeLater(gui);
			
			OutputThread output = new OutputThread(out, bufReader);
			Thread outputThread = new Thread(output);
			outputThread.start();
			
			InputThread input = new InputThread(in);
			Thread inputThread = new Thread(input);
			inputThread.start();
			
			//ListenUsersThread listenUsers = new ListenUsersThread(in);
			//Thread listenThread = new Thread(listenUsers);
			//listenThread.start();
			
			input.addObserver(gui);
			//listenUsers.addObserver(gui);
		
		} catch (Exception e) {
			System.out.println("Error: Unknown host");
			System.out.println("Please check your internet connection");
			
			try {
				socket.close();
				writer.close();
				reader.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} 
	}
}