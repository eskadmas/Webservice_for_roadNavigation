package it.polito.dp2.RNS.sol3.service.dataAndNeo4j;

public class WrongPlaceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongPlaceException() {
	}

	public WrongPlaceException(String message) {
		super(message);
	}

	public WrongPlaceException(Throwable cause) {
		super(cause);
	}

	public WrongPlaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongPlaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
