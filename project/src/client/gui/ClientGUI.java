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

import client.thread.*;

public class ClientGUI extends JFrame implements Observer, Runnable {
	
	private static final long serialVersionUID = 4229L;
	private static final String SIMPLE = "SimpleMessage";
	private static final String AUTH = "AuthorizeMessage";
	private static final String USERS = "UserListMessage";
	private static final String ANSWER = "AnswerMessage";
	private static final String HISTORY = "HistoryMessage";
	private static final String CONNECT = "ConnectUserMessage";
	
	private JTextField input;
	private JTextField userName;
	
	private JTextArea messages;
	private JScrollPane scroll;
	private JButton sendButton;
	private PrintWriter pipedOut;
	
	private JList<String> users;
	private DefaultListModel<String> usersModel;
	
	private DataOutputStream socketOut; 
	
	public ClientGUI(PrintWriter output, String name, DataOutputStream out) {
		super("Client");
		
		pipedOut = output;
		socketOut = out;
		
		userName = new JTextField();
		userName.setText(name);
	}
	
	@SuppressWarnings( "unchecked" )
	public void update(Observable source, Object object) {
		Message message = (Message) object;
		switch (message.getType()) {
			case SIMPLE:
				MessageType receivedMessage = (MessageType) message.getValue();
				
				Date time = receivedMessage.getTime();
				
				String from = receivedMessage.getFromUser();
				String to = receivedMessage.getToUser();
				String text = receivedMessage.getMessage();
				
				if (!text.isEmpty()) {
					messages.setText(messages.getText() + DateFormat.getDateInstance().format(time) + " : " + text + "\n");
				}
				break;
			case USERS:
				List<String> users = (List<String>) message.getValue();
				
				usersModel.clear();				
					
				for (int i = 0; i < users.size(); i++) {
					usersModel.addElement(users.get(i));
				}
				
				usersModel.removeElement(userName.getText());
				break;
			case HISTORY:
				List<String> history = (List<String>) message.getValue();
				
				messages.removeAll();
				for (String str : history) {
					messages.append(str + "\n");
				}
				break;
			
		}
	}
	
	@Override
	public void run() {
		createFrame();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(640, 480);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	private void createFrame() {
		input = new JTextField();
		
		messages = new JTextArea();
		messages.setEditable(false);
		
		scroll = new JScrollPane(messages);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener(input, pipedOut));
		
		input.addActionListener(new SendButtonListener(input, pipedOut));
		
		usersModel = new DefaultListModel<String>();
		
		users = new JList<String>(usersModel);
		
		ListSelectionListener listSelection = new ListSelectionListener() {
		
			@Override
			public void valueChanged(ListSelectionEvent event) {
				String name = users.getSelectedValue();
				
				try {
					Operations.sendConnectUser(name, socketOut);
				} catch (Exception e) {
					System.out.println("Can't connect to user");
				}
				//Operation.receive(socket.getInputStream());
			}
		};
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(input, BorderLayout.CENTER);
		panel.add(sendButton, BorderLayout.EAST);
		
		JPanel leftSplitPanel = new JPanel(new BorderLayout());
		leftSplitPanel.add(scroll, BorderLayout.CENTER);
		leftSplitPanel.add(panel, BorderLayout.SOUTH);
		
		JPanel rightSplitPanel = new JPanel(new BorderLayout());
		rightSplitPanel.setMinimumSize(new Dimension(150, 400));
		rightSplitPanel.add(users, BorderLayout.CENTER);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftSplitPanel, rightSplitPanel);
		splitPane.setDividerLocation(450);
		
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}	
	
	private class SendButtonListener implements ActionListener {
		
		private PrintWriter output;
		private JTextField input;
		
		public SendButtonListener(JTextField input, PrintWriter output) {
			this.output = output;
			this.input = input;
		}
		
		public void actionPerformed(ActionEvent event) {
			output.println(userName.getText());
			output.println(users.getSelectedValue());
			output.println(input.getText());
			input.setText("");
		}
	}
}