package nl.plusminos.alcleantraz.peach;

import gnu.trove.list.array.TShortArrayList;

import java.util.ArrayList;

import jenes.chromosome.IntegerChromosome;
import nl.plusminos.alcleantraz.utils.ShortHammingWeight;

// TODO: Add distribution to standard evaluation?
// The only thing each function does is loop through all the weeks. It IS possible to merge them together!
public class Fitness {
	private short[] chrom;
	private TShortArrayList personArray;
	
	public Fitness() {
		personArray = new TShortArrayList(Info.getPersons());
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
		boolean recurrenceDetected = false;
		int weekStart = 0;
		
		// Maybe check the first hallway task with the last week of previous schedule?
		for (int tw = 0; tw < Info.getThreeWeeks(); tw++) {
			for (int w = 0; w < 3; w++) {
				weekStart = tw * Info.JOBS_THREEWEEKS + w * Info.JOBS_EXCLUDING;
				recurrenceDetected = false;
				
				previousKitchen = kitchen;
				kitchen = (short) (chrom[weekStart] | chrom[weekStart + 1] | chrom[weekStart + 2]);
				
				previousToilet = toilet;
				toilet = chrom[weekStart + 3];
				
				previousBathroom = bathroom;
				bathroom = chrom[weekStart + 4];
				
				if ((previousKitchen | kitchen) != (previousKitchen ^ kitchen)) { // Kitchen check
					quality++;
					recurrenceDetected = true;
//					System.out.println("Recurred kitchen [" + (tw * 3 + w) + "]");
				} else {
					if (previousToilet == toilet) { // Toilet check
						quality++;
						recurrenceDetected = true;
//						System.out.println("Recurred toilet [" + (tw * 3 + w) + "]");
					} else {
						if (previousBathroom == bathroom) { // Bathroom check
							quality++;
							recurrenceDetected = true;
//							System.out.println("Recurred bathroom [" + (tw * 3 + w) + "]");
						}
					}
				}
			}
			
			// Hallway check
			previousHallway = hallway;
			hallway = (short) (chrom[weekStart + 5] | chrom[weekStart + 6]);
			
			if ((previousHallway | hallway) != (previousHallway ^ hallway) && !recurrenceDetected) {
				quality++;
			}
		}
		
		
		return quality;
	}
	
	/**
	 * The lower the better!
	 * @return The amount of weeks in which a double was found
	 */
	public int getInjectionQuality() { // Is there a better way to do this (detecting when a number appears twice in a sequence)? Answer: yes. http://stackoverflow.com/questions/1532819/algorithm-efficient-way-to-remove-duplicate-integers-from-an-array With a merge sort that ignores doubles. Has some overhead though
		int quality = 0;
		int weekStart = 0;
		boolean doubleDetected = false;
		for (int tw = 0; tw < Info.getThreeWeeks(); tw++) {
			for (int w = 0; w < 3; w++) {
				weekStart = tw * Info.JOBS_THREEWEEKS + w * Info.JOBS_EXCLUDING;
				doubleDetected = false;
				personArray.resetQuick();
				personArray.add(chrom[weekStart]);
				
				// Regular check
				for (int j = 1; j < Info.JOBS_EXCLUDING; j++) {
					if (personArray.contains(chrom[weekStart + j])) {
						quality++;
						doubleDetected = true;
						break;
					} else {
						personArray.add(chrom[weekStart + j]);
					}
				}
			}
			
			// Hallway check
			if (!doubleDetected) {
				if (personArray.contains(chrom[weekStart + 5])) {
					quality++;
				} else {
					personArray.add(chrom[weekStart + 5]);
					if (personArray.contains(chrom[weekStart + 6])) {
						quality++;
					}
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
		
		for (int tw = 0; tw < Info.getThreeWeeks(); tw++) {
			for (int w = 0; w < 3; w++) {
				weekStart = tw * Info.JOBS_THREEWEEKS + w * Info.JOBS_EXCLUDING;
				
				kitchen |= chrom[weekStart] | chrom[weekStart + 1] | chrom[weekStart + 2];
				toilet |= chrom[weekStart + 3];
				bathroom |= chrom[weekStart + 4];
				hallway |= chrom[weekStart + 5] | chrom[weekStart + 6];
			}
		}
		
//		System.out.println("Kitchen: " + kitchen + " >=> " + ShortHammingWeight.ones[kitchen]);
//		System.out.println("Toilet: " + toilet + " >=> " + ShortHammingWeight.ones[toilet]);
//		System.out.println("Bathroom: " + bathroom + " >=> " + ShortHammingWeight.ones[bathroom]);
//		System.out.println("Hallway: " + hallway + " >=> " + ShortHammingWeight.ones[hallway]);
		
		int quality = 4 * Info.getPersons() - (ShortHammingWeight.ones[kitchen] + ShortHammingWeight.ones[toilet] + ShortHammingWeight.ones[bathroom] + ShortHammingWeight.ones[hallway]);
		
		return quality;
	}
	
	/**
	 * The lower the better!
	 * @return I think the variance?
	 */
	public float gradeDistribution() {
		float jpp = (float) Info.getTotalJobs() / (float) Info.getPersons();
		float[] hasJobs = new float[Info.getPersons()];
		
		int person, jobPos;
		
		for (int tw = 0; tw < Info.getThreeWeeks(); tw++) {
			for (int w = 0; w < 3; w++) {
				for (int j = 0; j < (w == 3 ? 7 : 5); j++) {
					jobPos = tw * Info.JOBS_THREEWEEKS + w * Info.JOBS_EXCLUDING + j; 
					person = chrom[jobPos];
					hasJobs[(int) (Math.log(person) / Math.log(2))]++;
				}
			}
		}
		
		float total = 0;
		for (int p = 0; p < Info.getPersons(); p++) {
			hasJobs[p] -= jpp;
			hasJobs[p] *= hasJobs[p];
			total += hasJobs[p];
		}
		
		return total;
	}
	
	public void evaluate(Individual individual) {
		setChromosome(individual.getChromosome());
		
		if (individual.isDirty()) {
			int sum = getPerfectAssignmentQuality();
			sum += getInjectionQuality();
			sum += getRecurrenceQuality();
			sum += gradeDistribution(); // TODO: Bottleneck!
			
			individual.setFitness(sum);
		}
	}
	
	public static void main(String[] args) {
		Info.setup(7, 10);
		
		Fitness fitness = new Fitness();
		
		int amount = 10000;
		for (int i = 0; i < amount; i++) {
			Individual ind = new Individual().randomize();
			String result = FitnessDebugger.evaluate(ind, fitness);
			if (result.contains("INCORRECTLY")) {
				System.out.println(result);
				
				return;
			}
		}
		
		System.out.println(amount + " rosters tested successfully!");
	}

}
