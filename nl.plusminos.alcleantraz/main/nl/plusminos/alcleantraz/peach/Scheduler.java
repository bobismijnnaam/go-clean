package nl.plusminos.alcleantraz.peach;

import java.util.ArrayList;

import nl.plusminos.alcleantraz.utils.BucketList.BucketList;

// TODO: Realtime statistics
// Multithreading. Is this even needed?
public class Scheduler implements Runnable {
	
	public final int THREADS;
	
//	BucketList<Individual> otherPop, pop;
	ArrayList<Individual> pop, otherPop;
	int bestFitness = 0;
	int[] totalFitness;
	Fitness fitness;
	Individual bestIndividual;
	Individual perfectIndividual;
	float perfectIndividualDistribution = 999999999;
	
	public Scheduler(int persons, int threeWeeks, int threads) {
		Info.setup(persons, threeWeeks);
		
		THREADS = threads; // Add threading support
		
//		pop = new BucketList<Individual>(Individual.class, Info.BUCKETSIZE, Info.BUCKETS);
//		otherPop = new BucketList<Individual>(Individual.class, Info.BUCKETSIZE, Info.BUCKETS);
		pop = new ArrayList<Individual>(Info.POPSIZE);
		otherPop = new ArrayList<Individual>(Info.POPSIZE);
		fitness = new Fitness();
		totalFitness = new int[Info.POPSIZE];
	}
	
	@Override
	public void run() {
		// Create population
		for (int i = 0; i < Info.POPSIZE; i++) {
			pop.add(new Individual().randomize());
			fitness.evaluate(pop.get(i));
			if (bestFitness < pop.get(i).getFitness()) {
				bestFitness = pop.get(i).getFitness();
				bestIndividual = pop.get(i);
			}
		}
		
		for (int i = 0; i < Info.MAXGENERATION; i++) {
			otherPop.add(bestIndividual);
			otherPop.add(new Individual().randomize());
			
			while(otherPop.size() < Info.POPSIZE) {
				Individual ind1 = findIndividualTournament();
				Individual ind2 = findIndividualTournament();
				
				Individual ind3 = Individual.getOffspring(ind1, ind2);
				
				if (Info.rand.nextFloat() < Info.MUTATIONCHANCE)
					ind1.mutate();
				
				if (Info.rand.nextFloat() < Info.MUTATIONCHANCE)
					ind2.mutate();
				
				otherPop.add(ind1);
				otherPop.add(ind2);
				otherPop.add(ind3);
			}
			
			pop.clear();
			ArrayList<Individual> t = pop;
			pop = otherPop;
			otherPop = t;
			
			bestFitness = pop.get(0).getFitness();
			bestIndividual = pop.get(0);
			for (int j = 1; j < Info.POPSIZE; j++) {
				fitness.evaluate(pop.get(j));
				if (bestFitness < pop.get(j).getFitness()) {
					bestFitness = pop.get(j).getFitness();
					bestIndividual = pop.get(j);
				}
			}
			
			if (bestFitness == fitness.totalScore) {
				fitness.setChromosome(bestIndividual.getChromosome());
				float distribution = fitness.gradeDistribution();
				if (distribution < perfectIndividualDistribution) {
					perfectIndividual = bestIndividual;
					perfectIndividualDistribution = distribution;
				}
			}
			
			if (i % 128 == 0 && i != 0) {
				System.out.print("|");
			}
			if (i % 1024 == 0 && i != 0) {
				System.out.println("] " + bestFitness + " - " + i);
				
				 System.out.println(FitnessDebugger.evaluate(bestIndividual, fitness));
			}
		}
		
//		bestFitness = pop.get(0).getFitness();
//		Individual ind = pop.get(0);
//		for (int i = 0; i < Info.POPSIZE; i++) {
//			if (pop.get(i).getFitness() > bestFitness) {
//				ind = pop.get(i);
//				bestFitness = pop.get(i).getFitness();
//			}
//		}
		System.out.println(FitnessDebugger.evaluate(perfectIndividual, fitness));
	}
	
	public void prepareTotalArray() {
		Individual ind = pop.get(0);
		totalFitness[0] = ind.getFitness();
		bestFitness = ind.getFitness();
		for (int i = 1; i < Info.POPSIZE; i++) {
			fitness.evaluate(pop.get(i));
			totalFitness[i] = totalFitness[i - 1] + pop.get(i).getFitness();
			if (bestFitness > pop.get(i).getFitness()) {
				bestFitness = pop.get(i).getFitness();
			}
		}
	}
	
	public Individual findIndividualRoulette() {
		int target = Info.nextInt(totalFitness[Info.POPSIZE - 1]);
		int lo = 0, hi = Info.POPSIZE - 1, pos = (hi - lo) / 2 + lo;
		
		while (hi > lo) {
			if (target >= totalFitness[pos]) {
				lo = pos + 1;
				pos = (hi - lo) / 2 + lo;
			} else {
				hi = pos - 1;
				pos = (hi - lo) / 2 + lo;
			}
		}
		
		return pop.get(pos);
	}
	
	protected Individual findIndividualTournament() {
		int size = pop.size();
		
		int h = Info.nextInt(size);
		Individual candidate = pop.get(h);
		
		for (int i=0; i < Info.ATTEMPTS; ++i) {
			int k = Info.nextInt(size);
			Individual challenger = pop.get(k);
			
			if(candidate != challenger) {
				if(challenger.getFitness() > candidate.getFitness()) {
					candidate = challenger;           
				}
			}
		}
		
		return candidate;
	} 
	
	public static void main(String[] args) {
		Thread scheduler = new Thread(new Scheduler(11, 7, 4));
		scheduler.start();
	}

}
