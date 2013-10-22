package server.com.netcracker.romenskiy;

import server.com.netcracker.romenskiy.messages.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;

public class RoomsObserver implements Observer {

	private static final Logger logger = Logger.getLogger("im.server");
	
	public RoomsObserver(/* Rooms r */) {
		//r.addObserver(this);
		logger.info("RoomsObserver has been created");
	}

	public void update(Observable source, Object object) {
		Room newRoom = (Room)object;
		String path = "server_history/" + newRoom.getUser1() + "-" + newRoom.getUser2() + ".hst";
		File file = new File(path);
		logger.info("File for room history created");
		newRoom.addObserver(new RoomObserver(file));
		logger.info("RoomObserver has been added to the new room");
	}

}