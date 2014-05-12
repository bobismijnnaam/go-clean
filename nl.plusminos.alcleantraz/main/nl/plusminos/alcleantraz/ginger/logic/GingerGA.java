package nl.plusminos.alcleantraz.ginger.logic;

import jenes.GeneticAlgorithm;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;
import jenes.population.Population;

public class GingerGA extends GeneticAlgorithm<IntegerChromosome> {
	public final Fitness<IntegerChromosome> FITNESS;
	
	public GingerGA(Fitness<IntegerChromosome> fitness, Population<IntegerChromosome> pop, int generationLimit) {
		super(fitness, pop, generationLimit);
		
		FITNESS = fitness;
	}
	
	@Override
	public boolean end() {
		Population.Statistics<IntegerChromosome> stat = this.getCurrentPopulation().getStatistics();
		Population.Statistics.Group<IntegerChromosome> schedules = stat.getGroup(Population.LEGALS);
		
		Individual<IntegerChromosome> candidate = stat.getGroup(Population.LEGALS).get(0);
		IntegerChromosome chrom = candidate.getChromosome();
		
//		boolean noDoubles = FITNESS.hasNoDoublesPerWeek(chrom);
//		boolean noOverlap = FITNESS.hasNoOverlap(chrom);
//		boolean hasEachJobOnce = FITNESS.hasPerfectAssignment(chrom);
//		float distribution = FITNESS.gradeDistribution(chrom);
		
		
		return false;
//		return noDoubles && hasEachJobOnce; // && distribution <= 3.0;
	}
}
