package util.xml.message;

import java.util.Date;

public class MessageType {

	private Date time;
	private String from;
	private String to;
	private String text;
	
	public MessageType() {
	
	}
	
	public MessageType(Date date, String fromUser, String toUser, String message) {
		setTime(date);
		setFromUser(fromUser);
		setToUser(toUser);
		setMessage(message);
	}
	
	public void setTime(Date date) {
		this.time = date;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setFromUser(String userName) {
		from = userName;
	}	
	
	public String getFromUser() {
		return from;
	}
	
	public void setToUser(String userName) {
		to = userName;
	}
	
	public String getToUser() {
		return to;
	}
	
	public void setMessage(String message) {
		text = message;
	}
	
	public String getMessage() {
		return text;
	}
}