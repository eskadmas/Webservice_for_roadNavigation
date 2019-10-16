package it.polito.dp2.RNS.sol3.service.dataAndNeo4j;

public class UnknownPlaceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownPlaceException() {
	}

	public UnknownPlaceException(String message) {
		super(message);
	}

	public UnknownPlaceException(Throwable cause) {
		super(cause);
	}

	public UnknownPlaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownPlaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
