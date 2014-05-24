package nl.plusminos.alcleantraz.peach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// TODO: Display job counts per person (per job & total)
public class FitnessDebugger {
	public static String evaluate(Individual individual, Fitness fitness) {
		short[] chrom = individual.getChromosome();
		String result = "R = A person who also did that job the previous week\n"
				+ "D = A person who also has another job that week\n\n";
		
		int recurrence = 0, doubles = 0, assignment = 0;
		
		ArrayList<HashSet<Short>> jobs = new ArrayList<HashSet<Short>>();
		for (int i = 0; i < 4; i++) {
			jobs.add(new HashSet<Short>());
		}
		
		// Display roster with debug info
		for (int tw = 0; tw < Info.getThreeWeeks(); tw++) {
			for (int w = 0; w < 3; w++) { // First and second week
				boolean doubleDetected = false;
				boolean recurrenceDetected = false;
				
				int weekStart = tw * Info.JOBS_THREEWEEKS + w * Info.JOBS_EXCLUDING;
				String[] params = new String[7];
				for (int i = 0; i < 7; i++) {
					params[i] = "";
				}
				
				// Doubles checking (injection)
				checkDoubles(chrom, params, w == 2, weekStart);

				// Recurrence checking
				checkRecurrence(chrom, params, w == 2, weekStart, w);
				
				// Perfect assignment checking
				checkAssignment(chrom, jobs, weekStart, w == 2);
				
				// Output & parse week
				result += (tw * 3 + w) + ":\t";
				for (int j = 0; j < ((w == 2) ? 7 : 5); j++) {
					result += Info.getName(chrom[weekStart + j]) + " " + params[j] + "\t";
					if (params[j].contains("D") && !doubleDetected) {
						doubles++;
						doubleDetected = true;
					}
					if (params[j].contains("R") && !recurrenceDetected) {
						recurrence++;
						recurrenceDetected = true;
					}
				}
				
				result += "\n";
			}
		}
		
		// Calculate perfect assignment value
		assignment = 4 * Info.getPersons() - (jobs.get(Info.JOB_KITCHEN).size() 
				+ jobs.get(Info.JOB_TOILET).size()
				+ jobs.get(Info.JOB_SHOWER).size()
				+ jobs.get(Info.JOB_HALLWAY).size());
		
		fitness.setChromosome(individual.getChromosome());
		int resultAssignment = fitness.getPerfectAssignmentQuality();
		int resultInjection = fitness.getInjectionQuality();
		int resultRecurrence = fitness.getRecurrenceQuality();
		float resultDistribution = fitness.gradeDistribution();
		
		result += "\n[Test results]";
		result += "\n" + (individual.isViable() ? "VIABLE" : "UNVIABLE");
		result += "\nPerfect assignment: " + resultAssignment + "(" + assignment +")";
		result += "\nInjection: " + resultInjection + "(" + doubles + ")";
		result += "\nRecurrence: " + resultRecurrence + "(" + recurrence + ")";
		result += "\nDistribution: " + resultDistribution;
		result += "\nJobs per person: ";
		ArrayList<HashMap<Integer, Integer>> totals = displayDistribution(chrom);
		for (int i = 1; i < Math.pow(2, Info.getPersons()); i *= 2) {
			result += "\n" + Info.getName((short) i) + ": ";
			for (int j = 0; j < 5; j++) {
				HashMap<Integer, Integer> hm = totals.get(j);
				result += hm.get(i) == null ? 0 : hm.get(i);
				if (j != 4) {
					result += " | ";
				}
			}
		}
		if (resultAssignment == assignment &&
				resultInjection == doubles &&
				resultRecurrence == recurrence) {
			result += "\nRoster evaluated CORRECTLY with endscore " + (assignment + doubles + recurrence + resultDistribution);
		} else {
			result += "\nRoster evaluated INCORRECTLY";
		}
		
		result += "\n";
		
		return result;
	}
	
	private static void checkAssignment(short[] chrom, ArrayList<HashSet<Short>> jobs, int weekStart, boolean hallWeek) {
		jobs.get(Info.JOB_KITCHEN).add(chrom[weekStart]);
		jobs.get(Info.JOB_KITCHEN).add(chrom[weekStart + 1]);
		jobs.get(Info.JOB_KITCHEN).add(chrom[weekStart + 2]);
		
		jobs.get(Info.JOB_TOILET).add(chrom[weekStart + 3]);
		
		jobs.get(Info.JOB_SHOWER).add(chrom[weekStart + 4]);
		
		if (hallWeek) {
			jobs.get(Info.JOB_HALLWAY).add(chrom[weekStart + 5]);
			jobs.get(Info.JOB_HALLWAY).add(chrom[weekStart + 6]);
		}
	}

	private static void checkRecurrence(short[] chrom, String[] params, boolean hallWeek, int weekStart, int weekNum) {
		int previousWeekStart = 0;
		if (weekNum == 0) {
			previousWeekStart = weekStart - 7; 
		} else {
			previousWeekStart = weekStart - 5;
		}
		
		if (previousWeekStart < 0) return;
		
		ArrayList<Short> persons = new ArrayList<Short>();
		persons.add(chrom[previousWeekStart]);
		persons.add(chrom[previousWeekStart + 1]);
		persons.add(chrom[previousWeekStart + 2]);
		
		for (int j = 0; j < 3; j++) {
			if (persons.contains(chrom[weekStart + j])) {
				params[j] += "R";
			}
		}
		
		if (chrom[previousWeekStart + 3] == chrom[weekStart + 3]) {
			params[3] += "R";
		}
		
		if (chrom[previousWeekStart + 4] == chrom[weekStart + 4]) {
			params[4] += "R";
		}
		
		if (hallWeek) {
			previousWeekStart = weekStart - Info.JOBS_THREEWEEKS;
			if (previousWeekStart < 0) return;
			
			persons.clear();
			persons.add(chrom[previousWeekStart + 5]);
			persons.add(chrom[previousWeekStart + 6]);
			
			for (int j = 5; j < 7; j++) {
				if (persons.contains(chrom[weekStart + j])) {
					params[j] += "R";
				}
			}
		}
	}

	public static void checkDoubles(short[] chrom, String[] params, boolean hallWeek, int weekStart) {
		ArrayList<Short> doubles = new ArrayList<Short>();
		for (int j = 0; j < (hallWeek ? 7 : 5); j++) {
			if (doubles.contains(chrom[weekStart + j])) {
				params[j] += "D";
			}
			doubles.add(chrom[weekStart + j]);
		}
	}
	
	public static ArrayList<HashMap<Integer, Integer>> displayDistribution(short[] chrom) {
		ArrayList<HashMap<Integer, Integer>> totals = new ArrayList<HashMap<Integer, Integer>>(5);
		for (int i = 0; i < 5; i++) {
			totals.add(new HashMap<Integer, Integer>());
		}
		
		for (int tw = 0; tw < Info.getThreeWeeks(); tw++) {
			for (int w = 0; w < 3; w++) {
				int weekStart = tw * Info.JOBS_THREEWEEKS + w * Info.JOBS_EXCLUDING;
				
				for (int j = 0; j < (w == 2 ? 7 : 5); j++) {
					HashMap<Integer, Integer> job = totals.get(Info.JOB_KITCHEN);
					job = (j == 3) ? totals.get(Info.JOB_TOILET) : job;
					job = (j == 4) ? totals.get(Info.JOB_SHOWER) : job;
					job = (j > 4) ? totals.get(Info.JOB_HALLWAY) : job;
					int person = chrom[weekStart + j];
					int count = job.get(person) == null ? 0 : job.get(person);
					job.put(person, count + 1);
					
					count = totals.get(4).get(person) == null ? 0 : totals.get(4).get(person);
					totals.get(4).put(person, count + 1);
				}
			}
		}
		
		return totals;
	}
	
	
}
