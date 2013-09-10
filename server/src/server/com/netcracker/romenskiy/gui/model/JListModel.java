package server.com.netcracker.romenskiy.gui.model;

import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import server.com.netcracker.romenskiy.*;
import server.com.netcracker.romenskiy.gui.model.*;

public class JListModel extends AbstractListModel<ClientThread> implements Observer {
	private Users users;
	
	public JListModel(Users users) {
		this.users = users;
		users.addObserver(this);
	}
	
	public int getSize() {
		return users.size();
	}
	
	public ClientThread getElementAt(int index) {
		return users.get(index);
	}
	
	public void update(Observable users, Object message) {
		fireContentsChanged((Users)users, 0, getSize()-1);
	}
}