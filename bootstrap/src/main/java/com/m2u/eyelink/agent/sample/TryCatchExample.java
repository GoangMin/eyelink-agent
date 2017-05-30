package com.m2u.eyelink.agent.sample;

public class TryCatchExample {
	public static void main(String[] args) {
		try {
			System.out.println(args[0]);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
