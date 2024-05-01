package server.exceptions;

public class MapContainsUnreachableFieldException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MapContainsUnreachableFieldException(String errorMessage) {
		super("MapContainsUnreachableFieldException", errorMessage);
	}

}
