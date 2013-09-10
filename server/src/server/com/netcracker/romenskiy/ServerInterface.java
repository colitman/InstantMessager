/**
*Interface that describes basic operations for the server
*/

package server.com.netcracker.romenskiy;

import server.com.netcracker.romenskiy.messages.*;

public interface ServerInterface {

	/**
	*Receives messages from client
	*/
	public void receive() throws Exception;
	
	/**
	*Sends messages to client
	*/
	public void send(Message message) throws Exception;
}