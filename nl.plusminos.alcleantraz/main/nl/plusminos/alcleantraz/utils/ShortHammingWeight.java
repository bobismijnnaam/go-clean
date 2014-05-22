package nl.plusminos.alcleantraz.utils;

public class ShortHammingWeight {

	private ShortHammingWeight() {	
	}
	
	public static final byte[] ones = init();
	
	private static byte[] init() {
		byte[] result = new byte[(1 << 11)];
		
		for (int i = 0; i < (1 << 11); i++) {
			result[i] = (byte) Integer.bitCount(i);
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println((1 << 11) - 1);
		System.out.println(ShortHammingWeight.ones[(1 << 11) - 1]);
	}
}
