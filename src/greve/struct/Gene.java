package greve.struct;

public class Gene {
	private String name;
	private int start, end;

	public Gene(String name, int start, int end) {
		this.name = name;
		this.start = start;
		this.end = end;
	}

	public Gene(String name, int start) {
		this(name, start, start);
	}

	public String toString() {
		return name;
	}

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public int compareTo(Gene g) {

        if(name.equals(g.getName())
                && end == g.getEnd() && start == g.getStart()) {
            return 0;
        } else {
            return 1;
        }
    }

    public boolean equals(Gene g) {
        return compareTo(g) == 0;
    }
}