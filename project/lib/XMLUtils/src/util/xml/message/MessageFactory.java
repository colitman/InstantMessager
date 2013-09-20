package util.xml.message;

/**
 * Tool for making messages
 */
public class MessageFactory {
	
	private static final String[] supportedMessages = {"SimpleMessage", "AuthorizeMessage", "UserListMessage", "AnswerMessage", "HistoryMessage", "ConnectUserMessage"};
	
	private static MessageFactory instance = new MessageFactory();
	
	private MessageFactory() {}
	
	public static MessageFactory getInstance() {
		return instance;
	}
	
	public Message newMessage(String messageType) {
		for (int i = 0; i < supportedMessages.length; i++) {
			if (messageType.equals(supportedMessages[i])) {
				Message mess = new Message() {};
				mess.setType(messageType);
				return mess;
			}
		}
		return null;
	}
}