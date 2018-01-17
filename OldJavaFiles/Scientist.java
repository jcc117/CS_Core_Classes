// Jordan Carr
// Scientist File
// 3/15/16

public class Scientist
{
	private int[] coordinates = new int[5];
	private World world;
	public Scientist(World thisWorld)
	{
		world = thisWorld;
		coordinates[0] = 0;
		coordinates[1] = 0;
		coordinates[2] = 0;
		coordinates[3] = 0;
		coordinates[4] = 0;
	}
	
	//Move user to specified coordinate
	public int[] move(int dimension, int numUnits)
	{
		//Ensure dimensions loop on themselves
		int newSpot = (coordinates[dimension - 1] + numUnits) % 10;
		if(newSpot < 0)
		{
			newSpot = 10 - newSpot;
			coordinates[dimension - 1] = newSpot;
		}
		else
		{
			coordinates[dimension - 1] = newSpot;
		}
		//Retrieve color and record the current coordinates in world
		int color = world.visit(coordinates);
		
		//Return new coordinates and color to the main class
		int[] returnArray = new int[6];
		for(int i = 0; i < coordinates.length; i++)
		{
			returnArray[i] = coordinates[i];
		}
		returnArray[5] = color;
		return returnArray;
	}
}