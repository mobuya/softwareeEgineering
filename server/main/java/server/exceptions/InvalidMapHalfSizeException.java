package server.exceptions;

public class InvalidMapHalfSizeException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMapHalfSizeException(String errorMessage) {
		super("InvalidMapHalfSize", errorMessage);
	}

}
