package server.exceptions;

public class InvalidWaterFieldBorderCountException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidWaterFieldBorderCountException(int fieldLimit, long foundCount, String playerID) {
		super("InvalidWaterFieldBorderCountException", "Found too many water fields on Half Map borders: limit "
				+ fieldLimit + " but was found: " + foundCount + ". Player who violated this rule: " + playerID);
	}

}
