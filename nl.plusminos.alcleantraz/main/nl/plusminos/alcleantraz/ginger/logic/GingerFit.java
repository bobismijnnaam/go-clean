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
	}
	
	public final int PERSONS;
	public final boolean QUANTITATIVE;
	
	public GingerFit(int persons, boolean quantitative) {
		super(false, false, false); // For multi-objective genetic algorithm
		
		PERSONS = persons;
		QUANTITATIVE = quantitative;
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
		Planning pl = new Planning(individual.getChromosome(), PERSONS);
		
		if (quantitative) {
			pq.doubleQuality = pl.getDoublesQuality();
			pq.recurringQuality = pl.getNoRecurringPersonsQuality();
			pq.perfectAssignmentQuality = pl.getPerfectAssignmentQuality();
		}
		if (binary) {
			pq.hasNoDoubles = pl.hasNoWeeksWithDoubles() ? 0 : 1;
			pq.hasNoRecurring = pl.hasNoRecurringPersons() ? 0 : 1;
			pq.hasPerfectAssignment = pl.hasPerfectAssignment() ? 0 : 1;
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
		Planning pl = new Planning(individual.getChromosome(), PERSONS);
		
		System.out.println("hasNoDoubles: " + pl.hasNoWeeksWithDoubles());
		System.out.println("hasNoRecurring: " + pl.hasNoRecurringPersons());
		System.out.println("hasPerfectAssignment: " + pl.hasPerfectAssignment());
		System.out.println("doublesQuality: " + pl.getDoublesQuality());
		System.out.println("recurringQuality: " + pl.getNoRecurringPersonsQuality());
		System.out.println("perfectAssignmentQuality: " + pl.getPerfectAssignmentQuality());
	}

}
