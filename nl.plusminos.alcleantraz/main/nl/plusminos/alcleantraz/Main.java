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
	private static int SCHEDULE_WEEKS = 15; // Prefer a multiple of 3
	private static int SCHEDULE_SLOTS = SCHEDULE_JOBS * SCHEDULE_WEEKS;
	private static int SCHEDULE_PERSONS = 3;
	
	private static int CHROMOSOME_LENGTH = SCHEDULE_SLOTS;
	private static int POPULATION_SIZE = 1000;
	private static int TARGET_SCORE = SCHEDULE_SLOTS; // Slots + 0 + 0 (you don't want overlaps/doubles)
	
	private static int GENERATION_LIMIT = 30000;
	
	private static GeneticAlgorithm<IntegerChromosome> ga;
	
	private String[] args = null;
	
	public Main(String[] args) {
		this.args = args;
	}
	
	public void printSchedule(Individual<IntegerChromosome> ind, String[] names, AlcaFit fitness) {
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
		System.out.println("Has no duplicates in every week: " + fitness.hasNoDoublesPerWeek(chrom) + " (" + fitness.countDoublesPerWeek(chrom) + ")");
		System.out.println("Has no consequent identical jobs: " + fitness.hasNoOverlap(chrom) + " (" + fitness.countOverlaps(chrom) + ")");
		System.out.println("Everyone has each job at least once: " + fitness.hasPerfectAssignment(chrom) + " (" + fitness.countPerfectAssignment(chrom) + ")");
		System.out.println("Distribution (ideal = 0): " + fitness.gradeDistribution(chrom));
		System.out.println("\nActual distribution of work: ");
		
		int jobs = 0;
		for (int p = 0; p < SCHEDULE_PERSONS; p++) {
			jobs = 0;
			for (int j = 0; j < SCHEDULE_SLOTS; j++) {
				if (chrom.getValue(j) == p) {
					jobs++;
				}
			}
			System.out.println("\t" + args[p] + ": " + jobs);
		}
		
		System.out.println();
	}
	
	@Override
	public void onGeneration(GeneticAlgorithm<IntegerChromosome> ga, long arg1) {
		int gen = ga.getGeneration();
		if ((gen & 1023) == 0) {
			System.out.print("\nGeneration: " + ga.getGeneration() + " Minutes: " + (ga.getStatistics().getExecutionTime() / 1000 / 60 + " - "));
		} else if ((gen & 127) == 0) {
			System.out.print("|");
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
		ga = new AlcaGA(fitness, pop, GENERATION_LIMIT);
	
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
		System.out.println("Starting at: " + sdf.format(cal.getTime()));
		
		// Calculate the schedule/////////
		ga.evolve(); // FIGHT!////////////
		//////////////////////////////////
		
		// Get statistics
		Population.Statistics<IntegerChromosome> stats = ga.getCurrentPopulation().getStatistics();
		GeneticAlgorithm.Statistics algostats = ga.getStatistics();
		
		Group<IntegerChromosome> legals = stats.getGroup(Population.LEGALS);
		
		System.out.println("\n\n[Results]\nSchedule:");
		
		printSchedule(legals.get(0), args, fitness);
		
		System.out.println("<nonsense>\n\nStatistics:");
		Utils.printStatistics(stats);
		System.out.println("</nonsense>\n");
		
		System.out.println("Generations needed: " + algostats.getGenerations());

		cal = Calendar.getInstance();
		System.out.println("Finished at: " + sdf.format(cal.getTime()));
		
		if (algostats.getGenerations() < GENERATION_LIMIT) {
			System.out.println("[SUCCESS]");
		} else {
			System.out.println("[FAILURE]");
		}
	}
	
	public static void main(String[] args) {
		Main main = new Main(args);
		
		main.run();
	}
}
