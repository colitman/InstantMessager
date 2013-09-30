/**
*Interface that describes basic operations for the server
*/

package server.com.netcracker.romenskiy;

import util.xml.*;
import util.xml.message.*;

public interface ServerInterface {

	/**
	*Receives messages from client
	*/
	public void receive() throws Exception;
}