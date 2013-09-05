package server.com.netcracker.romenskiy;

public interface ServerInterface {

	public void receive() throws Exception;
	
	public void send(String message) throws Exception;
}