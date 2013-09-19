package util.xml.message;

/**
 * Contains a scecifical message that received/sended by xml
 */
public abstract class Message<T> {
	
	private T storage;
	private String type;
	
	public void setValue(Object value) {
		if (value instanceof T.getClass()) {
			storage = (T) value;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public T getValue() {
		return storage;
	}
	
	public void setType(String t) {
		type = t; 
	};
	
	public String getType() {
		return type;
	}
}