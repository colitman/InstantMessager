package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;
import util.xml.*;
import util.xml.message.*;

public class Messages extends Observable {

	private List<MessageType> list;

	public Messages() {
		list = new ArrayList<MessageType>();
	}
	
	public void add(MessageType message) {
		list.add(message);
		setChanged();
		notifyObservers(message);
	}
	
	public int size() {
		return list.size();
	}
	
	public MessageType getLast() {
		return list.get(list.size()-1);
	}
	
	public List<String> getLastFiveWith(String userName) {
	
		List<String> lastFive = new ArrayList<String>();
		List<MessageType> temp = new ArrayList<MessageType>();
		
		for(MessageType m:list) {
			if(m.getToUser().equals(userName) || m.getFromUser().equals(userName)) {
				temp.add(m);
			}
		}
		
		int i = temp.size() - 5;
		if(i < 0) {
			i = 0;
		}
		for (int j = i; j < temp.size(); j++) {
			lastFive.add(temp.get(j).getMessage());
		}
		
		return lastFive;
	}
}