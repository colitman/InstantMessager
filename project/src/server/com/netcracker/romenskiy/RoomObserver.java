package server.com.netcracker.romenskiy;

import server.com.netcracker.romenskiy.messages.*;
import java.util.*;
import java.io.*;
import util.xml.*;
import util.xml.message.*;

public class RoomObserver implements Observer {

	private File file;
	private LinkedList<MessageType> list = new LinkedList<MessageType>();
	
	public RoomObserver(File file) {
		this.file = file;
	}

	public void update(Observable source, Object object) {
		System.out.println("update in the RoomObserver");
		MessageType message = (MessageType)object;
		add(message);
		saveFile(list, file);
	}
	
	private void add(MessageType message) {
		if(list.size() < 10) {
			list.add(message);
		} else {
			list.removeFirst();
			list.addLast(message);
		}
	}
	
	private void saveFile(LinkedList<MessageType> message, File file) {
		Operations.saveServerHistory(list, file);
	}

}