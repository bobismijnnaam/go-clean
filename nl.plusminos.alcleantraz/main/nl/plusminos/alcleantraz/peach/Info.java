package nl.plusminos.alcleantraz.peach;

import java.util.Random;

import nl.plusminos.alcleantraz.utils.HighQualityRandom;

public class Info {
	public static final Random rand = new HighQualityRandom();
	public static final int JOBS_EXCLUDING = 5;
	public static final int JOBS_INCLUDING = 7;
	public static final int JOBS_THREEWEEKS = JOBS_EXCLUDING * 2 + JOBS_INCLUDING;
	
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
		return (short) nextInt(PERSONS);
	}
	
}
