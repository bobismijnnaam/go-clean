package nl.plusminos.alcleantraz.utils;

import java.util.ArrayList;

public class ArgumentParser {
	
	ArrayList<String> persons = new ArrayList<String>();
	ArrayList<String> jobs = new ArrayList<String>();
	int weeks = 0;
	
	/**
	 * Source: http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java
	 * @param input The string to check
	 * @return True/false
	 */
	public static boolean isInteger( String input ) {
	    try {
	        Integer.parseInt( input );
	        return true;
	    }
	    catch(Exception e) {
	        return false;
	    }
	}
	
	public ArgumentParser(String[] args) {
		int breakIndex = 0;
		for (int i = 0; i < args.length; i++) {
			if (isInteger(args[i])) {
				breakIndex = i;
				break;
			}
		}
		
		for (int i = 0; i < breakIndex; i++) {
			persons.add(args[i]);
		}
		
		weeks = Integer.parseInt(args[breakIndex]);
		
		for (int i = breakIndex + 1; i < args.length; i++) {
			jobs.add(args[i]);
		}
	}
	
	public String[] getPersons() {
		return persons.toArray(new String[persons.size()]);
	}

	public String[] getJobs() {
		return jobs.toArray(new String[jobs.size()]);
	}
	
	public String[] getReducedJobs(int minus) {
		String[] lessJobs = new String[jobs.size() - minus];
		
		for (int i = 0; i < lessJobs.length; i++) {
			lessJobs[i] = jobs.get(i);
		}
		
		return lessJobs;
	}
	
	public int getWeeks() {
		return weeks;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder(persons.size() * (8 + 8) + jobs.size() * (8 + 8) + 10);
		
		for (int i = 0; i < persons.size(); i++) {
			result.append("Person: \"");
			result.append(persons.get(i));
			result.append("\"\n");
		}
		
		
		
		for (int i = 0; i < jobs.size(); i++) {
			result.append("Job: \"");
			result.append(jobs.get(i));
			result.append("\"\n");
		}
		
		result.append("Amount of weeks: \"");
		result.append(Integer.toString(weeks));
		result.append("\"\n");
		
		return result.toString();
	}
}
