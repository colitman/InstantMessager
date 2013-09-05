package client.com.netcracker.romenskiy;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ClientGUI extends JFrame implements Runnable, Observer {

	private JTextField input = new JTextField();
	private JTextArea messages = new JTextArea();
	private JScrollPane scroll = new JScrollPane(messages);
	private JButton send = new JButton("Send");
	private PrintWriter output;

	public ClientGUI(PrintWriter output) {
		this.output = output;
	}
	
	public void run() {
		createFrame();
	}
	
	private void createFrame() {
		setTitle("InstantMessager");
		setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(input, BorderLayout.CENTER);
		panel.add(send, BorderLayout.EAST);
		
		Container contentPane = getContentPane();
		contentPane.add(panel, BorderLayout.SOUTH);
		contentPane.add(new JScrollPane(messages), BorderLayout.CENTER);

		send.addActionListener(new SendButtonListener(input, output));
		input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					send.doClick();
				}
			}
		});
		messages.setEditable(false);
		setVisible(true);
	}
	
	public void update(Observable source, Object message) {
		messages.setText(messages.getText() + (String)message + "\n");
	}
}