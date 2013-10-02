package server.com.netcracker.romenskiy;

import server.com.netcracker.romenskiy.messages.Rooms;
import java.util.*;

public class RoomsObserver implements Observer {
	
	public RoomsObserver() {
		Rooms.addObserver(this);
	}

	public void update(Observable source, Object object) {
		
	}

}