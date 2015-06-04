package greve.struct;

/**
 * Describes the positions of cytogenetic bands within a chromosome
 */
public class Cytoband {
	private String chrom;
	private int chromStart;
	private int chromEnd;
	private String name;
	private String gieStain;

	/**
	 * Initializes a cytoband object
	 * @param  chrom      Chromosome number
	 * @param  chromStart Start position
	 * @param  chromEnd   End position
	 * @param  name       Name of cytoband
	 * @param  gieStain   Giesma stain results
	 */
	public Cytoband(String chrom, int chromStart, int chromEnd, String name, String gieStain) {
		this.chrom = chrom;
		this.chromStart = chromStart;
		this.chromEnd = chromEnd;
		this.name = name;
		this.gieStain = gieStain;
	}

	public String getChrom() {
		return chrom;
	}

	public int getChromStart() {
		return chromStart;
	}

	public int getChromEnd() {
		return chromEnd;
	}

	public String getName() {
		return name;
	}

	public String getGieStain() {
		return gieStain;
	}

	public String toString() {
		return "[ " + name + ": " + chromStart + " - " + chromEnd + ", " + gieStain + " ]";
	}

	@Override
    public int hashCode() {
        return (name + chrom + "").hashCode();
    }

    @Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Cytoband))
            return false;
        if (obj == this)
            return true;

        Cytoband cb = (Cytoband) obj;
        return (cb.getChrom().equals(chrom) &&
           cb.getChromStart() == chromStart &&
           cb.getChromEnd() == chromEnd &&
           cb.getName().equals(name) &&
           cb.getGieStain().equals(gieStain));
	}
}