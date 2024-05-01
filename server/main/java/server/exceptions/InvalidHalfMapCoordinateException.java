package server.exceptions;

public class InvalidHalfMapCoordinateException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidHalfMapCoordinateException(String errorMessage) {
		super("InvalidHalfMapCoordinateException", errorMessage);
	}

}
