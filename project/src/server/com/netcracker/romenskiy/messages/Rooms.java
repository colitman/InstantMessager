package server.com.netcracker.romenskiy.messages;

import java.net.*;
import java.io.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;
import util.xml.*;
import util.xml.message.*;

public class Rooms {
	
	private static List<Room> rooms = new ArrayList<Room>();

	public static Room getRoom(String user1, String user2) {
		for(Room r:rooms) {
			if(r.contains(user1, user2)) {
				return r;
			}
		}
		Room room = new Room(user1, user2);
		rooms.add(room);
		return room;
	}
}