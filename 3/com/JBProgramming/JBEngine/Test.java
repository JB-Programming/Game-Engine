package com.JBProgramming.JBEngine;

public class Test extends JBMain {

	private static final long serialVersionUID = 3875699108876436831L;

	public static void main(String[] args) {
		new Test();
	}

	public Test() {
		super("Test", 500, 500, false, true, null);
	}

	@Override
	public void tick() {
		System.out.println(getAverageFPS(30.5432523D));
	}
	
	public void printArray(double[] array) {
		for (double d : array) {
			System.out.print(d + ", ");
		}
		System.out.println();
	}

	@Override
	public void render() {
		
	}

}
