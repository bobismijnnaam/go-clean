package nl.plusminos.alcleantraz.utils;

import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;

public class Utils {

	private Utils() {
		// TODO Auto-generated constructor stub
	}

	public static void reportGeneration(GeneticAlgorithm<? extends Chromosome<?>> ga) {
		int gen = ga.getGeneration();
		if ((gen & 1023) == 0) {
			System.out.print("\nGeneration " + ga.getGeneration() + " - ");
		} else if ((gen & 127) == 0) {
			System.out.print("|");
		}
	}
}
