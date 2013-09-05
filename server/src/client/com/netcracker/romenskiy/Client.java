package client.com.netcracker.romenskiy;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class Client implements ClientInterface {

	

	public static void main(String... args) throws Exception {
		
		Socket s = new Socket(IP, PORT);
		
		PipedReader reader = null;
		PipedWriter writer = null;
		BufferedReader input = null;
		PrintWriter output = null;
		
		try {
			reader = new PipedReader();
			input = new BufferedReader(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
		writer = new PipedWriter(reader);
		output = new PrintWriter(new BufferedWriter(writer), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		InputThread in = new InputThread(s);
		OutputThread out = new OutputThread(s, input);
		Thread it = new Thread(in);
		Thread ot = new Thread(out);
		it.start();
		ot.start();

		ClientGUI gui = new ClientGUI(output);
		in.addObserver(gui);
		SwingUtilities.invokeLater(gui);
	}
}