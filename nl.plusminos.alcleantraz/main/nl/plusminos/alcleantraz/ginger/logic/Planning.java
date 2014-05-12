package nl.plusminos.alcleantraz.ginger.logic;

import gnu.trove.set.hash.TByteHashSet;
import jenes.chromosome.IntegerChromosome;

public class Planning {
	public static final byte TASK_KITCHEN = 0;
	public static final byte TASK_HALL = 1;
	public static final byte TASK_WC = 2;
	public static final byte TASK_DOUCHE = 3;
	public static final byte TASKS_TOTAL = 4;
	
	public static final int JOBS_INCLUDING = 7;
	public static final int JOBS_EXCLUDING = 5;
	public static final int JOBS_THREEWEEKS = JOBS_EXCLUDING * 2 + JOBS_INCLUDING;
	
	private int threeWeeks;
	private int persons;
	
	private Week[] weeks;
	
	private TByteHashSet[] surjectiveCheck;
	
	public Planning(IntegerChromosome chrom, int persons) {
		threeWeeks = chrom.length() / JOBS_THREEWEEKS;
		weeks = new Week[threeWeeks * 3];
		this.persons = persons;
		
		surjectiveCheck = new TByteHashSet[TASKS_TOTAL];
		for (int i = 0; i < TASKS_TOTAL; i++) {
			surjectiveCheck[i] = new TByteHashSet(persons);
		}
		
		int weekNum;
		int startPos;
		for (int i = 0; i < threeWeeks; i++) {
			for (int j = 0; j < 2; j++) {
				weekNum = i * 3 + j;
				weeks[weekNum] = new Week(surjectiveCheck, false);
				
				startPos = i * JOBS_THREEWEEKS + j * JOBS_EXCLUDING;
				
				applyPlanning(chrom, weeks[weekNum], startPos);
			}
			
			weekNum = i * 3 + 2;
			weeks[weekNum] = new Week(surjectiveCheck, true);
			startPos = i * JOBS_THREEWEEKS + 2 * JOBS_EXCLUDING;
			applyPlanning(chrom, weeks[weekNum], startPos);
		}
	}

	private static void applyPlanning(IntegerChromosome chrom, Week week, int startPos) {
		// Apply kitchen persons
		week.addToKitchen((byte) chrom.getValue(startPos));
		week.addToKitchen((byte) chrom.getValue(startPos + 1));
		week.addToKitchen((byte) chrom.getValue(startPos + 2));
		
		// Apply one-person jobs
		week.addToWc((byte) chrom.getValue(startPos + 3));
		week.addToDouche((byte) chrom.getValue(startPos + 4));
		
		// (Maybe) APply hallway
		if (week.hall != null) {
			week.addToHallway((byte) chrom.getValue(startPos + 5));
			week.addToHallway((byte) chrom.getValue(startPos + 6));
		}
	}
	
	public boolean hasPerfectAssignment() {
		boolean result = false;
		
		for (int i = 0; i < TASKS_TOTAL; i++) {
			result = result | (surjectiveCheck[i].size() == persons);
		}
		
		return result;
	}
	
	/**
	 * The lower the better!
	 * @return The amount of times a certain person doesn't do a job
	 */
	public int getPerfectAssignmentQuality() {
		int result = 0;
		
		for (int i = 0; i < TASKS_TOTAL; i++) {
			result += persons - surjectiveCheck[i].size();
		}
		
		return result;
	}
	
	public boolean hasNoWeeksWithDoubles() {
		boolean result = false;
		
		for (int i = 0; i < weeks.length; i++) {
			result = result | weeks[i].hasNoDoubles();
		}
		
		return result;
	}
	
	/**
	 * The lower the better
	 * @return The amount of times a person was found double in each week
	 */
	public int getDoublesQuality() {
		int result = 0;
		
		for (int i = 0; i < weeks.length; i++) {
			result += weeks[i].getAmountOfDoubles();
		}
		
		return result;
	}
	
	public boolean hasNoRecurringPersons() { // TODO: Can one do hallway twice in a row? Answer: NO
		boolean result = false;
		
		int weekNum;
		for (int i = 0; i < threeWeeks; i++) {
			if (i > 0) {
				result = result | weeks[i * JOBS_THREEWEEKS].hasNoRecurringPersons(weeks[i * JOBS_THREEWEEKS - 1]);
				
				result = result | weeks[i * JOBS_THREEWEEKS + 2].hasNoRecurringPersons(
						weeks[i * JOBS_THREEWEEKS + 1], weeks[i * JOBS_THREEWEEKS - 1]);
			}
			
			result = result | weeks[i * JOBS_THREEWEEKS + 1].hasNoRecurringPersons(weeks[i * JOBS_THREEWEEKS]);
		}
		
		return result;
	}
	
	public int getNoRecurringPersonsQuality() {
		int result = 0;
		
		for (int i = 1; i < weeks.length(); i++) {
			
		}
	}
	
}
