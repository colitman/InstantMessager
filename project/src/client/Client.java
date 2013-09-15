package client;

import java.net.*;
import java.io.*;

import javax.swing.*;

import util.xml.*;

public class Client implements ClientInterface {
	
	public static void main(String[] args) {
		
		Socket socket = null;
		PipedWriter writer = null;
		PipedReader reader = null;
		
		try {
			
			socket = new Socket(IP, PORT);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			String name = JOptionPane.showInputDialog("Enter name");
			Operations.sendAuthorize(name, out);
			String answer = null;
			Operations.receiveAnswer(answer, in);
			
			while (!answer.contains("accept")) {
				name = JOptionPane.showInputDialog("Please enter a another name");
				Operations.sendAuthorize(name, out);
				Operations.receiveAnswer(answer, in);
			}
			
			writer = new PipedWriter();
			reader = new PipedReader(writer);
			
			PrintWriter bufWriter = new PrintWriter(new BufferedWriter(writer), true);
			BufferedReader bufReader = new BufferedReader(reader);
		
			ClientGUI gui = new ClientGUI(bufWriter);			
			SwingUtilities.invokeLater(gui);
		
			OutputThread output = new OutputThread(socket, bufReader);
			Thread outputThread = new Thread(output);
			outputThread.start();
		
			InputThread input = new InputThread(socket);
			Thread inputThread = new Thread(input);
			inputThread.start();
			
			ListenUsersThread listenUsers = new ListenUsersThread(socket);
			Thread listenThread = new Thread(listenUsers);
			listenThread.start();
		
			input.addObserver(gui);
			listenUsers.addObserver(gui);
		
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