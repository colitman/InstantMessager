package client.com.netcracker.romenskiy;

import java.io.*;
import java.net.*;
import java.util.*;

public class OutputThread implements Runnable {

	private Socket s;
	private PrintWriter out = null;
	private BufferedReader input;

	public OutputThread(Socket s, BufferedReader input) {
		this.s = s;
		this.input = input;
		
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		send();
	}
	
	public void send() {
		try {			
			while(true) {
					String message = input.readLine();
					out.println(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}