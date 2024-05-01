package server.exceptions;

public class InvalidPlayerIDForRunningGameException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPlayerIDForRunningGameException(String errorMessage) {
		super("InvalidPlayerIDForRunningGameException", errorMessage);
	}

}
