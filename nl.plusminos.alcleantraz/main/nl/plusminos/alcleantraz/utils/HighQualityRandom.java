package nl.plusminos.alcleantraz.utils;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Source: http://www.javamex.com/tutorials/random_numbers/numerical_recipes.shtml
 * "This generator is useful in cases where you need fast, good-quality randomness but don't need cryptographic randomness, as provided by the Java SecureRandom class. The code above is not much slower than java.util.Random and provides much better quality randomness and a much larger period. It is about 20 times faster than SecureRandom. Typical candidates for using this generator would be games and simulations (except games where money depends on the random number generator, such as in gambling applications)."
 * @author www.javamex.com
 *
 */
public class HighQualityRandom extends Random {
	private Lock l = new ReentrantLock();
	private long u;
	private long v = 4101842887655102017L;
	private long w = 1;
	
	public static final HighQualityRandom instance = new HighQualityRandom();
	
	public HighQualityRandom() {
		this(System.nanoTime());
	}
	
		public HighQualityRandom(long seed) {
		l.lock();
		u = seed ^ v;
		nextLong();
		v = u;
		nextLong();
		w = v;
		nextLong();
		l.unlock();
	}
	
	public long nextLong() {
		l.lock();
		try {
			u = u * 2862933555777941757L + 7046029254386353087L;
			v ^= v >>> 17;
			v ^= v << 31;
			v ^= v >>> 8;
			w = 4294957665L * (w & 0xffffffff) + (w >>> 32);
			long x = u ^ (u << 21);
			x ^= x >>> 35;
			x ^= x << 4;
			long ret = (x + v) ^ w;
			return ret;
		} finally {
		  l.unlock();
		}
	}
	
	protected int next(int bits) {
		return (int) (nextLong() >>> (64-bits));
	}
}
