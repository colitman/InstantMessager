/**
*Class for storing the list of users, which are connected to the server
*/

package server.com.netcracker.romenskiy;

import java.net.*;
import java.io.*;
import java.util.*;

public class Users extends Observable {
	private ArrayList<ClientThread> users;
	
	/**
	*Creates an empty list of users
	*/
	public Users() {
		users = new ArrayList<ClientThread>();
	}
	
	/**
	*Adds the new user to the list
	*/
	public void add(ClientThread ct) {
		users.add(ct);
		setChanged();
		notifyObservers(ct);
	}
	
	/**
	*Removes a user from the list
	*/
	public void remove(ClientThread ct) {
		users.remove(ct);
		setChanged();
		notifyObservers(ct);
	}
	
	public boolean contain(String username) {
		for(ClientThread ct:users) {
			if(ct.getClientName().equals(username)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	*Returns the number of active users
	*/
	public int size() {
		return users.size();
	}
	
	/**
	*Returns the instance of client object by the index number
	*/
	public ClientThread get(int index) {
		return users.get(index);
	}
	
	public ClientThread get(String name) {
		for(ClientThread ct:users) {
			if(ct.getClientName().equals(name)) {
				return ct;
			}
		}
		return null;
	}
	
	/**
	*Returns a list of usernames
	*/
	public List<String> getUserNames() {
		List<String> userNames = new ArrayList<String>();
	
		for(ClientThread ct:users) {
			userNames.add(ct.getClientName());
		}
		
		return userNames;
	}
}