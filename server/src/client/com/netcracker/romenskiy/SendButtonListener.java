package client.com.netcracker.romenskiy;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class SendButtonListener implements ActionListener {

	private JTextField input;
	private PrintWriter output = null;

	public SendButtonListener(JTextField input, PrintWriter output) {
		this.input = input;
		this.output = output;
	}
	
	public void actionPerformed(ActionEvent ae) {
		String message = input.getText();
		if (message != null && !message.isEmpty()) {
			output.println(message);
			input.setText("");
		}
	}
}