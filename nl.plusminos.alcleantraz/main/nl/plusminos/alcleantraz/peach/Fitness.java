package nl.plusminos.alcleantraz.peach;

import nl.plusminos.alcleantraz.utils.ShortHammingWeight;
import gnu.trove.list.array.TShortArrayList;

// TODO: The only thing each function does is loop through all the weeks. It IS possible to merge them together!
public class Fitness {
	public final int PERSONS;
	public final int THREEWEEKS;
	public final int JOBS_EXCLUDING = 5;
	public final int JOBS_INCLUDING = 7;
	public final int JOBS_THREEWEEKS = JOBS_EXCLUDING * 2 + JOBS_INCLUDING;
	
	private short[] chrom;
	private TShortArrayList personArray;
	
	public Fitness(int persons, int threeWeeks) {
		PERSONS = persons;
		THREEWEEKS = threeWeeks;
		
		personArray = new TShortArrayList(PERSONS);
	}
	
	public void setChromosome(short[] chrom) {
		this.chrom = chrom;
	}
	
	/**
	 * The lower the better!
	 * @return The amount of weeks where a person was found doing the same thing as previous week
	 */
	public int getRecurrenceQuality() {
		// Init variables
		int quality = 0;
		short previousKitchen = 0, kitchen = 0;
		short previousBathroom = 0, bathroom = 0;
		short previousToilet = 0, toilet = 0;
		short previousHallway = 0, hallway = 0;
		int weekStart = 0;
		
		// For the first three weeks
		for (int w = 0; w < 3; w++) {
			weekStart = 0 * JOBS_THREEWEEKS + w * JOBS_EXCLUDING;
			
			previousKitchen = kitchen;
			kitchen = (short) (chrom[weekStart] | chrom[weekStart + 1] | chrom[weekStart + 2]);
			
			if ((previousKitchen | kitchen) != (previousKitchen ^ kitchen)) { // Kitchen check
				quality++;
			} else {
				previousToilet = toilet;
				toilet = chrom[weekStart + 3];
				if (previousToilet == toilet) { // Toilet check
					quality++;
				} else {
					previousBathroom = bathroom;
					bathroom = chrom[weekStart + 4];
					if (previousBathroom == bathroom) { // Bathroom check
						quality++;
					}
				}
			}
		}
		
		// Maybe check the first hallway task with the last week of previous schedule?
		
		for (int tw = 1; tw < THREEWEEKS; tw++) {
			for (int w = 0; w < 3; w++) {
				weekStart = tw * JOBS_THREEWEEKS + w * JOBS_EXCLUDING;
				
				previousKitchen = kitchen;
				kitchen = (short) (chrom[weekStart] | chrom[weekStart + 1] | chrom[weekStart + 2]);
				
				if ((previousKitchen | kitchen) != (previousKitchen ^ kitchen)) { // Kitchen check
					quality++;
				} else {
					previousToilet = toilet;
					toilet = chrom[weekStart + 3];
					if (previousToilet == toilet) { // Toilet check
						quality++;
					} else {
						previousBathroom = bathroom;
						bathroom = chrom[weekStart + 4];
						if (previousBathroom == bathroom) { // Bathroom check
							quality++;
						}
					}
				}
			}
			
			// Hallway check
			previousHallway = hallway;
			hallway = (short) (chrom[weekStart + 5] | chrom[weekStart + 6]);
			
			if ((previousHallway | hallway) != (previousHallway ^ hallway)) {
				quality++;
			}
		}
		
		
		return quality;
	}
	
	/**
	 * The lower the better!
	 * @return The amount of weeks in which a double was found
	 */
	public int getInjectionQuality() { // TODO: Is there a better way to do this (detecting when a number appears twice in a sequence)?
		int quality = 0;
		int weekStart = 0;
		
		for (int tw = 1; tw < THREEWEEKS; tw++) {
			for (int w = 0; w < 3; w++) {
				weekStart = tw * JOBS_THREEWEEKS + w * JOBS_EXCLUDING;
				personArray.resetQuick();
				personArray.add(chrom[weekStart]);
				
				// Regular check
				for (int j = 1; j < JOBS_EXCLUDING; j++) {
					if (personArray.contains(chrom[weekStart + j])) {
						quality++;
						break;
					} else {
						personArray.add(chrom[weekStart + j]);
					}
				}
			}
			
			// Hallway check
			if (personArray.contains(chrom[weekStart + 5])) {
				quality++;
			} else {
				personArray.add(chrom[weekStart + 5]);
				if (personArray.contains(chrom[weekStart + 6])) {
					quality++;
				}
			}
		}
		
		return quality;
	}
	
	/**
	 * The lower the better!
	 * @return The amount of person/job pairs that where unused
	 */
	public int getPerfectAssignmentQuality() {
		short kitchen = 0, toilet = 0, bathroom = 0, hallway = 0;
		int weekStart = 0;
		
		for (int tw = 1; tw < THREEWEEKS; tw++) {
			for (int w = 0; w < 3; w++) {
				weekStart = tw * JOBS_THREEWEEKS + w * JOBS_EXCLUDING;
				
				kitchen |= chrom[weekStart] | chrom[weekStart + 1] | chrom[weekStart + 2];
				toilet |= chrom[weekStart + 3];
				bathroom |= chrom[weekStart + 4];
				hallway |= chrom[weekStart + 5] | chrom[weekStart + 6];
			}
		}
		
		int quality = 4 * 11 - (ShortHammingWeight.ones[kitchen] + ShortHammingWeight.ones[toilet] + ShortHammingWeight.ones[bathroom] + ShortHammingWeight.ones[hallway]);
		
		return quality;
	}
	
	public static void main(String[] args) {
		
	}

}
