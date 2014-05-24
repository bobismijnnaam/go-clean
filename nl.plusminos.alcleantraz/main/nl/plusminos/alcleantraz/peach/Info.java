package nl.plusminos.alcleantraz.peach;

import java.util.Random;

import nl.plusminos.alcleantraz.utils.HighQualityRandom;

public class Info {
//	public static final Random rand = new HighQualityRandom();
	public static final Random rand = new Random();
	
	public static final int JOBS_EXCLUDING = 5;
	public static final int JOBS_INCLUDING = 7;
	public static final int JOBS_THREEWEEKS = JOBS_EXCLUDING * 2 + JOBS_INCLUDING;
	
	public static final int JOB_KITCHEN = 0;
	public static final int JOB_TOILET = 1;
	public static final int JOB_SHOWER = 2;
	public static final int JOB_HALLWAY = 3;
	
	public static final int POPSIZE = 1200;
	public static final int MAXGENERATION = 20000;
	
	public static final float MUTATIONCHANCE = 0.2f; // 0.2
	public static final float XOVERCHANCE = 0.8f; // 0.8
	public static final int ATTEMPTS = 5;
	
	private static int PERSONS = -1;
	private static int THREEWEEKS = -1;
	private static int JOBS_TOTAL = -1;
	
	public static void setPersons(int persons) {
		if (PERSONS == -1) {
			PERSONS = persons;
		}
	}
	
	public static int getPersons() {
		return PERSONS;
	}
	
	public static void setThreeWeeks(int threeWeeks) {
		if (THREEWEEKS == -1) {
			THREEWEEKS = threeWeeks;
			
			JOBS_TOTAL = THREEWEEKS * JOBS_THREEWEEKS;
		}
	}
	
	public static void setup(int persons, int threeWeeks) {
		setPersons(persons);
		setThreeWeeks(threeWeeks);
	}
	
	public static int getThreeWeeks() {
		return THREEWEEKS;
	}
	
	public static int getTotalJobs() {
		return JOBS_TOTAL;
	}
	
	public static int nextInt(int bound) {
		return rand.nextInt(bound);
	}
	
	public static short nextPerson() {
		return (short) Math.pow(2, nextInt(PERSONS));
	}
	
	public static String getName(short id) {
		return "" + ((char) (65 + Math.log(id) / Math.log(2)));
	}
	
}
