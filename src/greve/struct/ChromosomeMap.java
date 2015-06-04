package greve.struct;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class ChromosomeMap extends HashMap<String, ArrayList<Gene>> {
	public ChromosomeMap() {
		super();
	}

	public void add(String chromosome, Gene gene) {
		if(get(chromosome) != null) {
			get(chromosome).add(gene);
		} else {
			ArrayList<Gene> genes = new ArrayList<Gene>();
			genes.add(gene);

			super.put(chromosome, genes);
		}
	}

	public ArrayList<Gene> get(String index) {
		return super.get(index);
	}

	public String toString() {
		String acc = "";
		for (Map.Entry<String, ArrayList<Gene>> entry : this.entrySet()) {
		    acc += entry.getKey() + " => ";

		    ArrayList<Gene> geneList = entry.getValue();
		    for(int i = 0; i < geneList.size(); i++) {
		    	acc += geneList.get(i) + ", ";
		    }

		    acc += "\n";
		}

		return acc;
	}

    public int compareTo(ChromosomeMap cm) {

        if(super.equals(cm)) {
            return 0;
        } else {
            return 1;
        }
    }

    public boolean equals(ChromosomeMap cm) {
        return compareTo(cm) == 0;
    }
}