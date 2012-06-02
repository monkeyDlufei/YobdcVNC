package yobdc.exception;

public class UnKnownHostExceptioin extends Exception {

	private String detail = null;
	public UnKnownHostExceptioin() {
		// TODO Auto-generated constructor stub
	}

	public UnKnownHostExceptioin(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
		detail = detailMessage;
	}

	public UnKnownHostExceptioin(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public UnKnownHostExceptioin(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return detail;
	}

}
