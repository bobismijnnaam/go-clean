package nl.plusminos.alcleantraz.ginger.logic;

import gnu.trove.list.array.TByteArrayList;

import java.util.HashSet;

import nl.plusminos.alcleantraz.utils.Utils;

public class Week {
	private boolean[][] surjectiveCheck;
	
	public boolean[] all;
	
	public boolean[] kitchen;
	public boolean[] hall;
//	public HashSet<Byte> tempHashSet = new HashSet<Byte>(3);
	private boolean isHallWeek = false;
	public byte wc = 0;
	public byte douche = 0;
	
	public TByteArrayList kitchenCollection;
	public TByteArrayList hallCollection;
	
	private final int PERSONS;
	
	public Week(boolean[][] surjectiveCheck, boolean hallWeek, int persons) {
		isHallWeek = hallWeek;
		this.surjectiveCheck = surjectiveCheck;
		this.PERSONS = persons;
		
		if (hallWeek) {
			hall = new boolean[PERSONS];
			hallCollection = new TByteArrayList(2);
		}
		kitchenCollection = new TByteArrayList(3);
		all = new boolean[PERSONS];
		kitchen = new boolean[PERSONS];
		hall = new boolean[PERSONS];
	}
	
	public void initialize() {
		Utils.resetBooleanArray(all);
		Utils.resetBooleanArray(kitchen);
		if (hall != null) Utils.resetBooleanArray(hall);;
		wc = 0;
		douche = 0;
		
		if (isHallWeek) hallCollection.resetQuick();
		kitchenCollection.resetQuick();
	}
	
	public void addToKitchen(byte p) {
		surjectiveCheck[Planning.TASK_KITCHEN][p] = true;
		all[p] = true;
		kitchen[p] = true;
		kitchenCollection.add(p);
	}
	
	public void addToHallway(byte p) {
		surjectiveCheck[Planning.TASK_HALL][p] = true;
		all[p] = true;
		hall[p] = true;
		hallCollection.add(p);
	}
	
	public void addToWc(byte p) {
		surjectiveCheck[Planning.TASK_WC][p] = true;
		all[p] = true;
		wc = p;
	}
	
	public void addToDouche(byte p) {
		surjectiveCheck[Planning.TASK_DOUCHE][p] = true;
		all[p] = true;
		douche = p;
	}
	
	public boolean isHallWeek() {
		return isHallWeek;
	}
	
	public boolean hasNoDoubles() {
		if (hall == null) { // Check for hall week
			return Utils.countTrue(all) == 5;
		} else {
			return Utils.countTrue(all) == 7;
		}
	}
	
	public int getAmountOfDoubles() {
		if (hall == null) { // Check for hall week
			return 5 - Utils.countTrue(all);
		} else {
			return 7 - Utils.countTrue(all);
		}
	}
	
	public boolean hasNoRecurringPersons(Week previousWeek) {
		if (previousWeek.wc == wc) {
			return false;
		} else if (previousWeek.douche == douche) {
			return false;
		} else {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (kitchenCollection.getQuick(i) == previousWeek.kitchenCollection.getQuick(j)) {
						return false;
					}
				}
			}
			
//			HashSet<Byte> kitchenCopy = new HashSet<Byte>(kitchen);
//			kitchenCopy.retainAll(previousWeek.kitchen);
//			if (kitchenCopy.size() > 0) {
//				return false;
//			}
		}
		
		return true;
	}
	
	public boolean hasNoRecurringPersons(Week previousWeek, Week previousHallWeek) {
		if (!hasNoRecurringPersons(previousWeek)) {
			return false;
		} else {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					if (hallCollection.getQuick(i) == previousHallWeek.hallCollection.getQuick(j)) {
						return false;
					}
				}
			}
		}
		
//		boolean result = hasNoRecurringPersons(previousWeek);
//		
//		HashSet<Byte> hallCopy = new HashSet<Byte>(hall);
//		hallCopy.retainAll(previousHallWeek.hall);
//		result = result | hallCopy.size() == 0;
		
		return true;
	}
	
	/**
	 * Deprecated - only use hasNoRecurringPersons and then count the weeks. THIS FUNCTION DOES NOT WORK
	 * @param previousWeek
	 * @return
	 */
	@Deprecated
	public int getAmountOfRecurringPersons(Week previousWeek) {
		int count = 0;
		
		if (previousWeek.wc == wc) {
			count++;
		}
		
		if (previousWeek.douche == douche) {
			count++;
		}
		
//		tempHashSet.clear();
//		tempHashSet.addAll(kitchen);
//		tempHashSet.retainAll(previousWeek.kitchen);
//		count += tempHashSet.size();
		
		return count;
	}
	
	/**
	 * Deprecated - only use hasNoRecurringPersons and then count the weeks. THIS FUNCTION DOES NOT WORK
	 * @param previousWeek
	 * @param previousHallWeek
	 * @return
	 */
	@Deprecated
	public int getAmountOfRecurringPersons(Week previousWeek, Week previousHallWeek) {
		int count = getAmountOfRecurringPersons(previousWeek);
		
//		tempHashSet.clear();
//		tempHashSet.addAll(hall);
//		tempHashSet.retainAll(previousHallWeek.hall);
//		count += tempHashSet.size();
		
		return count;
	}
}
