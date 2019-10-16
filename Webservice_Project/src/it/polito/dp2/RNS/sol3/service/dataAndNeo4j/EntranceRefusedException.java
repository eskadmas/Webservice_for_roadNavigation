package it.polito.dp2.RNS.sol3.service.dataAndNeo4j;

public class EntranceRefusedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntranceRefusedException() {
	}

	public EntranceRefusedException(String message) {
		super(message);
	}

	public EntranceRefusedException(Throwable cause) {
		super(cause);
	}

	public EntranceRefusedException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntranceRefusedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
