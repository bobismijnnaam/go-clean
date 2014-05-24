package nl.plusminos.alcleantraz.utils.BucketList;

import java.lang.reflect.Array;

public class Bucket<T> {
	private T[] array;
	private int size;
	
	public Bucket(Class<T> c, int capacity) {
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(c, capacity);
		this.array = array;
	}
	
	public T get(int i) {
		if (i >= size) {
			throw new IndexOutOfBoundsException();
		}
		
		return array[i];
	}
	
	public T getQuick(int i) {
		return array[i];
	}
	
	public void reset() {
		size = 0;
		for (int i = 0; i < array.length; i++) {
			array[i] = null;
		}
	}
	
	public void resetQuick() {
		size = 0;
	}
	
	public boolean skim() {
		if (size != array.length) {
			for (int i = size; i < array.length; i++) {
				array[i] = null;
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean add(T obj) {
		if (size < array.length) {
			size++;
			array[size - 1] = obj;
			return true;
		} else {
			return false;
		}
	}
	
	public int size() {
		return size;
	}
	
	public boolean isFull() {
		return size == array.length;
	}
	
	public static void main(String[] args) {
		Bucket<Integer> b = new Bucket<>(Integer.class, 10);
		
		for (int i = 0; i < 5; i++) {
			b.add(i);
		}
		
		System.out.println("---");
		System.out.println(b.get(3));
		System.out.println(b.get(2));
//		System.out.println(b.get(7));
		System.out.println(b.getQuick(7));
		System.out.println("---\n");
		
		System.out.println("---");
		b.reset();
		System.out.println("Size after resetting: " + b.size());
//		System.out.println(b.get(1));
		
		for (int i = 0; i < 10; i++) {
			b.add(i);
		}
		b.resetQuick();
		System.out.println(b.getQuick(3));
		System.out.println(b.getQuick(1));
		System.out.println(b.size());
		System.out.println("---\n");
		
		System.out.println("---");
		b.reset();
		for (int i = 0; i < 10; i++) {
			b.add(i);
		}
		b.resetQuick();
		for (int i = 0; i < 7; i++) {
			b.add(i * 10);
		}
		b.skim();
		for (int i = 0; i < 10; i++) {
			System.out.println(b.getQuick(i));
		}
		System.out.println("---\n");
	}
	
}
