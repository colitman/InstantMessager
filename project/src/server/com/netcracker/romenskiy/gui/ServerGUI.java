package server.com.netcracker.romenskiy.gui;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import server.com.netcracker.romenskiy.*;
import server.com.netcracker.romenskiy.gui.model.*;
import org.apache.log4j.*;
import server.com.netcracker.romenskiy.messages.*;

public class ServerGUI extends JFrame implements Runnable, Observer {
	
	private static final Logger logger = Logger.getLogger("im.server.gui");

	Users users;

	JPanel chatPanel = new JPanel(new BorderLayout());
	JPanel servicePanel = new JPanel(new BorderLayout());
	JPanel statusPanel = new JPanel(new BorderLayout());
	JPanel countPanel = new JPanel(new BorderLayout());
	
	JTextArea userMessages = new JTextArea();
	JScrollPane userScroll = new JScrollPane(userMessages);
	
	JTextArea serviceMessages = new JTextArea();
	JScrollPane serviceScroll = new JScrollPane(serviceMessages);
	
	JLabel usersCountTitle = new JLabel("Number of users: ");
	JLabel usersCount = new JLabel("0");
	
	JListModel listModel;
	JList<ClientThread> userList;
	JScrollPane userListScroll;
	
	JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, chatPanel, servicePanel);
	JSplitPane side = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, main, statusPanel);
	
	public ServerGUI(Users users) {
	
		logger.info("GUI constructor started, assigning lists");
		
		this.users = users;
		users.addObserver(this);
		
		logger.info("Lists are assigned");
		
		listModel = new JListModel(users);
		userList = new JList<ClientThread>(listModel);
		userListScroll = new JScrollPane(userList);
	}
	
	public void run() {
		logger.info("Starting to build the GUI window");
		createFrame();
		logger.info("GUI window is built");
	}
	
	private void createFrame() {
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 480);
		
		chatPanel.add(userScroll, BorderLayout.CENTER);
		servicePanel.add(serviceScroll, BorderLayout.CENTER);
		countPanel.add(usersCountTitle, BorderLayout.WEST);
		countPanel.add(usersCount, BorderLayout.EAST);
		statusPanel.add(countPanel, BorderLayout.PAGE_START);
		statusPanel.add(userListScroll, BorderLayout.CENTER);
		
		Container contentPane = getContentPane();
		contentPane.add(side, BorderLayout.CENTER);
		
		userMessages.setEditable(false);
		serviceMessages.setEditable(false);
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				main.setDividerLocation(0.5);
				side.setDividerLocation(0.8);
			}
		});
		
		setVisible(true);
		main.setDividerLocation(0.5);
		side.setDividerLocation(0.8);
	}
	
	public void update(Observable source, Object message) {		
		if (source instanceof Users) {
			usersCount.setText(new Integer(users.size()).toString());
		}
	}
}