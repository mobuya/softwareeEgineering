package server.exceptions;

public class ExceededNumberOfRegistrationsException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExceededNumberOfRegistrationsException(String errorMessage) {
		super("ExceededNumberOfRegistrationsException", errorMessage);
	}

}
