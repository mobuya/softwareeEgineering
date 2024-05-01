package server.exceptions;

public class InvalidMapFieldCountException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMapFieldCountException(String errorMessage) {
		super("InvalidMapFieldCountException", errorMessage);
	}

}
