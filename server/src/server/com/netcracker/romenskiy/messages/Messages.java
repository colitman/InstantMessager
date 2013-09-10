package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;

public class Messages extends Observable {

	private List<Message> list;

	public Messages() {
		list = new ArrayList<Message>();
	}
	
	public void add(Message message) {
		list.add(message);
		setChanged();
		notifyObservers(message);
	}
	
	public int size() {
		return list.size();
	}
	
	public Message getLast() {
		return list.get(list.size()-1);
	}
	
	public List<Message> getLastFiveWith(String userName) {
		List<Message> lastFive = new ArrayList<Message>();
		List<Message> temp = new ArrayList<Message>();
		
		for(Message m:list) {
			if(m.getToUser().equals(userName) || m.getFromUser().equals(userName)) {
				temp.add(m);
			}
		}
		
		int i = temp.size() - 5;
		if(i < 0) {
			i = 0;
		}
		for (i = i; i < temp.size(); i++) {
			lastFive.add(temp.get(i));
		}
		
		return lastFive;
	}
}