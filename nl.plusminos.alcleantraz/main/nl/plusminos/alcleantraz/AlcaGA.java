package nl.plusminos.alcleantraz;

import jenes.GeneticAlgorithm;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Population;

public class AlcaGA extends GeneticAlgorithm<IntegerChromosome> {
	
	private final int TARGET_SCORE;
	
	public AlcaGA(AlcaFit fitness, Population<IntegerChromosome> pop, int generationLimit, int targetScore) {
		super(fitness, pop, generationLimit);
		
		TARGET_SCORE = targetScore;
	}
	
	@Override
	protected boolean end() {
		jenes.population.Population.Statistics stat = this.getCurrentPopulation(). getStatistics();
		Population.Statistics.Group schedules = stat.getGroup(Population.LEGALS);
		return (int) schedules.getMax()[0] >= TARGET_SCORE;
	}
}
