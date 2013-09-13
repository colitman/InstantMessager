package client;

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

public class ClientGUI extends JFrame implements Observer, Runnable {
	
	private static final long serialVersionUID = 4229L;
	
	private JTextField input;
	private JTextField userName;
	
	private JTextArea messages;
	private JScrollPane scroll;
	private JButton sendButton;
	private PrintWriter output;
	
	private JList<String> users;
	private DefaultListModel<String> usersModel;
	
	public ClientGUI(PrintWriter output) {
		super("Client");
		
		this.output = output;
	}
	
	public void update(Observable source, Object message) {
		if (source instanceof InputThread) {
			if (message instanceof Message) {
				Message receivedMessage = (Message) message;
				
				Date time = receivedMessage.getTime();
				
				String from = receivedMessage.getFromUser();
				String to = receivedMessage.getToUser();
				String text = receivedMessage.getMessage();
				
				if (!text.isEmpty()) {
					messages.setText(messages.getText() + DateFormat.getDateInstance().format(time) + " : " + text + "\n");
				}
			} else {
				throw new IllegalArgumentException();
			}
		}
		if (source instanceof ListenUsersThread) {
			if (message instanceof List) {
				List<?> list = (List<?>) message;
				
				usersModel.clear();				
					
				for (int i = 0; i < list.size(); i++) {
					usersModel.addElement((String) list.get(i));
				}
				
				usersModel.removeElement(userName.getText());
				
			} else {
				throw new IllegalArgumentException();
			}
		}
		// if (source instanceof HistoryThread) {
			// if (message instanceof Map) {
				// Map<> map = (Map<>) message;
			// }
		// }
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
		sendButton.addActionListener(new SendButtonListener(input, output));
		
		input.addActionListener(new SendButtonListener(input, output));
		
		usersModel = new DefaultListModel<String>();
		
		users = new JList<String>(usersModel);
		
		ListSelectionListener listSelection = new ListSelectionListener() {
		
			@Override
			public void valueChanged(ListSelectionEvent event) {
				String name = users.getSelectedValue();
				
				//XMLUtils.connectUser(String userName, scoket.getOutputStream());
				//XMLUtils.receiveHistory(Map history, socket.getInputStream());
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