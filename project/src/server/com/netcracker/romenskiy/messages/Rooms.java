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
	}

	public Room getRoom(String user1, String user2) {
		for(Room r:rooms) {
			if(r.contains(user1, user2)) {
				return r;
			}
		}
		Room room = new Room(user1, user2);
		rooms.add(room);
		setChanged();
		notifyObservers(room);
		return room;
	}
}