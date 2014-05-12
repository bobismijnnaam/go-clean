package nl.plusminos.alcleantraz.ginger.logic;

import gnu.trove.set.hash.TByteHashSet;

// TODO: TByteHashSet pooling?
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
	
	public void addToKitchen(byte p) {
		surjectiveCheck[Planning.TASK_KITCHEN].add(p);
		all.add(p);
		kitchen.add(p);
	}
	
	public void addToHallway(byte p) {
		surjectiveCheck[Planning.TASK_HALL].add(p);
		all.add(p);
		hall.add(p);
	}
	
	public void addToWc(byte p) {
		surjectiveCheck[Planning.TASK_WC].add(p);
		all.add(p);
		wc = p;
	}
	
	public void addToDouche(byte p) {
		surjectiveCheck[Planning.TASK_DOUCHE].add(p);
		all.add(p);
		douche = p;
	}
	
	public boolean hasNoDoubles() {
		if (hall == null) { // Check for hall week
			return all.size() == 5;
		} else {
			return all.size() == 7;
		}
	}
	
	public int getAmountOfDoubles() {
		if (hall == null) { // Check for hall week
			return 5 - all.size();
		} else {
			return 7 - all.size();
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
		}
		
		return true;
	}
	
	public boolean hasNoRecurringPersons(Week previousWeek, Week previousHallWeek) {
		boolean result = hasNoRecurringPersons(previousWeek);
		
		TByteHashSet hallCopy = new TByteHashSet(hall);
		hallCopy.retainAll(previousHallWeek.hall);
		result = result | hallCopy.size() == 0;
		
		return result;
	}
	
	public int getAmountOfRecurringPersons(Week previousWeek) {
		int count = 0;
		
		if (previousWeek.wc == wc) {
			count++;
		}
		
		if (previousWeek.douche == douche) {
			count++;
		}
		
		TByteHashSet kitchenCopy = new TByteHashSet(kitchen);
		kitchenCopy.retainAll(previousWeek.kitchen);
		count += kitchenCopy.size();
		
		return count;
	}
	
	public int getAmountOfRecurringPersons(Week previousWeek, Week previousHallWeek) {
		int count = getAmountOfRecurringPersons(previousWeek);
		
		TByteHashSet hallCopy = new TByteHashSet(hall);
		hallCopy.retainAll(previousHallWeek.hall);
		count += hallCopy.size();
		
		return count;
	}
}
