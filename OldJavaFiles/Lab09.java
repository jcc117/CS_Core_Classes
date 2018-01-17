// Jordan Carr
// Lab 9
// 3/15/16

import java.util.Scanner;
public class Lab09
{
	public static void main(String[] args)
	{
		//Create input variable
		Scanner keyboard = new Scanner(System.in);
		//Create variable to determine whether playing or not
		boolean stillPlaying = true;
		World world = new World();
		Scientist scientist = new Scientist(world);
		while(stillPlaying)
		{
			//Prompt user for the number of dimensions
			System.out.print("Enter dimension to travel(1,2,3,4,5)(negative to quit) >");
			int dimension = 0;
			
			//Error check to make sure input type is correct
			boolean goodInput = false;
			while(!goodInput)
			{
				//Check to make sure variable is an int
				if(keyboard.hasNextInt())
				{
					dimension = keyboard.nextInt();
					//Check whether input is a valid dimension or greater than zero
					//A negative number will exit the program
					if(dimension > 0)
					{
						if(dimension != 1 && dimension != 2 && dimension != 3 && dimension != 4 && dimension != 5)
						{
							System.out.print("Invalid Input. Try again. >");
						}
						
						else
						{
							goodInput = true;
						}
					}
					else
					{
						stillPlaying = false;
						goodInput = true;
					}
				}
				//If input is not an int
				else if(!keyboard.hasNextInt())
				{
					System.out.print("Invalid Input. Try again. >");
					keyboard.nextLine();
				}
			}
			
			//If user did not enter a negative number and exit the system
			if(stillPlaying == true)
			{
				goodInput = false;
				//Prompt user to enter number of units to travel
				System.out.print("Enter units to travel (negative for backwards) > ");
				int numUnits = 0;
				
				//Error check to make sure input is an int
				while(!goodInput)
				{
					if(keyboard.hasNextInt())
					{
						numUnits = keyboard.nextInt();
						goodInput = true;
					}
				
					else if(!keyboard.hasNextInt())
					{
						System.out.print("Invalid Input. Try again. >");
						keyboard.nextLine();
					}
				}
				//Move the user to the specified coordinates
				int[] coordinates = scientist.move(dimension, numUnits);
				//Display result to the user
				System.out.println("Location: [" + coordinates[0] + " " + coordinates[1] + " " + coordinates[2] + " " +coordinates[3] + " " + coordinates[4] + "] = " + world.getColor(coordinates[5]));
			}
		}
	}
}