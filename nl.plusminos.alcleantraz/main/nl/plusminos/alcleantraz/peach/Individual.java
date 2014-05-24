package nl.plusminos.alcleantraz.peach;

public class Individual {
	private final short[] chrom;
	private int fitness;
	
	private boolean dirty = true;
	
	public Individual() {
		chrom = new short[Info.JOBS_THREEWEEKS * Info.getThreeWeeks()];
	}
	
	public Individual(Individual original) {
		this();
		System.arraycopy(original.chrom, 0, chrom, 0, original.chrom.length);
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
	
	public String toString(boolean simple) {
		if (simple) {
			String result = "";
			for (int tw = 0; tw < Info.getThreeWeeks(); tw++) {
				for (int w = 0; w < 3; w++) {
					for (int j = 0; j < ((w == 2) ? 7 : 5); j++) {
						result += chrom[tw * Info.JOBS_THREEWEEKS + w * Info.JOBS_EXCLUDING + j];
					}
				}
			}
			
			return result;
		} else {
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
	
	public static final Individual getOffspring(Individual first, Individual second) {
		int pos = Info.nextInt(Info.getTotalJobs());
		Individual ind = new Individual();
		
		for (int i = 0; i < pos; i++) {
			ind.chrom[i] = first.chrom[i];
		}
		for (int i = pos; i < Info.getTotalJobs(); i++) {
			ind.chrom[i] = second.chrom[i];
		}
		
		return ind;
	}
	
	public static void main(String[] args) {
		Info.setup(2, 1);
		
		Individual one = new Individual().randomize();
		Individual two = new Individual().randomize();
		
		System.out.println("Before:");
		System.out.println(one.toString(true));
		System.out.println(two.toString(true));
		
		Individual.crossover(one, two);
		
		System.out.println("After:");
		System.out.println(one.toString(true));
		System.out.println(two.toString(true));
	}
}
