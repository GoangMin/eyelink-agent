package com.m2u.eyelink.agent.sample;

public class HelloWorld {
	public String aaa = "a";
	public String getAaa()  {
		return this.aaa;
	}
    public static void main(String[] args) {
    	HelloWorld hw = new HelloWorld();
        // Prints "Hello, World" to the terminal window.
        System.out.println("Hello, World");
        System.out.println("aaa : " + hw.getAaa());
    }

}