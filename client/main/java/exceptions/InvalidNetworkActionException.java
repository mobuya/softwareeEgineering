package exceptions;

public class InvalidNetworkActionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidNetworkActionException() {
		super("Client Action sent is invalid!");
	}

	public InvalidNetworkActionException(String exceptionMessage) {
		super("Client Action sent is invalid! : " + exceptionMessage);
	}

}
