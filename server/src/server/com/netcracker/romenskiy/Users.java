package server.com.netcracker.romenskiy;

import java.net.*;
import java.io.*;
import java.util.*;

public class Users extends Observable {
	private ArrayList<ClientThread> users;
	
	public Users() {
		users = new ArrayList<ClientThread>();
	}
	
	public void add(ClientThread ct) {
		users.add(ct);
		setChanged();
		notifyObservers(ct);
	}
	
	public void remove(ClientThread ct) {
		users.remove(ct);
		setChanged();
		notifyObservers(ct);
	}
	
	public int size() {
		return users.size();
	}
	
	public ClientThread get(int index) {
		return users.get(index);
	}
}