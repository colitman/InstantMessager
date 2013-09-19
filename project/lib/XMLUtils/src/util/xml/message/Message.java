package util.xml.message;

/**
 * Contains a scecifical message that received/sended by xml
 */
public abstract class Message {
	
	private Object storage;
	private String type;
	
	public void setValue(Object value) {
		storage = value;
	}
	
	public Object getValue() {
		return storage;
	}
	
	public void setType(String t) {
		type = t; 
	};
	
	public String getType() {
		return type;
	}
}