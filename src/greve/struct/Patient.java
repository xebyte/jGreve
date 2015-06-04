package greve.struct;

public class Patient {
	private String patientID;
	private int start;
	private int end;

	public Patient(String pid, int start, int end) {
		this.patientID = pid;
		this.start = start;
		this.end = end;
	}

	public String getPatientID() {
		return patientID;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int compareTo(Patient p) {

		if(patientID.equals(p.getPatientID())
                && end == p.getEnd() && start == p.getStart()) {
			return 0;
		} else {
			return 1;
		}
	}

	public boolean equals(Patient p) {
		return compareTo(p) == 0;
	}

	public String toString() {
		return "[ " + patientID + ", " + start + ", " + end + " ]";
	}
}