package client.gui;

import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import util.xml.*;
import util.xml.message.*;
import util.xml.message.Message;

import org.apache.log4j.*;
import org.apache.log4j.xml.*;

import client.thread.*;

public class UserListCellRenderer extends JLabel implements ListCellRenderer<String>, Observer {
	
	private ClientGUI win;
	private JList<? extends String> list;
	private MessageType messageType;

	public UserListCellRenderer(ClientGUI win) {
		this.win = win;
		setOpaque(true);
		InputThread.getInput().addObserver(this);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
	
		this.list = list;
		
		setText(value);
		setBackground(Color.WHITE);
		
		if(!isSelected && win.getNonRead().contains(value))
			setBackground(Color.YELLOW);
		
		if(isSelected)
			setBackground(Color.GRAY);
		
		return this;
	}
	
	@Override
	public void update (Observable source, Object message) {
		Message mess = (Message)message;
		if(mess.getType().equals("SimpleMessage")) {
			messageType = (MessageType)mess.getValue();
			list.updateUI();
		}
	}
}