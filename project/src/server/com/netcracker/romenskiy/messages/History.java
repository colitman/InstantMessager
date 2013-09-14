package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;
import util.xml.*;

public class History extends Observable {

	private Map<String, Messages> history;

	public History() {
		history = new Hashtable<String, Messages>();
	}
	
	public void put(String clientName, Messages messages) {
		history.put(clientName, messages);
		setChanged();
		notifyObservers();
	}
	
	public Messages get(String name) {
		return history.get(name);
	}
	
	public boolean containsKey(String name) {
		return history.containsKey(name);
	}
	
	public int size() {
		return history.size();
	}
}