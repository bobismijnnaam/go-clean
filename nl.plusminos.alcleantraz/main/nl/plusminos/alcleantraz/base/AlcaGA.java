package nl.plusminos.alcleantraz.base;

import jenes.GeneticAlgorithm;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.population.Population;

public class AlcaGA extends GeneticAlgorithm<IntegerChromosome> {
	
	private final AlcaFit FITNESS;
	
	public AlcaGA(AlcaFit fitness, Population<IntegerChromosome> pop, int generationLimit) {
		super(fitness, pop, generationLimit);
		
		FITNESS = fitness;
	}
	
	@Override
	protected boolean end() {
		Population.Statistics<IntegerChromosome> stat = this.getCurrentPopulation().getStatistics();
		Population.Statistics.Group<IntegerChromosome> schedules = stat.getGroup(Population.LEGALS);
		
		Individual<IntegerChromosome> candidate = stat.getGroup(Population.LEGALS).get(0);
		IntegerChromosome chrom = candidate.getChromosome();
		
		boolean noDoubles = FITNESS.hasNoDoublesPerWeek(chrom);
		boolean noOverlap = FITNESS.hasNoOverlap(chrom);
		boolean hasEachJobOnce = FITNESS.hasPerfectAssignment(chrom);
		float distribution = FITNESS.gradeDistribution(chrom);
		
		return hasEachJobOnce && noOverlap && noDoubles; // && distribution <= 3.0;
	}
}
