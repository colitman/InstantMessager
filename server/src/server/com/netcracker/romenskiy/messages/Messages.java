package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;

public class Messages extends Observable {

	private LinkedList<String> list;

	public Messages() {
		list = new LinkedList<String>();
	}
	
	public void add(String message) {
		list.addLast(message);
		setChanged();
		notifyObservers(message);
	}
	
	public int size() {
		return list.size();
	}
	
	public String getLast() {
		return list.getLast();
	}
}