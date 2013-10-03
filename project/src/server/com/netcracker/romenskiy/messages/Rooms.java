package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;
import util.xml.*;
import util.xml.message.*;

public class Rooms extends Observable {
	
	private List<Room> rooms;
	
	public Rooms() {
		rooms = new ArrayList<Room>();
		String[] roomsFileNames = Operations.readExistingRooms();
		int index;
		String fileName;
		int indexOfPoint;
		for(int i = 0; i < roomsFileNames.length; i++) {
			fileName = roomsFileNames[i];
			index = fileName.indexOf("-");
			indexOfPoint = fileName.indexOf(".");
			String user1 = fileName.substring(0, index);
			String user2 = fileName.substring(index + 1, indexOfPoint -1);
			Room room = getRoom(user1, user2);
			ArrayList<MessageType> messages = Operations.readHistoryFile("server_history/" + fileName);
			room.setMessages(messages);
		}
	}

	public Room getRoom(String user1, String user2) {
		for(Room r:rooms) {
			if(r.contains(user1, user2)) {
				System.out.println("Such Room exists");
				return r;
			}
		}
		System.out.println("new Room created");
		Room room = new Room(user1, user2);
		rooms.add(room);
		setChanged();
		notifyObservers(room);
		return room;
	}
}