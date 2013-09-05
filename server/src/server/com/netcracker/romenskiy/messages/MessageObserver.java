package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;

public class MessageObserver implements Observer {
	
	private Messages list = null;
	private BufferedWriter out = null;
	
	public MessageObserver(Messages list) {
		this.list = list;
		try {
			out = new BufferedWriter(new FileWriter("logs/messages.log", true));
		} catch (IOException io) {
			io.printStackTrace();
		}
		list.addObserver(this);
	}
	
	public void update(Observable list, Object message) {
		saveHistory();
	}
	
	private void saveHistory() {
		try {
			out.write(list.getLast());
			out.newLine();
			out.flush();
		} catch (IOException e) {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ioe) {
					System.out.println("Unable to close a log file.");
					ioe.printStackTrace();
				}
			}
			e.printStackTrace();
		} 
	}
}