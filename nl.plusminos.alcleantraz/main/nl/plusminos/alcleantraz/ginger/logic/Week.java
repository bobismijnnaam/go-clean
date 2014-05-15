package nl.plusminos.alcleantraz.ginger.logic;

import java.util.ArrayList;
import java.util.HashSet;

public class Week {
	private ArrayList<HashSet<Byte>> surjectiveCheck;
	
	public HashSet<Byte> all = new HashSet<Byte>(7);
	
	public HashSet<Byte> kitchen = new HashSet<Byte>(3);
	public HashSet<Byte> hall;
	public HashSet<Byte> tempHashSet = new HashSet<Byte>(3);
	private boolean isHallWeek = false;
	public byte wc = 0;
	public byte douche = 0;
	
	
	public Week(ArrayList<HashSet<Byte>> surjectiveCheck, boolean hallWeek) {
		isHallWeek = hallWeek;
		
		if (hallWeek) {
			hall = new HashSet<Byte>(2);
		}
		
		this.surjectiveCheck = surjectiveCheck;
	}
	
	public void initialize() {
		all.clear();
		kitchen.clear();
		if (hall != null) hall.clear();;
		wc = 0;
		douche = 0;
	}
	
	public void addToKitchen(byte p) {
		surjectiveCheck.get(Planning.TASK_KITCHEN).add(p);
		all.add(p);
		kitchen.add(p);
	}
	
	public void addToHallway(byte p) {
		surjectiveCheck.get(Planning.TASK_HALL).add(p);
		all.add(p);
		hall.add(p);
	}
	
	public void addToWc(byte p) {
		surjectiveCheck.get(Planning.TASK_WC).add(p);
		all.add(p);
		wc = p;
	}
	
	public void addToDouche(byte p) {
		surjectiveCheck.get(Planning.TASK_DOUCHE).add(p);
		all.add(p);
		douche = p;
	}
	
	public boolean isHallWeek() {
		return isHallWeek;
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
			HashSet<Byte> kitchenCopy = new HashSet<Byte>(kitchen);
			kitchenCopy.retainAll(previousWeek.kitchen);
			if (kitchenCopy.size() > 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean hasNoRecurringPersons(Week previousWeek, Week previousHallWeek) {
		boolean result = hasNoRecurringPersons(previousWeek);
		
		HashSet<Byte> hallCopy = new HashSet<Byte>(hall);
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
		
		tempHashSet.clear();
		tempHashSet.addAll(kitchen);
		tempHashSet.retainAll(previousWeek.kitchen);
		count += tempHashSet.size();
		
		return count;
	}
	
	public int getAmountOfRecurringPersons(Week previousWeek, Week previousHallWeek) {
		int count = getAmountOfRecurringPersons(previousWeek);
		
		tempHashSet.clear();
		tempHashSet.addAll(hall);
		tempHashSet.retainAll(previousHallWeek.hall);
		count += tempHashSet.size();
		
		return count;
	}
}
