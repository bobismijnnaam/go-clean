package nl.plusminos.alcleantraz.peach;

public class Individual {
	private final short[] chrom;
	private int fitness;
	
	private boolean dirty = true;
	
	public Individual() {
		chrom = new short[Info.JOBS_THREEWEEKS * Info.getThreeWeeks()];
	}
	
	public Individual randomize() {
		for (int i = 0; i < chrom.length; i++) {
			chrom[i] = Info.nextPerson();
		}
		
		return this;
	}
	
	public void mutate() {
		int pos = Info.nextInt(chrom.length);
		chrom[pos] = Info.nextPerson();
		dirty = true;
	}

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		dirty = false;
		this.fitness = fitness;
	}
	
	public short[] getChromosome() {
		return chrom;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public String toString() {
		String result = "";
		for (int tw = 0; tw < Info.getThreeWeeks(); tw++) {
			for (int w = 0; w < 2; w++) {
				for (int j = 0; j < 5; j++) {
					result += chrom[tw * Info.JOBS_THREEWEEKS + w * Info.JOBS_EXCLUDING + j];
					result += "\t";
				}
				result += "\n";
			}
			
			for (int j = 0; j < 7; j++) {
				result += chrom[tw * Info.JOBS_THREEWEEKS + 2 * Info.JOBS_EXCLUDING + j];
				result += "\t";
			}
			
			if (tw != Info.getThreeWeeks() - 1) result += "\n";
		}
		
		return result;
	}
	
	public static final void crossover(Individual first, Individual second) {
		int pos = Info.nextInt(Info.getTotalJobs());
		
		short t;
		short[] chrom1 = first.getChromosome();
		short[] chrom2 = second.getChromosome();
		for (int i = pos; i < Info.getTotalJobs(); i++) {
			t = chrom1[i];
			chrom1[i] = chrom2[i];
			chrom2[i] = t;
		}
		
		first.dirty = true;
		second.dirty = true;
	}
}
