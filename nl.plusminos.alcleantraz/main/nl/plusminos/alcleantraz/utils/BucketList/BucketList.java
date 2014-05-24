package nl.plusminos.alcleantraz.utils.BucketList;

import java.lang.reflect.Array;

/**
 * Kind of a pointless class actually :')
 * @author Bob
 *
 * @param <T>
 */
public class BucketList<T> {
	private Bucket<T>[] buckets;
	private int totalCapacity;
	private int size;
	private int bucketSize;
	private int currentBucket = 0;
	
	public BucketList(Class<T> c, int bucketSize, int numberOfBuckets) {
		@SuppressWarnings("unchecked")
		Bucket<T>[] buckets = (Bucket<T>[]) Array.newInstance(Bucket.class, numberOfBuckets);
		this.buckets = buckets;
		
		for (int i = 0; i < numberOfBuckets; i++) {
			buckets[i] = new Bucket<T>(c, bucketSize);
		}
		
		size = 0;
		totalCapacity = bucketSize * numberOfBuckets;
		this.bucketSize = bucketSize;  
	}
	
	public T get(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException();
		}
		
		return getQuick(index);
	}
	
	public T getQuick(int index) {
		int bucket = index / bucketSize;
		return buckets[bucket].getQuick(index % bucketSize);
	}
	
	public boolean add(T obj) {
		while (currentBucket < buckets.length && buckets[currentBucket].isFull()) {
			currentBucket++;
		}
		
		if (currentBucket == buckets.length) {
			return false;
		}
		
		size++;
		
		return buckets[currentBucket].add(obj);
	}

	public int size() {
		return size;
	}
	
	public int totalCapacity() {
		return totalCapacity;
	}
	
	public void reset() {
		for (int i = 0; i < buckets.length; i++) {
			buckets[i].reset();
		}
		
		currentBucket = 0;
	}
	
	public void resetQuick() {
		for (int i = 0; i < buckets.length; i++) {
			buckets[i].resetQuick();
		}
		
		currentBucket = 0;
	}
	
	public Bucket<T> getBucket(int index) {
		return buckets[index];
	}
	
	public static void main(String[] args) {
		BucketList<Integer> bl = new BucketList<>(Integer.class, 250, 4);
		
		System.out.println("Size: " + bl.size());
		System.out.println("LEVEL UP!");
		
		for (int i = 0; i < 1000; i++) {
			bl.add(i);
		}
		
		System.out.println("Size: " + bl.size());
		System.out.println("Capacity: " + bl.totalCapacity());
		
		for (int i = 0; i < 1000; i++) {
			System.out.print(bl.get(i) + "-");
		}
		
		System.out.println();
		bl.reset();
		
		for (int i = 0; i < 1000; i++) {
			bl.add(i);
		}
		
		bl.resetQuick();
		
		for (int i = 1000; i < 1634; i++) {
			bl.add(i);
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.print(bl.getQuick(i) + "-");
		}
		
		System.out.println();
	}
}
