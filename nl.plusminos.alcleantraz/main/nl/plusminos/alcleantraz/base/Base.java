package nl.plusminos.alcleantraz.base;

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

import nl.plusminos.alcleantraz.utils.ArgumentParser;

public class Base implements GenerationEventListener<IntegerChromosome> {
	private final int SCHEDULE_JOBS;
	private final int SCHEDULE_WEEKS; // Prefer a multiple of 3
	private final int SCHEDULE_SLOTS;
	private final int SCHEDULE_PERSONS;
	
	private final int CHROMOSOME_LENGTH;
	private final int POPULATION_SIZE;
	private final int UNIQUE_PERSON_JOB_COMBOS; // Slots + 0 + 0 (you don't want overlaps/doubles)
	
	private final int GENERATION_LIMIT;
	
	private static GeneticAlgorithm<IntegerChromosome> ga;
	
	private String[] persons = null;
	private String[] jobs = null;
	
	public Base(String[] persons, String[] jobs, int weeks, int chromLength, int popSize, int genLimit) {
		this.persons = persons;
		this.jobs = jobs;
		
		SCHEDULE_JOBS = jobs.length;
		SCHEDULE_PERSONS = persons.length;
		SCHEDULE_WEEKS = weeks;
		SCHEDULE_SLOTS = SCHEDULE_JOBS * SCHEDULE_WEEKS;
		
		CHROMOSOME_LENGTH = chromLength;
		POPULATION_SIZE = popSize;
		UNIQUE_PERSON_JOB_COMBOS = SCHEDULE_PERSONS * SCHEDULE_JOBS;
		
		GENERATION_LIMIT = genLimit;
	}
	
	public void printSchedule(Individual<IntegerChromosome> ind, String[] names, AlcaFit fitness) {
		int pos;
		int person;
		IntegerChromosome chrom = ind.getChromosome();
		
		for (int j = 0; j < SCHEDULE_JOBS; j++) {
			System.out.print(jobs[j] + "\t");
		}
		System.out.println();
		
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
			System.out.println("\t" + persons[p] + ": " + jobs);
		}
		
		System.out.println();
	}
	
	@Override
	public void onGeneration(GeneticAlgorithm<IntegerChromosome> ga, long arg1) {
		int gen = ga.getGeneration();
		if ((gen & 1023) == 0) {
			System.out.print("\nGeneration " + ga.getGeneration() + " - ");
		} else if ((gen & 127) == 0) {
			System.out.print("|");
		}
	}
	
	public IntegerChromosome run() {
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
		System.out.println("{{ALCLEANTRAZ ALPHA BASE ROSTER GENERATOR}}\n");
		System.out.println("[Parameters]");
		System.out.println("Weeks: " + SCHEDULE_WEEKS);
		System.out.println("Persons: " + SCHEDULE_PERSONS);
		System.out.println("Jobs: " + SCHEDULE_JOBS);
		System.out.println("Population: " + POPULATION_SIZE);
		System.out.println("Maximum generation: " + GENERATION_LIMIT);
		System.out.println("Unqiue combos of jobs and persons: " + UNIQUE_PERSON_JOB_COMBOS);
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
		
		printSchedule(legals.get(0), persons, fitness);
		
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
		
		return legals.get(0).getChromosome();
	}
	
	public static void main(String[] args) {
		ArgumentParser ap = new ArgumentParser(args);

		int weeks = 15; // Prefer a multiple of 3
		int slots = ap.getReducedJobs(2).length * weeks;
		int chromLength = slots;
		int popSize = 1000;
		int uniqueCombos = ap.getReducedJobs(2).length * ap.getPersons().length; // Slots + 0 + 0 (you don't want overlaps/doubles)
		int genLimit = 30000;
		
		Base main = new Base(ap.getPersons(), ap.getReducedJobs(2), weeks, chromLength, popSize, genLimit);
		
		IntegerChromosome result = main.run();
		
		System.out.println("Resulting integer chromosome: " + result.toString());
	}
}
