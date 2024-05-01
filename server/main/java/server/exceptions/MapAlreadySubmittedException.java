package server.exceptions;

public class MapAlreadySubmittedException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MapAlreadySubmittedException(String errorMessage) {
		super("MapAlreadySubmittedException", errorMessage);
	}

}
