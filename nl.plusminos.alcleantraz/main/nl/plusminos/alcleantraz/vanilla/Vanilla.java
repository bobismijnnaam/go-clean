package nl.plusminos.alcleantraz.vanilla;

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

public class Vanilla implements GenerationEventListener<IntegerChromosome> {
	
	public static int SCHEDULE_JOBS_NO_HALLWAY;
	public static int SCHEDULE_JOBS_HALLWAY;
	public static int SCHEDULE_WEEKS; // Prefer a multiple of 3
	public static int SCHEDULE_SLOTS;
	public static int SCHEDULE_PERSONS;
	
	private final int CHROMOSOME_LENGTH;
	private final int POPULATION_SIZE;
	
	private final int GENERATION_LIMIT;
	
	private static GeneticAlgorithm<IntegerChromosome> ga;
	
	private String[] persons = null;
	private String[] jobs = null;

	//@ requires weeks % 3 == 0;
	public Vanilla(String[] persons, String[] jobs, int weeks, int popSize, int genLimit) {
		this.persons = persons;
		this.jobs = jobs;
		
		SCHEDULE_JOBS_HALLWAY = jobs.length;
		SCHEDULE_JOBS_NO_HALLWAY = SCHEDULE_JOBS_HALLWAY - 2;
		
		SCHEDULE_PERSONS = persons.length;
		SCHEDULE_WEEKS = weeks;
		SCHEDULE_SLOTS = SCHEDULE_JOBS_NO_HALLWAY * SCHEDULE_WEEKS + SCHEDULE_WEEKS / 3 * 2;
		
		CHROMOSOME_LENGTH = SCHEDULE_SLOTS;
		POPULATION_SIZE = popSize;
		
		GENERATION_LIMIT = genLimit;
	}
	
	public void printSchedule(Individual<IntegerChromosome> ind, VanillaFit fitness) {
		int pos;
		int person;
		int maxJob;
		IntegerChromosome chrom = ind.getChromosome();
		
		for (int j = 0; j < SCHEDULE_JOBS_HALLWAY; j++) {
			System.out.print(jobs[j] + "\t");
		}
		System.out.println();
		
		for (int w = 0; w < SCHEDULE_WEEKS; w++) {
			maxJob = fitness.hasHallway(w) ? SCHEDULE_JOBS_HALLWAY : SCHEDULE_JOBS_NO_HALLWAY;
			
			for (int j = 0; j < maxJob; j++) {
				pos = fitness.getJobPos(j, w);
				person = chrom.getValue(pos);
				System.out.print(persons[person] + "\t");
			}
			System.out.println();
		}
		
		System.out.println("\nConstraints:");
		System.out.println("Has no duplicates in every week: " + fitness.hasNoDoublesPerWeek(chrom) + " (" + fitness.countWeeksWithDoubles(chrom) + ")");
//		System.out.println("Has no consequent identical jobs: " + fitness.hasNoOverlap(chrom) + " (" + fitness.countOverlaps(chrom) + ")");
//		System.out.println("Everyone has each job at least once: " + fitness.hasPerfectAssignment(chrom) + " (" + fitness.countPerfectAssignment(chrom) + ")");
//		System.out.println("Distribution (ideal = 0): " + fitness.gradeDistribution(chrom));
		System.out.println("\nActual distribution of work: ");
		
		int jobs = 0;
		for (int p = 0; p < SCHEDULE_PERSONS; p++) {
			jobs = 0;
			for (int j = 0; j < SCHEDULE_SLOTS; j++) {
				if (chrom.getValue(j) == p) {
					jobs++;
				}
			}
			System.out.println("\t" + persons[p] + ": " + jobs);
		}
		
		System.out.println();
	}

	@Override
	public void onGeneration(GeneticAlgorithm<IntegerChromosome> thisGeneration, long arg1) {
		nl.plusminos.alcleantraz.utils.Utils.reportGeneration(thisGeneration);
	}

	public IntegerChromosome generate() {
		// Set some initial vars
		Individual<IntegerChromosome> sample = new Individual<IntegerChromosome>(
				new IntegerChromosome(CHROMOSOME_LENGTH, 0 ,SCHEDULE_PERSONS - 1));
		Population<IntegerChromosome> pop = new Population<IntegerChromosome>(sample, POPULATION_SIZE);
		
		// Initialize fitness instance
		VanillaFit fitness = new VanillaFit(SCHEDULE_PERSONS, SCHEDULE_JOBS_HALLWAY, SCHEDULE_WEEKS, null);
		
		// Setup genetic algorithm
		ga = new VanillaGA(fitness, pop, GENERATION_LIMIT);
	
		AbstractStage<IntegerChromosome> selection = new TournamentSelector<IntegerChromosome>(3);
		AbstractStage<IntegerChromosome> crossover = new OnePointCrossover<IntegerChromosome>(0.8);
		AbstractStage<IntegerChromosome> mutation = new SimpleMutator<IntegerChromosome>(0.02);
		ga.addStage(selection);
		ga.addStage(crossover);
		ga.addStage(mutation);
		
		ga.addGenerationEventListener(this);
		
		// Startup info
		System.out.println("{{ALCLEANTRAZ ALPHA BASE ROSTER GENERATOR}}\n");
		System.out.println("[Parameters]");
		System.out.println("Weeks: " + SCHEDULE_WEEKS);
		System.out.println("Persons: " + SCHEDULE_PERSONS);
		System.out.println("Jobs (including hallway): " + SCHEDULE_JOBS_HALLWAY);
		System.out.println("Population: " + POPULATION_SIZE);
		System.out.println("Maximum generation: " + GENERATION_LIMIT);
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
		
		printSchedule(legals.get(0), fitness);
		
		System.out.println("<nonsense>\n\nStatistics:");
		Utils.printStatistics(stats);
		System.out.println("</nonsense>\n");
		
		System.out.println("Generations needed: " + algostats.getGenerations());

		cal = Calendar.getInstance();
		System.out.println("Finished at: " + sdf.format(cal.getTime()));
		
		if (algostats.getGenerations() < GENERATION_LIMIT) {
			System.out.println("[SUCCESS]\n");
		} else {
			System.out.println("[FAILURE]\n");
		}
		
		System.out.println("{{END}}\n");
		
		return legals.get(0).getChromosome();
	}
	
	// Args should be names and last one the amount of weeks. Should be a multiple of 3
	// args = "binky penny maarten ramon bob granny sam jeroen lizzy emile 15" 
	public static void main(String[] args) {
		int popSize = 1000;
		int genLimit = 3000;
		int weeks = 6;
		
		String[] jobs = new String[]{"Keuken", "Keuken", "Keuken", "Toilet", "Douche", "Gang", "Gang"};
		
		Vanilla vanillaScheduleFactory = new Vanilla(args, jobs, weeks, popSize, genLimit);
		
		IntegerChromosome result = vanillaScheduleFactory.generate();
		
		System.out.println("Resulting integer chromosome: " + result.toString());
	}
	
}
