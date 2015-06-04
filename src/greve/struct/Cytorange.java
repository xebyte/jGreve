package greve.struct;

public class Cytorange {
	private String name;
	private int start;
	private int end;

	public Cytorange(String name, int start, int end) {
		this.name = name;
		this.start = start;
		this.end = end;
	}

	public String toString() {
		return "[ " + name + ": " + start + " - " + end + " ]";
	}
}