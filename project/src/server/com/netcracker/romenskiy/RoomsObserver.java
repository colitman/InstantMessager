package server.com.netcracker.romenskiy;

import server.com.netcracker.romenskiy.messages.*;
import java.util.*;
import java.io.*;

public class RoomsObserver implements Observer {
	
	public RoomsObserver(Rooms r) {
		r.addObserver(this);
	}

	public void update(Observable source, Object object) {
		System.out.println("works");
		Room newRoom = (Room)object;
		String path = "server_history/" + newRoom.getUser1() + "-" + newRoom.getUser2() + ".hst";
		File file = new File(path);
		newRoom.addObserver(new RoomObserver(file));
	}

}