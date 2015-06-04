package greve.struct;

public class Overlap {
	//["ID",thischr,thistype,1,maxi37[thischr],0]
	private String id;
	private String chromosome;
	private String type;
	private int min;
	private int max;
	private int count; // count ??

	private double score;
	private double poiBin;
	private double poiBinC;

	public Overlap(String id, String chromosome, String type, int min, int max, int count) {
		this.id = id;
		this.chromosome = chromosome;
		this.type = type;
		this.min = min;
		this.max = max;
		this.count = count;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getCount() {
		return count;
	}

	public String getID() {
		return id;
	}

	public String getChromosome() {
		return chromosome;
	}

	public String getType() {
		return type;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setPoiBin(double poiBin) {
		this.poiBin = poiBin;
	}

	public void setPoiBinC(double poiBinC) {
		this.poiBinC = poiBinC;
	}

	public double getScore() {
		return score;
	}

	public double getPoiBin() {
		return poiBin;
	}

	public double getPoiBinC() {
		return poiBinC;
	}

	public String toString() {
		return chromosome + "\t" + type + "\t" + min + "\t" + max + "\t" + count + "\t" + score + "\t" + poiBin + "\t" + poiBinC;
	}

    public String[] toPrint() {
        return new String[]{chromosome, type, min + "", max + "", count + "", score + "", poiBin + "", poiBinC + ""};
    }

	@Override
    public int hashCode() {
        return (id + chromosome + "").hashCode();
    }

    @Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Overlap))
            return false;
        if (obj == this)
            return true;

        Overlap o = (Overlap) obj;
        return (o.getID().equals(id) &&
        		o.getChromosome().equals(chromosome) &&
        		o.getType().equals(type) &&
        		o.getMin() == min &&
        		o.getMax() == max);
	}

	@Override
	public Overlap clone() {
        Overlap o = new Overlap(new String(id), new String(chromosome), new String(type), min, max, count);
        o.setScore(score);
        o.setPoiBin(poiBin);
        o.setPoiBinC(poiBinC);
        
        return o;
    }

}