package nl.plusminos.alcleantraz.ginger.logic;

import jenes.chromosome.IntegerChromosome;
import nl.plusminos.alcleantraz.utils.Utils;

public class Planning {
	public static final byte TASK_KITCHEN = 0;
	public static final byte TASK_HALL = 1;
	public static final byte TASK_WC = 2;
	public static final byte TASK_DOUCHE = 3;
	public static final byte TASKS_TOTAL = 4;
	
	public static final int JOBS_INCLUDING = 7;
	public static final int JOBS_EXCLUDING = 5;
	public static final int JOBS_THREEWEEKS = JOBS_EXCLUDING * 2 + JOBS_INCLUDING;
	
	private final int THREEWEEKS;
	private int persons;
	
	private Week[] weeks;
	
	private boolean[][] surjectiveCheck = new boolean[TASKS_TOTAL][];
	
	public Planning(int threeWeeks, int persons) {
		THREEWEEKS = threeWeeks;
		this.persons = persons;
		
		for (int i = 0; i < TASKS_TOTAL; i++) {
			surjectiveCheck[i] = new boolean[persons];
		}
		
		weeks = new Week[THREEWEEKS * 3];
		
		int weekNum;
		for (int i = 0; i < THREEWEEKS; i++) {
			for (int j = 0; j < 2; j++) {
				weekNum = i * 3 + j;
				weeks[weekNum] = new Week(surjectiveCheck, false, persons);
			}
			
			weekNum = i * 3 + 2;
			weeks[weekNum] = new Week(surjectiveCheck, true, persons);
		}
	}
	
	public void initialize(IntegerChromosome chrom) {
		for (boolean[] job : surjectiveCheck) {
			Utils.resetBooleanArray(job);
		}
		
		int weekNum;
		int startPos;
		for (int i = 0; i < THREEWEEKS; i++) {
			for (int j = 0; j < 2; j++) {
				weekNum = i * 3 + j;
				weeks[weekNum].initialize();
				
				startPos = i * JOBS_THREEWEEKS + j * JOBS_EXCLUDING;
				applyPlanning(chrom, weeks[weekNum], startPos);
			}
			
			weekNum = i * 3 + 2;
			weeks[weekNum].initialize();
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
		
		// (Maybe) Apply hallway
		if (week.isHallWeek()) {
			week.addToHallway((byte) chrom.getValue(startPos + 5));
			week.addToHallway((byte) chrom.getValue(startPos + 6));
		}
	}
	
	public boolean hasPerfectAssignment() {
		boolean result = true;
		
		for (int i = 0; i < TASKS_TOTAL; i++) {
			result = result && (Utils.countTrue(surjectiveCheck[i]) == persons);
		}
		
		return result;
	}
	
	public boolean hasPerfectAssignment(int p) {
		boolean result = true;
		
		System.out.print("[");
		for (int i = 0; i < TASKS_TOTAL; i++) {
			result = result && (Utils.countTrue(surjectiveCheck[i]) == persons);
			System.out.print(Utils.countTrue(surjectiveCheck[i]) + "|");
		}
		System.out.println();
		
		return result;
	}
	
	/**
	 * The lower the better!
	 * @return The amount of times a certain person doesn't do a job
	 */
	public int getPerfectAssignmentQuality() {
		int result = 0;
		
		for (int i = 0; i < TASKS_TOTAL; i++) {
			result += persons - Utils.countTrue(surjectiveCheck[i]);
		}
		
		return result;
	}
	
	public boolean hasNoWeeksWithDoubles() {
		boolean result = true;
		
		for (int i = 0; i < weeks.length; i++) {
			result = result && weeks[i].hasNoDoubles();
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
		boolean result = true;
		
		for (int i = 0; i < THREEWEEKS; i++) {
			if (i > 0) {
				// First week
				result = result && weeks[i * 3].hasNoRecurringPersons(weeks[i * 3 - 1]);
				
				// Third week including hallway
				result = result && weeks[i * 3 + 2].hasNoRecurringPersons(
						weeks[i * 3 + 1], weeks[i * 3 - 1]);
				// Handle special case with last week of previous schedule?
			} else {
				// Third week, but not checking the hallway
				result = result && weeks[i * 3 + 2].hasNoRecurringPersons(
						weeks[i * 3 + 1]);
			}
			
			// Second week
			result = result && weeks[i * 3 + 1].hasNoRecurringPersons(weeks[i * 3]);
		}
		
		return result;
	}
	
	/**
	 * The lower the better!
	 * @return Returns the amount of times a person was found that does the same job as last week
	 */
	public int getNoRecurringPersonsQuality() {
		int result = 0;
		
		for (int i = 0; i < THREEWEEKS; i++) {
			if (i > 0) {
				// Count first week
				result += weeks[i * 3].hasNoRecurringPersons(weeks[i * 3 - 1]) ? 0 : 1;
				
				// Third week including hallway
				result += weeks[i * 3 + 2].hasNoRecurringPersons(weeks[i * 3 + 1],
						weeks[i * 3 - 1]) ? 0 : 1;
			} else {
				// Third week without hallway
				result += weeks[i * 3 + 2].hasNoRecurringPersons(weeks[i * 3 + 1]) ? 0 : 1;
			}
			
			// Second week
			result += weeks[i * 3 + 1].hasNoRecurringPersons(weeks[i * 3]) ? 0 : 1;
			
		}
		
		return result;
	}
	
}
