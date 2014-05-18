package nl.plusminos.alcleantraz.peach;

public class Fitness {
	public final int PERSONS;
	public final int THREEWEEKS;
	
	private short[] chrom;
	
	public Fitness(int persons, int threeWeeks) {
		PERSONS = persons;
		THREEWEEKS = threeWeeks;
	}
	
	public void setChromosome(short[] chrom) {
		this.chrom = chrom;
	}
	
	public int getRecurrenceQuality() {
		int quality;
		
		for (int tw = 0; tw < THREEWEEKS; tw++) {
			for (int w = tw * 3; w < (tw + 1) * 3; w++) {
				if (tw != 0 && w != 0) {
					
				}
			}
		}
		
		
		return quality;
	}

}
