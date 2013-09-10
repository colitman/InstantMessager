package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;

public class Messages extends Observable {

	private LinkedList<Message> list;

	public Messages() {
		list = new LinkedList<Message>();
	}
	
	public void add(Message message) {
		list.addLast(message);
		setChanged();
		notifyObservers(message);
	}
	
	public int size() {
		return list.size();
	}
	
	public Message getLast() {
		return list.getLast();
	}
	
	public List<Message> getLastFiveWith(String userName) {
		List<Message> lastFive = new LinkedList<Message>();
		
		int i = 0;
		for(Message m:list) {
			if(m.getFromUser() == userName || m.getToUser() == userName) {
				lastFive.add(m);
				i++;
				if(i >= 5) {
					break;
				}
			}
		}
		
		return lastFive;
	}
}