package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;
import util.xml.*;
import util.xml.message.*;
import org.apache.log4j.*;

public class Rooms extends Observable {
	
	private List<Room> rooms;
	private static final Logger logger = Logger.getLogger("im.server");
	
	public Rooms(RoomsObserver o) {
		this.addObserver(o);
		logger.info("RoomsObserver has been added");
		logger.info("Searching and creating rooms from log files");
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
			String user2 = fileName.substring(index + 1, indexOfPoint);
			Room room = getRoom(user1, user2);
			ArrayList<MessageType> messages = Operations.readHistoryFile("server_history/" + fileName);
			room.setMessages(messages);
		}
	}

	public Room getRoom(String user1, String user2) {
		for(Room r:rooms) {
			if(r.contains(user1, user2)) {
				logger.info("Existing room was found for " + user1 + " and " + user2);
				return r;
			}
		}
		Room room = new Room(user1, user2);
		rooms.add(room);
		logger.info("New room for " + user1 + " and " + user2 + " was added to the room list");
		setChanged();
		notifyObservers(room);
		logger.info("RoomsObserver notified about new room");
		return room;
	}
}