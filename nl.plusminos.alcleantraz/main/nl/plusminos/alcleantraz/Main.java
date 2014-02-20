package nl.plusminos.alcleantraz;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jenes.GeneticAlgorithm;
import jenes.algorithms.SimpleGA;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.tutorials.utils.Utils;

public class Main {
	private static int SCHEDULE_JOBS = 3;
	private static int SCHEDULE_WEEKS = 6; // Prefer a multiple of 3
	private static int SCHEDULE_SLOTS = SCHEDULE_JOBS * SCHEDULE_WEEKS;
	private static int SCHEDULE_PERSONS = 3;
	
	private static int CHROMOSOME_LENGTH = SCHEDULE_SLOTS;
	private static int POPULATION_SIZE = 2000;
	
	private static int GENERATION_LIMIT = 40000;
	
	private static GeneticAlgorithm<IntegerChromosome> ga;
	
	public static void printSchedule(Individual<IntegerChromosome> ind, String[] names) {
		int pos;
		int person;
		IntegerChromosome chrom = ind.getChromosome();
		
		System.out.println("Keuken\tKeuken\tKeuken\tWC\tDouche");
		
		for (int w = 0; w < SCHEDULE_WEEKS; w++) {
			for (int j = 0; j < SCHEDULE_JOBS; j++) {
				pos = AlcaFit.getJob(j, w);
				person = chrom.getValue(pos);
				System.out.print(names[person] + "\t");
			}
			System.out.println();
		}
		
		System.out.println();
	}
	
	public static void main(String[] args) {
		// Set some initial vars
		Individual<IntegerChromosome> sample = new Individual<IntegerChromosome>(
				new IntegerChromosome(CHROMOSOME_LENGTH, 0 ,SCHEDULE_PERSONS - 1));
		Population<IntegerChromosome> pop = new Population<IntegerChromosome>(sample, POPULATION_SIZE);
		
		// Set the amount of persons for the schedule
		AlcaFit.initialize(SCHEDULE_PERSONS, SCHEDULE_JOBS, SCHEDULE_WEEKS);
		
		// Initialize simple Genetic Algorithm
		ga = new SimpleGA<IntegerChromosome>(AlcaFit.fitness, pop, GENERATION_LIMIT);
		
		System.out.println("[Parameters]");
		System.out.println("Weeks: " + SCHEDULE_WEEKS);
		System.out.println("Persons: " + SCHEDULE_PERSONS);
		System.out.println("Jobs: " + SCHEDULE_JOBS);
		System.out.println("Population: " + POPULATION_SIZE);
		System.out.println("Maximum generation: " + GENERATION_LIMIT);
		System.out.println();
		
    	Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	System.out.println("Starting at: " + sdf.format(cal.getTime()) + "\n");
		
		// Calculate the schedule
		ga.evolve(); // FIGHT!
		
		// Get statistics
		Population.Statistics stats = ga.getCurrentPopulation().getStatistics();
		GeneticAlgorithm.Statistics algostats = ga.getStatistics();
		
		Group legals = stats.getGroup(Population.LEGALS);
		
		System.out.println("[Results]");
		
		// System.out.println(legals.get(0));
		Main.printSchedule(legals.get(0), args);
		
		Utils.printStatistics(stats);
		
		cal = Calendar.getInstance();
    	System.out.println("Terminated at: " + sdf.format(cal.getTime()) + "\n");
		
	}
}
