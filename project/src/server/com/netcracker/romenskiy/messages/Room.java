package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;
import util.xml.*;
import util.xml.message.*;

public class Room extends Observable {
	private String user1;
	private String user2;
	private List<MessageType> messages;
	
	public Room (String user1, String user2) {
		this.user1 = user1;
		this.user2 = user2;
		this.messages = new ArrayList<MessageType>();
	}
	
	public void add(MessageType message) {
		messages.add(message);
		setChanged();
		notifyObservers(message);
	}
	
	public int size() {
		return messages.size();
	}
	
	public List<String> getLastFive() {
		List<String> returned = new ArrayList<String>();
		int i = messages.size()-5;
		if (i < 0)
			i = 0;
		for(int j = i; j < messages.size(); j++) {
			returned.add(messages.get(j).getMessage());
		}
		
		return returned;
	}
	
	public boolean contains(String user1, String user2) {
		if(this.user1.equals(user1) || this.user1.equals(user2)) {
			if(this.user2.equals(user2) || this.user2.equals(user1)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if (obj == null)
			return false;
			
		if (obj.getClass() != this.getClass())
			return false;
			
		Room temp = (Room)obj;
		if(temp.user1.equals(this.user1) || temp.user1.equals(this.user2)) {
			if(temp.user2.equals(this.user2) || temp.user2.equals(this.user1)) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		int initial = 19;
		int hash = 11;
		int operand = initial * hash;
		
		hash = (operand + user1.hashCode()) + (operand + user2.hashCode());
	
		return hash;
	}
}