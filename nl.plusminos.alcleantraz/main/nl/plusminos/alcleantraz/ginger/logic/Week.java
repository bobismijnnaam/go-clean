package nl.plusminos.alcleantraz.ginger.logic;

import gnu.trove.set.hash.TByteHashSet;
import jenes.chromosome.IntegerChromosome;

// TODO: TByteHashSet pooling?
// TODO: Caching! Of fitness
public class Week {
	private final TByteHashSet[] surjectiveCheck;
	
	public final TByteHashSet all = new TByteHashSet(7);
	
	public final TByteHashSet kitchen = new TByteHashSet(3);
	public final TByteHashSet hall;
	public byte wc = 0;
	public byte douche = 0;
	
	public Week(TByteHashSet[] surjectiveCheck, boolean hallWeek) { // TODO
		this.surjectiveCheck = surjectiveCheck;
		
		if (hallWeek)
			hall = new TByteHashSet(2);
		else
			hall = null;
	}
	
	private void addToKitchen(byte p) {
		surjectiveCheck[Planning.TASK_KITCHEN].add(p);
		all.add(p);
		kitchen.add(p);
	}
	
	private void addToHallway(byte p) {
		surjectiveCheck[Planning.TASK_HALL].add(p);
		all.add(p);
		hall.add(p);
	}
	
	private void addToWc(byte p) {
		surjectiveCheck[Planning.TASK_WC].add(p);
		all.add(p);
		wc = p;
	}
	
	private void addToDouche(byte p) {
		surjectiveCheck[Planning.TASK_DOUCHE].add(p);
		all.add(p);
		douche = p;
	}
	
	public boolean hasNoPersonTwice() {
		if (hall == null) { // Check for hall week
			return all.size() == 5;
		} else {
			return all.size() == 7;
		}
	}
	
	public boolean hasNoRecurringPersons(Week previousWeek) { // TODO: Make it so that no new TByteHashSets have to be made
		if (previousWeek.wc == wc) {
			return false;
		} else if (previousWeek.douche == douche) {
			return false;
		} else {
			TByteHashSet kitchenCopy = new TByteHashSet(kitchen);
			kitchenCopy.retainAll(previousWeek.kitchen);
			if (kitchenCopy.size() > 0) {
				return false;
			}
			
			TByteHashSet hallCopy = new TByteHashSet(hall);
			hallCopy.retainAll(previousWeek.hall);
			if (hallCopy.size() > 0) {
				return false;
			}
		}
		
		return true;
	}
}
