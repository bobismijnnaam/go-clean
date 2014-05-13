package nl.plusminos.alcleantraz.ginger.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jenes.GenerationEventListener;
import jenes.GeneticAlgorithm;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.stage.AbstractStage;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;
import jenes.tutorials.utils.Utils;
import nl.plusminos.alcleantraz.vanilla.VanillaGA;

public class Scheduler implements Runnable, GenerationEventListener<IntegerChromosome> {
	public final int PERSONS;
	public final int TRIPLETS;
	public final int POPSIZE;
	public final int GENLIMIT;
	public final boolean QUANTITATIVE;
	
	public int currentGeneration;
	
	private final boolean CONSOLE;
	
	public IntegerChromosome result = null;
	
	private GingerFit fitness;
	
	public Scheduler(int persons, int triplets, int popSize, int genLimit, boolean quantitative, boolean console) {
		PERSONS = persons;
		TRIPLETS = triplets;
		POPSIZE = popSize;
		GENLIMIT = genLimit;
		
		CONSOLE = console;
		
		QUANTITATIVE = quantitative;
	}

	@Override
	public void run() {
		// Set some initial vars
		Individual<IntegerChromosome> sample = new Individual<IntegerChromosome>(
				new IntegerChromosome(TRIPLETS * Planning.JOBS_THREEWEEKS, 0 , PERSONS - 1));
		Population<IntegerChromosome> pop = new Population<IntegerChromosome>(sample, POPSIZE);
		
		// Initialize fitness instance
		fitness = new GingerFit(PERSONS, QUANTITATIVE);
		
		// Setup genetic algorithm
		GingerGA ga = new GingerGA(fitness, pop, GENLIMIT);
	
		AbstractStage<IntegerChromosome> selection = new TournamentSelector<IntegerChromosome>(3);
		AbstractStage<IntegerChromosome> crossover = new OnePointCrossover<IntegerChromosome>(0.8);
		AbstractStage<IntegerChromosome> mutation = new SimpleMutator<IntegerChromosome>(0.02);
		ga.addStage(selection);
		ga.addStage(crossover);
		ga.addStage(mutation);
		
		ga.addGenerationEventListener(this);
		
		// Startup info
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		if (CONSOLE) {
			System.out.println("[Alcleantraz{Ginger} base roster generator]\n");
			System.out.println("[Parameters]");
			System.out.println("Weeks: " + TRIPLETS * 3);
			System.out.println("Persons: " + PERSONS);
			System.out.println("Jobs: " + TRIPLETS * Planning.JOBS_THREEWEEKS);
			System.out.println("Population: " + POPSIZE);
			System.out.println("Maximum generation: " + GENLIMIT);
			System.out.println();
			
			System.out.println("Starting at: " + sdf.format(cal.getTime()));
		}
		
		// Calculate the schedule/////////
		ga.evolve(); // FIGHT!////////////
		//////////////////////////////////
		
		// Get statistics
		Population.Statistics<IntegerChromosome> stats = ga.getCurrentPopulation().getStatistics();
		GeneticAlgorithm.Statistics algostats = ga.getStatistics();
		
		Group<IntegerChromosome> legals = stats.getGroup(Population.LEGALS);
		
		if (CONSOLE) {
			System.out.println("\n\n[Results]\n\nSchedule:");
			
			printSchedule(legals.get(0), fitness);
			System.out.println(legals.get(0).toCompleteString());
			
			System.out.println("<nonsense>\n\nStatistics:");
			Utils.printStatistics(stats);
			System.out.println("</nonsense>\n");
			
			System.out.println("Generations needed: " + algostats.getGenerations());

			cal = Calendar.getInstance();
			System.out.println("Finished at: " + sdf.format(cal.getTime()));
			
			if (algostats.getGenerations() < GENLIMIT) {
				System.out.println("[SUCCESS]\n");
			} else {
				System.out.println("[FAILURE]\n");
			}
			
			System.out.println("{{END}}\n");
		}
		
		result = legals.get(0).getChromosome();
	}

	private void printSchedule(Individual<IntegerChromosome> individual,
			Fitness<IntegerChromosome> fitness) {
		IntegerChromosome chrom = individual.getChromosome();
		
		for (int i = 0; i < TRIPLETS; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < Planning.JOBS_EXCLUDING; k++) {
					System.out.print(chrom.getValue(i * Planning.JOBS_THREEWEEKS + j * Planning.JOBS_EXCLUDING + k));
					System.out.print("\t");
				}
				System.out.println();
			}
			
			for (int k = 0; k < Planning.JOBS_INCLUDING; k++) {
				System.out.print(chrom.getValue(i * Planning.JOBS_THREEWEEKS + 2 * Planning.JOBS_EXCLUDING + k));
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println();
		
		((GingerFit) fitness).printEvaluation(individual);
		
		System.out.println();
		
	}

	public void onGeneration(GeneticAlgorithm<IntegerChromosome> thisGA, long timestamp) {
		currentGeneration = thisGA.getGeneration();
		if ((currentGeneration & 1023) == 0 && currentGeneration != 0 && CONSOLE) {
			Population.Statistics<IntegerChromosome> stats = thisGA.getCurrentPopulation().getStatistics();
			Group<IntegerChromosome> legals = stats.getGroup(Population.LEGALS);
			
			Individual<IntegerChromosome> elite = legals.get(0);
			IntegerChromosome chrom = elite.getChromosome();
			
			fitness.printEvaluation(elite);
			
			// TODO
//			System.out.print(" Unique job/person combos: " + fitness.countPerfectAssignment(chrom)
//					+ "; Weeks with doubles: " + fitness.countWeeksWithDoubles(chrom) + ";");
		}
		
		nl.plusminos.alcleantraz.utils.Utils.reportGeneration(thisGA);
	}
	
	public static void main(String[] args) {
		Scheduler s = new Scheduler(11, 5, 1000, 20000, false, true);
		Thread t = new Thread(s);
		t.run();
	}
}
