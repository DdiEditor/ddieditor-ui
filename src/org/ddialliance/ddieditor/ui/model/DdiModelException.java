package org.ddialliance.ddieditor.ui.model;

public class DdiModelException extends Exception {
	public enum Sevrity {
		INFO, WARNING, FATAL
	};

	String msg;
	Sevrity sevrity;
	Throwable throwable;

	public DdiModelException(String msg, Sevrity sevrity) {
		super();
		this.msg = msg;
		this.sevrity = sevrity;
	}
	
	public DdiModelException(String msg, Sevrity sevrity, Throwable throwable) {
		super();
		this.msg = msg;
		this.sevrity = sevrity;
		this.throwable = throwable;
	}
	
	@Override
	public String getMessage() {
		return msg;
	}

	public String getMsg() {
		return msg;
	}

	public Sevrity getSevrity() {
		return sevrity;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
