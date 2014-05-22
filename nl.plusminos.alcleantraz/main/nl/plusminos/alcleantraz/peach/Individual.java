package nl.plusminos.alcleantraz.peach;

import nl.plusminos.alcleantraz.utils.HighQualityRandom;

public class Individual {
	public final int THREEWEEKS;
	
	private final short[] chrom;
	private int fitness;
	
	private boolean dirty = true;
	
	public Individual(int threeWeeks) {
		THREEWEEKS = threeWeeks;
		chrom = new short[Info.JOBS_THREEWEEKS * THREEWEEKS];
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
	
	public static final void crossover(Individual first, Individual second) {
		int pos = Info.nextInt(Info.getTotalJobs());
		
		// TODO
	}
}
