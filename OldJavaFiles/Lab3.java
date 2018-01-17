//Jordan Carr
//Lab 3
//Created 1/26/16

public class Lab3
{
	public static void main(String[] args)
	{
		double distance = 10;
		int steps = 0;
		while(distance != 0.0)
		{
			distance /= 2.0;
			steps++;
			System.out.println("Distance: " + distance + ", Steps: " + steps);
		}
	}
}