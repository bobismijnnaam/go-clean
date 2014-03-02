package nl.plusminos.alcleantraz.vanilla;

import jenes.chromosome.IntegerChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;

public class VanillaFit extends Fitness<IntegerChromosome> {

	// Constants
	public static final int NO_PERSON = -1;
	public static final int JOB_KITCHEN = 0;
	public static final int JOB_WC = 3;
	public static final int JOB_DOUCHE = 4;
	public static final int JOB_HALLWAY = 5;
	
	// Settings
	private final int PERSONS;
	private final int JOBS;
	private final int JOBS_NO_HALLWAY;
	private final int WEEKS;
	private final int SLOTS;
	private final int[] LAST_WEEK;
	
	// Preset variables
	private boolean[] inWeek;
	
	public boolean hasHallway(int week) {
		return week % 3 == 0;
	}
	
	public void resetInWeekArray() {
		for (int i = 0; i < PERSONS; i++) {
			inWeek[i] = false;
		}
	}
	
	public int getWeekBeginning(int week) { // Returns the position of the first job of the given week within the genome
		// What we need: week 0 = 0; week 1 = 1; week 2 = 1; week 3 = 1; week 4 = 2
		// What this gives: week 0 = 0; week 1 = 1; week 2 = 1; week 3 = 1; week 4 = 2
		// int hallwayWeekCount = (int) Math.floor((week + 2) / 3);
		
		return week * JOBS_NO_HALLWAY
				+ (int) Math.floor((week + 2) / 3) * 2; // It takes 2 persons to do each hallway job;
	}
	
	public int getJobPos(int job, int week) {
		// Check if this week actually has a hallway - if it doesn't, write something in the console and return NO_PERSON;
		if ((job == JOB_HALLWAY || job == JOB_HALLWAY + 1) && !hasHallway(week)) {
			System.out.println("[ERROR] ALGORITHM TRIED ACCESSING A HALLWAY JOB WHEN IT WAS NOT A HALLWAY WEEK [ERROR]");
			return NO_PERSON;
		}
		
		return getWeekBeginning(week) + job;
	}
	
	public boolean hasNoDoublesPerWeek(IntegerChromosome chrom) {
		int currentJobPosition = 0;
		int currentPerson;
		int maxJob;
		
		for (int w = 0; w < WEEKS; w++) {
			maxJob = hasHallway(w) ? JOBS : JOBS_NO_HALLWAY;
			
			for (int j = 0; j < maxJob; j++) {
				currentPerson = chrom.getValue(currentJobPosition);
				
				if (inWeek[currentPerson]) {
					resetInWeekArray();
					return false;
				} else {
					inWeek[currentPerson] = true;
				}
				
				currentJobPosition++;
			}
			
			resetInWeekArray();
		}
		
		return true;
	}
	
	public int countWeeksWithDoubles(IntegerChromosome chrom) {
		int currentJobPosition = 0;
		int currentPerson;
		int maxJob;
		int weeksWithDoubles = 0;
		
		for (int w = 0; w < WEEKS; w++) {
			maxJob = hasHallway(w) ? JOBS : JOBS_NO_HALLWAY;
			maxJob += currentJobPosition;
			
			for (int j = currentJobPosition; j < maxJob; j++) {
				currentPerson = chrom.getValue(j); // wtf
				
				if (inWeek[currentPerson]) {
					weeksWithDoubles++;
					break;
				} else {
					inWeek[currentPerson] = true;
				}
			}
			
			currentJobPosition += hasHallway(w) ? JOBS : JOBS_NO_HALLWAY;
			
			resetInWeekArray();
		}
		
		return weeksWithDoubles;	
	}
	
	//@ requires weeks % 3 == 0;
	public VanillaFit(int persons, int jobs, int weeks, int[] lastWeek) {
		super(true);
		// super(false, false, false, true); // This comes later. For multi objective selection
		// The order is determined by the todo-list (in notes.txt)
		
		PERSONS = persons;
		JOBS = jobs;
		JOBS_NO_HALLWAY = JOBS - 2;
		WEEKS = weeks;
		SLOTS = JOBS_NO_HALLWAY * WEEKS // Normal tasks
				+ WEEKS / 3 * 2; // Hallway tasks
		LAST_WEEK = lastWeek;
		
		inWeek = new boolean[PERSONS]; // The array to store whether or not a person has a job that week
	}
	
	@Override
	public void evaluate(Individual<IntegerChromosome> ind) {
		IntegerChromosome chrom = ind.getChromosome();
		
		boolean hasNoDoubles = hasNoDoublesPerWeek(chrom);
		
		if (hasNoDoubles) {
			ind.setScore(1);
		} else {
			ind.setScore(0);
		}
		
	}
}
