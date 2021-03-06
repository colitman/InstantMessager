package server.com.netcracker.romenskiy;

import server.com.netcracker.romenskiy.messages.*;
import java.util.*;
import java.io.*;
import util.xml.*;
import util.xml.message.*;
import org.apache.log4j.*;

public class RoomObserver implements Observer {

	private File file;
	private LinkedList<MessageType> list = new LinkedList<MessageType>();
	private static final Logger logger = Logger.getLogger("im.server");
	
	public RoomObserver(File file) {
		this.file = file;
	}

	public void update(Observable source, Object object) {
		MessageType message = (MessageType)object;
		add(message);
		saveFile(list, file);
	}
	
	private void add(MessageType message) {
		if(list.size() < 10) {
			list.add(message);
		} else {
			list.removeFirst();
			list.addLast(message);
		}
	}
	
	private void saveFile(LinkedList<MessageType> message, File file) {
		Operations.saveServerHistory(list, file);
		logger.info("History file renewed");
	}

}