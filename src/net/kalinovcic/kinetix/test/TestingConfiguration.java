package net.kalinovcic.kinetix.test;

public class TestingConfiguration
{
	public TestingUnit head;
	
	public static class TestingUnit
	{
		public TestingUnit next;

		public int repeat;
		public double time;
		public double scale;
	}
}
