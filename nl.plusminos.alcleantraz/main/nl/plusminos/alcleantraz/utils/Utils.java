package nl.plusminos.alcleantraz.utils;

import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;

public class Utils {

	private Utils() {
		
	}

	public static void reportGeneration(GeneticAlgorithm<? extends Chromosome<?>> ga) {
		int gen = ga.getGeneration();
		if ((gen & 1023) == 0) {
			System.out.print("\nGeneration " + ga.getGeneration() + " - ");
		} else if ((gen & 127) == 0) {
			System.out.print("|");
		}
	}
	
	public static void resetBooleanArray(boolean[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = false;
		}
	}
	
	public static int countTrue(boolean[] array) {
		int result = 0;
		
		for (boolean b : array) {
			if (b) result++;
		}
		
		return result;
	}
}
