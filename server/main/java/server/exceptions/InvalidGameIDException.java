package server.exceptions;

public class InvalidGameIDException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidGameIDException(String errorMessage) {
		super("InvalidGameIDException", errorMessage);
	}

}
