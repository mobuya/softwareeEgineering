package server.exceptions;

public class InvalidCastleCountPresentException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCastleCountPresentException(String errorMessage) {
		super("CastlePositionNotPresentException", errorMessage);
	}

}
