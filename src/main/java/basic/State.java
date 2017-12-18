package basic;

public class State {
	public final String A = "A", B = "B", C = "C", D = "D", E = "E", F = "F", G = "G", H = "H", I = "I", J = "J",
			K = "K", L = "L", M = "M", N = "N";
	private final String[] arr = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N" };

	private String currentState;

	public State() {
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	public String getNextNaturalState() {
		if (currentState == null)
			return null;
		int index = 0;
		for (String state : arr) {
			if (currentState.equals(state))
				return arr[index + 1];
			index++;
		}
		return null;
	}

}
