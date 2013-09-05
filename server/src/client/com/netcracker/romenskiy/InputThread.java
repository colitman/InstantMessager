package client.com.netcracker.romenskiy;

import java.io.*;
import java.net.*;
import java.util.*;

public class InputThread extends Observable implements Runnable {

	private Socket s;
	private BufferedReader in = null;

	public InputThread(Socket s) {
		this.s = s;
	}

	public void run() {
		receive();
	}
	
	public void receive() {
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String response = null;
			while(true) {
				response = in.readLine();
				setChanged();
				notifyObservers(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}