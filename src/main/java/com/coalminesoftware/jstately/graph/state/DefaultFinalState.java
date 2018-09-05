package com.coalminesoftware.jstately.graph.state;

/** Basic {@link FinalState} implementation that allows for a fixed value to be used as the state's result. */
public class DefaultFinalState<Result> extends DefaultState<Result> implements FinalState<Result> {
	private Result result;

	public DefaultFinalState() {
		super();
	}

	public DefaultFinalState(String description, Result result) {
		super(description);
		this.result = result;
	}

	@Override
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
}
