package client.thread;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import util.xml.*;
import util.xml.message.*;

public class OutputThread implements Runnable {

	private BufferedReader input;
	private DataOutputStream output;
	
	public OutputThread(DataOutputStream out, BufferedReader in) {
		output = out;		
		input = in;
	}
	
	public void run() {
		try {
			send();
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(null, "Problems with a server");
		}
	}
	
	private void send() throws IOException {
		while (true) {
			String from = input.readLine();
			String to = input.readLine();
			String text = input.readLine();
			Date time = new Date();
			
			MessageType message = new MessageType(time, from, to, text);
			Message mess = MessageFactory.getInstance().newMessage("SimpleMessage");
			mess.setValue(message);
			
			Operations.sendMessage(message, output);
		}
	}
}