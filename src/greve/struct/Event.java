package greve.struct;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * A single event
 */
public class Event {
	private String type;
	private String label;
	private int bin;
	private HashMap<String, ArrayList<Patient>> list;
	private HashMap<String, HashMap<String, Double>> props;
	private HashMap<String, ArrayList<Overlap>> overlaps;
	private HashMap<String, ArrayList<ArrayList<String>>> ioverlaps;

	public String toString() {
		// return "[ " + type + ", " + label + ", " + bin + ", " + list + ", " + props + ", " + overlaps + ", " + ioverlaps + " ]";
		return "[ " + overlaps + " ]\n\n";
	}

	@Override
    public int hashCode() {
        return (type + "").hashCode();
    }

    // TODO: test against ALL the attributes
    @Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Event))
            return false;
        if (obj == this)
            return true;

        Event e = (Event) obj;
        return (e.getType().equals(type));
	}

	/**
	 * Initialize object
	 * @param  type Event type
	 * @return
	 */
	public Event(String type) {
		this.type = type;
		this.label = type;
		this.bin = 0;

		list = new HashMap<String, ArrayList<Patient>>();
		props = new HashMap<String, HashMap<String, Double>>();

		overlaps = new HashMap<String, ArrayList<Overlap>>();
		ioverlaps = new HashMap<String, ArrayList<ArrayList<String>>>();

		for(int i = 0; i < Common.CHROMOSOME_LIST.length; i++) {
			list.put(Common.CHROMOSOME_LIST[i], new ArrayList<Patient>());
			overlaps.put(Common.CHROMOSOME_LIST[i], new ArrayList<Overlap>());
			ioverlaps.put(Common.CHROMOSOME_LIST[i], new ArrayList<ArrayList<String>>());
		}
	}

	/**
	 * Add a patient to the event
	 * @param chr     Chromosome, adds a patient to the chromosome map for the particular chromosome
	 * @param patient Patient object
	 */
	public void add(String chr, Patient patient) {
		if(list.get(chr) == null) list.put(chr, new ArrayList<Patient>());

		for(int i = 0; i < list.get(chr).size(); i++) {
			if(patient.equals(list.get(chr).get(i))) {
				if(Common.DEBUG) System.out.println("Warning: " + patient.getPatientID() + " already is in the event of type " + type + " of " + chr);
				return;
			}
		}

		list.get(chr).add(patient);

		if(props.get(chr) == null) props.put(chr, new HashMap<String, Double>());
		HashMap<String, Double> prop = props.get(chr);

		for (Map.Entry<String, Double> entry : prop.entrySet()) {
		    if(entry.getKey().equals(patient.getPatientID())) {
		    	prop.put(entry.getKey(), entry.getValue() + (patient.getEnd() - patient.getStart()));
//		    	System.out.println("New val: " + entry.getValue() + ((double) (patient.getEnd() - patient.getStart())));
		    	return;
		    }
		}

//		System.out.println("New var: " + ((double) patient.getEnd() - patient.getStart()));
		prop.put(patient.getPatientID(), (double) patient.getEnd() - patient.getStart());
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<String> getChromosomes() {
		ArrayList<String> chrs = new ArrayList<String>();

		for (String chr : list.keySet()) {
		    chrs.add(chr);
		}

		return chrs;
	}

	public HashMap<String, HashMap<String, Double>> getProps() {
		return props;
	}

	public ArrayList<Overlap> getOverlaps(String chr) {
		return overlaps.get(chr);
	}

	public HashMap<String, ArrayList<Overlap>> getOverlaps() {
		return overlaps;
	}

	public void setOverlaps(HashMap<String, ArrayList<Overlap>> newOverlaps) {
		overlaps = newOverlaps;
	}

	public HashMap<String, ArrayList<Patient>> getList() {
		return list;
	}

	public ArrayList<Patient> getList(String chr) {
		return list.get(chr);
	}

	/**
	 * Routine to create overlaps from a list of events
	 * @param bin
	 * @param patients
	 */
	public void makeOverlap(int bin, ArrayList<Patient> patients, String chr, HashMap<String, Integer> maxi, HashMap<String, Integer> maxi37) {
		this.bin = bin;

		HashMap<String, Double> props = new HashMap<String, Double>();
//		System.out.println("\tEvent type " + type);

		for(int i = 0; i < Common.CHROMOSOME_LIST.length; i++) { // √
//			System.out.println("\tChromosome " + Common.CHROMOSOME_LIST[i]);

			ArrayList<Overlap> arr = new ArrayList<Overlap>();
			arr.add(new Overlap("ID", Common.CHROMOSOME_LIST[i], type, 1, maxi37.get(Common.CHROMOSOME_LIST[i]), 0));
			overlaps.put(Common.CHROMOSOME_LIST[i], arr);
			
			ioverlaps.put(Common.CHROMOSOME_LIST[i], new ArrayList<ArrayList<String>>());

			for(int k = 0; k < patients.size(); k++) {
				props.put(patients.get(k).getPatientID(), 0.);
			}

			for(int k = 0; k < list.get(Common.CHROMOSOME_LIST[i]).size(); k++) { // √
//				System.out.println("\t\tPatient " + list.get(Common.CHROMOSOME_LIST[i]).get(k));
				// if(Common.DEBUG) System.out.println("-----------------");
				// if(Common.DEBUG) System.out.println("Add to overlaps");

				// GV_add_overlap
				addOverlap(Common.CHROMOSOME_LIST[i], props, list.get(Common.CHROMOSOME_LIST[i]).get(k), bin);
				// k++;
			}

			// if(Common.DEBUG) System.out.println("End overlap");

			// tmp = props;
		}
	}

	/**
	 * Routine to add the current event to my overlap
	 */
	public void addOverlap(String chromosome, HashMap<String, Double> props, Patient patient, int bin) {
		String sam = patient.getPatientID();
		int cStart = patient.getStart();
		int cEnd = patient.getEnd();

		// TODO: HEAVY REFACTORING, PASS THESE ARRAYS AS PARAMS INSTEAD OF ACCESSING THEM LIEK THSI
		ArrayList<Overlap> overlaps = this.overlaps.get(chromosome);
		ArrayList<ArrayList<String>> ioverlaps = this.ioverlaps.get(chromosome);

		// if(Common.DEBUG) System.out.println(chromosome + " -> " + this.overlaps.get(chromosome) + "\n");
		// [["ID",thischr,thistype,1,maxi37[thischr],0]]


		for(int i = 0; i < overlaps.size() && cEnd >= overlaps.get(i).getMin(); i++) {

//            if(chromosome.equals("chr1") && type.equals("Gain") && overlaps.get(i).getMin() == 11228741 && overlaps.get(i).getMax() == 11631364) {
//                System.out.println("DEBUG START");
//                System.out.println("At " + i + " " + patient);
////                System.out.println(e.getProps());
////                System.exit(0);
//            }



			int oStart = overlaps.get(i).getMin();
			int oEnd = overlaps.get(i).getMax();

			Overlap added = overlaps.get(i).clone();

			ArrayList<String> innerOverlaps;

			try {
				innerOverlaps = ioverlaps.get(i);
			} catch(IndexOutOfBoundsException e) {
				innerOverlaps = new ArrayList<String>();
				ioverlaps.add(i, innerOverlaps);
			}

//			System.out.println("\t\t\tOverlap check: cStart = " + cStart + ", oStart = " + oStart + ", cEnd = " + cEnd + ", oEnd = " + oEnd);
			// over[i] = {
			// 	0: id
			// 	1: chr
			// 	2: type
			// 	3: start
			// 	4: end
			// 	5: noidea (count?)
			// }
			// Case where the new events completly includes the old one
			if (cStart<=oStart && cEnd>=oEnd) {
//				System.out.println("\t\t\tCase 1");
				// if(Common.DEBUG) System.out.println("Case 1: At " + i + ", interval " + cStart + "-" + cEnd + " " + sam);

				overlaps.get(i).setCount(overlaps.get(i).getCount() + 1);
				
				innerOverlaps.add(sam);

				props.put(patient.getPatientID(), props.get(sam) + ((double) overlaps.get(i).getMax()) - ((double) overlaps.get(i).getMin()));
			}
			// Case where the new events is completly included in the old one
			else if(oStart < cStart && cEnd < oEnd) {
//				System.out.println("\t\t\tCase 2");
				// if(Common.DEBUG) System.out.println("Case 2: At " + i + ", interval " + cStart + "-" + cEnd + " " + sam);
				Overlap added2 = added.clone();
				overlaps.add(i, added);

                ioverlaps.add(i, (ArrayList<String>) ioverlaps.get(i).clone());

      			overlaps.get(i).setMax(cStart-1);
      			overlaps.add(i+2, added2);

      			// quickfix -> for further code analysis

                // TODO: FOR FURTHER INVESTIGATION
                if(ioverlaps.size() < i+2) {
                    ioverlaps.add(i+1, new ArrayList<String>());
                }


      			ioverlaps.add(i+2, (ArrayList<String>) ioverlaps.get(i).clone());

      			overlaps.get(i+2).setMin(cEnd+1);
      			overlaps.get(i+1).setCount(added.getCount() + 1);

      			ioverlaps.get(i+1).add(sam);

//      			System.out.println(patient.toString());
//      			System.out.println(props.toString());
//      			System.out.println(chromosome);

      			props.put(patient.getPatientID(), props.get(sam) + ((double) overlaps.get(i+1).getMax()) - ((double) overlaps.get(i+1).getMin()));
				
				overlaps.get(i+1).setMin(cStart);
				overlaps.get(i+1).setMax(cEnd);

				i+=2;
			} 
			// Case where the new events starts within an old one
			else if(oStart < cStart && oEnd <= cEnd && cStart <= oEnd) {
//				System.out.println("\t\t\tCase 3");
				// if(Common.DEBUG) System.out.println("Case 3: At " + i + ", interval " + cStart + "-" + cEnd + " " + sam);

				overlaps.add(i, added.clone());

      			ioverlaps.add(i,(ArrayList<String>) ioverlaps.get(i).clone());

      			overlaps.get(i).setMax(cStart-1);
      			overlaps.get(i+1).setCount(added.getCount() + 1);

                if(ioverlaps.size() < i+2) {
                    ioverlaps.add(i+1, new ArrayList<String>());
                }

      			ioverlaps.get(i+1).add(sam);

      			props.put(patient.getPatientID(), props.get(sam) + ((double) overlaps.get(i+1).getMax()) - ((double) overlaps.get(i+1).getMin()));
				
				overlaps.get(i+1).setMin(cStart);
				i++;
			} 
			// Case where the new events ends within an old one
			else if(oStart >= cStart && oEnd > cEnd && cEnd >= oStart) {
//				System.out.println("\t\t\tCase 4");
				// if(Common.DEBUG) System.out.println("Case 4: At " + i + ", interval " + cStart + "-" + cEnd + " " + sam);

				overlaps.add(i, added.clone());

				ioverlaps.add(i,(ArrayList<String>) ioverlaps.get(i).clone());

				overlaps.get(i).setCount(added.getCount() + 1);
				ioverlaps.get(i).add(sam);

				props.put(patient.getPatientID(), props.get(sam) + ((double) overlaps.get(i+1).getMax()) - ((double) overlaps.get(i+1).getMin()));
				
				overlaps.get(i).setMax(cEnd);
				overlaps.get(i+1).setMin(cEnd+1);
				// System.out.println(i+1);
				// System.out.println(overlaps.get(i+1));
				i++;
			}




//            if(chromosome.equals("chr1") && type.equals("Gain") && overlaps.get(i).getMin() == 11228741 && overlaps.get(i).getMax() == 11631364) {
//
//                System.out.println(overlaps.get(i));
//                System.out.println(props.get(patient.getPatientID()));
////                System.out.println(e.getProps());
//                System.out.println("DEBUG END");
//                System.exit(0);
//            }
		}


	}
}