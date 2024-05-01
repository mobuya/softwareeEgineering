package server.exceptions;

public class InvalidCastleFieldException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCastleFieldException(String errorMessage) {
		super("InvalidCastleFieldException", errorMessage);
	}

}
