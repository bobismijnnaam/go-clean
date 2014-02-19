package nl.plusminos.alcleantraz;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jenes.GenerationEventListener;
import jenes.GeneticAlgorithm;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.stage.AbstractStage;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;
import jenes.tutorials.utils.Utils;

public class Main implements GenerationEventListener<IntegerChromosome> {
	private static int SCHEDULE_JOBS = 5;
	private static int SCHEDULE_WEEKS = 12; // Prefer a multiple of 3
	private static int SCHEDULE_SLOTS = SCHEDULE_JOBS * SCHEDULE_WEEKS;
	private static int SCHEDULE_PERSONS = 11;
	
	private static int CHROMOSOME_LENGTH = SCHEDULE_SLOTS;
	private static int POPULATION_SIZE = 4000;
	private static int TARGET_SCORE = 3;
	
	private static int GENERATION_LIMIT = 10000;
	
	private static GeneticAlgorithm<IntegerChromosome> ga;
	
	private String[] args = null;
	
	public Main(String[] args) {
		this.args = args;
	}
	
	public static void printSchedule(Individual<IntegerChromosome> ind, String[] names, AlcaFit fitness) {
		int pos;
		int person;
		IntegerChromosome chrom = ind.getChromosome();
		
		System.out.println("Keuken\tKeuken\tKeuken\tWC\tDouche");
		
		for (int w = 0; w < SCHEDULE_WEEKS; w++) {
			for (int j = 0; j < SCHEDULE_JOBS; j++) {
				pos = fitness.getJob(j, w);
				person = chrom.getValue(pos);
				System.out.print(names[person] + "\t");
			}
			System.out.println();
		}
		
		System.out.println("\nConstraints:");
		System.out.println("Has no duplicates in every week: " + fitness.hasNoDoublesPerWeek(chrom));
		System.out.println("Has no consequent identical jobs: " + fitness.hasNoOverlap(chrom));
		System.out.println("Everyone has each job at least once: " + fitness.everyoneHasEachJobOnce(chrom));
		
		System.out.println();
	}
	
	@Override
	public void onGeneration(GeneticAlgorithm<IntegerChromosome> ga, long arg1) {
		int gen = ga.getGeneration();
		if ((gen & 1023) == 0) {
			System.out.println("Generation: " + ga.getGeneration() + " Minutes: " + (ga.getStatistics().getExecutionTime() / 1000 / 60));
		}
	}
	
	public void run() {
		// Set some initial vars
		Individual<IntegerChromosome> sample = new Individual<IntegerChromosome>(
				new IntegerChromosome(CHROMOSOME_LENGTH, 0 ,SCHEDULE_PERSONS - 1));
		Population<IntegerChromosome> pop = new Population<IntegerChromosome>(sample, POPULATION_SIZE);
		
		// Initialize fitness instance
		AlcaFit fitness = new AlcaFit(SCHEDULE_PERSONS, SCHEDULE_JOBS, SCHEDULE_WEEKS, null);
		
		// Setup genetic algorithm
		ga = new AlcaGA(fitness, pop, GENERATION_LIMIT, TARGET_SCORE);
	
		AbstractStage<IntegerChromosome> selection = new TournamentSelector<IntegerChromosome>(3);
		AbstractStage<IntegerChromosome> crossover = new OnePointCrossover<IntegerChromosome>(0.8);
		AbstractStage<IntegerChromosome> mutation = new SimpleMutator<IntegerChromosome>(0.02);
		ga.addStage(selection);
		ga.addStage(crossover);
		ga.addStage(mutation);
		
		ga.addGenerationEventListener(this);
		
		// Startup info
		System.out.println("{{ALCLEANTRAZ ALPHA}}\n");
		System.out.println("[Parameters]");
		System.out.println("Weeks: " + SCHEDULE_WEEKS);
		System.out.println("Persons: " + SCHEDULE_PERSONS);
		System.out.println("Jobs: " + SCHEDULE_JOBS);
		System.out.println("Population: " + POPULATION_SIZE);
		System.out.println("Maximum generation: " + GENERATION_LIMIT);
		System.out.println("Target score: " + TARGET_SCORE);
		System.out.println();
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Starting at: " + sdf.format(cal.getTime()) + "\n");
		
		// Calculate the schedule/////////
		ga.evolve(); // FIGHT!////////////
		//////////////////////////////////
		
		// Get statistics
		Population.Statistics<IntegerChromosome> stats = ga.getCurrentPopulation().getStatistics();
		GeneticAlgorithm.Statistics algostats = ga.getStatistics();
		
		Group<IntegerChromosome> legals = stats.getGroup(Population.LEGALS);
		
		System.out.println("\n[Results]\nSchedule:");
		
		Main.printSchedule(legals.get(0), args, fitness);
		
		System.out.println("Statistics:");
		Utils.printStatistics(stats);
		
		System.out.println("Generations needed: " + algostats.getGenerations());
		System.out.println("Algorithm execution time (minutes): " + algostats.getExecutionTime() / 1000 / 60);
	}
	
	public static void main(String[] args) {
		Main main = new Main(args);
		
		main.run();
	}
}
