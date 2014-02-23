package extension;

// Discontinued. Was to be used in combination with Base.java, but turned out to be superfluous afterwards

import jenes.GenerationEventListener;
import jenes.GeneticAlgorithm;
import jenes.chromosome.IntegerChromosome;

public class Extension implements GenerationEventListener<IntegerChromosome> {
	
	// Generated with an early version of Base.java
	private final int[] exampleRoster = new int[]{3, 2, 8, 7, 0, 8, 9, 2, 6, 10, 2, 1, 5, 0, 8, 10, 9, 8, 2, 0, 6, 7, 10, 5, 4, 3, 5, 4, 9, 6, 9, 4, 7, 8, 2, 5, 6, 3, 9, 7, 2, 8, 0, 3, 5, 7, 6, 10, 4, 0, 0, 10, 9, 1, 3, 4, 8, 5, 9, 1, 1, 3, 7, 8, 4, 3, 0, 1, 10, 9, 10, 3, 6, 4, 5};
	
	private final IntegerChromosome BASE_SCHEDULE;
	private final int JOB_SPACING;
	
	private String[] persons;
	private String[] jobs;
	
	public Extension(String[] persons, String[] jobs, int jobSpacing,IntegerChromosome schedule, int popSize, int genLimit) {
		JOB_SPACING = jobSpacing;
		
		if (schedule == null) { // Test run!
			
		} else { // Actual run!
			BASE_SCHEDULE = schedule;
		}
	}

	@Override
	public void onGeneration(GeneticAlgorithm<IntegerChromosome> ga, long arg1) {
		nl.plusminos.alcleantraz.utils.Utils.reportGeneration(ga);
	}
	
	public static void main(String[] args) {
		
	}

}
