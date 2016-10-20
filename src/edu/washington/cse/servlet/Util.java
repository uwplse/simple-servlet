package edu.washington.cse.servlet;

public class Util {
	public static boolean nondetBool() {
		return System.currentTimeMillis() == 0;
	}
	
	public static int nondetInt() {
		return (int) System.currentTimeMillis();
	}
}
