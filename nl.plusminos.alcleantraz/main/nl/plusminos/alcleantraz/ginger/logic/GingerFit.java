package nl.plusminos.alcleantraz.ginger.logic;

import jenes.chromosome.IntegerChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;

public class GingerFit extends Fitness<IntegerChromosome> {
	
	public class PlanningQuality {
		public int doubleQuality;
		public int recurringQuality;
		public int perfectAssignmentQuality;
		
		public int hasNoDoubles;
		public int hasNoRecurring;
		public int hasPerfectAssignment;
		public boolean bHasNoDoubles;
		public boolean bHasNoRecurring;
		public boolean bHasPerfectAssignment;
		
		@Override
		public String toString() {
			String result = "";
			result += ("doubleQuality: " + doubleQuality);
			result += ("\nrecurringQuality: " + recurringQuality);
			result += ("\nperfectAssignmentQuality: " + perfectAssignmentQuality);
			result += ("\nhasNoDoubles: " + bHasNoDoubles);
			result += ("\nhasNoRecurring: " + bHasNoRecurring);
			result += ("\nhasPerfectAssignment: " + bHasPerfectAssignment);
			return result;
		}
	}
	
	public final int PERSONS;
	public final boolean QUANTITATIVE;
	public final int THREEWEEKS;
	
	private Planning pl;
	
	public GingerFit(int persons, boolean quantitative, int threeWeeks) {
		super(false, false, false); // For multi-objective genetic algorithm
		
		PERSONS = persons;
		QUANTITATIVE = quantitative;
		THREEWEEKS = threeWeeks;
		pl = new Planning(threeWeeks, persons);
	}
	
	/*
	 * 	- Iedereen alles ongeveer evenveel (zo goed mogelijk; soft)
	g Elke persoon max. 1 x per week (hard)
	- Als een persoon keukencorvee heeft mag hij de week ervoor/erna geen keukencorvee doen. (hard) (geld voor alle categorieën)
	- Gang corvee is elke 3 weken (hard)
	- De laatste week van die 3 weken mag die persoon geen ander corvee hebben (hard)
	- Rekening houden met laatste week van het oude rooster (hard)
	g In het hele rooster moet iedereen alles minstens 1 x gedaan hebben (hard).
	 */
	
	public PlanningQuality getQuality(Individual<IntegerChromosome> individual, boolean quantitative, boolean binary) {
		PlanningQuality pq = new PlanningQuality();
		pl.initialize(individual.getChromosome());
		
		if (quantitative) {
			pq.doubleQuality = pl.getDoublesQuality();
			pq.recurringQuality = pl.getNoRecurringPersonsQuality();
			pq.perfectAssignmentQuality = pl.getPerfectAssignmentQuality();
		}
		if (binary) {
			pq.bHasNoDoubles = pl.hasNoWeeksWithDoubles();
			pq.bHasNoRecurring = pl.hasNoRecurringPersons();
			pq.bHasPerfectAssignment = pl.hasPerfectAssignment();
			
			pq.hasNoDoubles = pq.bHasNoDoubles ? 0 : 1;
			pq.hasNoRecurring = pq.bHasNoRecurring ? 0 : 1;
			pq.hasPerfectAssignment = pq.bHasPerfectAssignment ? 0 : 1;
		}
		
		return pq;
	}
	
	@Override
	public void evaluate(Individual<IntegerChromosome> individual) {
		PlanningQuality pq = getQuality(individual, QUANTITATIVE, !QUANTITATIVE);	
		
		if (QUANTITATIVE) {
			individual.setScore(pq.doubleQuality, pq.recurringQuality, pq.perfectAssignmentQuality);
		} else {
			individual.setScore(pq.hasNoDoubles, pq.hasNoRecurring, pq.hasPerfectAssignment);
		}
	}
	
	public void printEvaluation(Individual<IntegerChromosome> individual) {
		Planning pl = new Planning(THREEWEEKS, PERSONS);
		pl.initialize(individual.getChromosome());
		PlanningQuality pq = getQuality(individual, QUANTITATIVE, !QUANTITATIVE);
		pl.hasPerfectAssignment(1);
		System.out.println(pq.toString());
	}

}
