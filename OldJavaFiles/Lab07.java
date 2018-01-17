// Jordan Carr
// Lab 7
// 2/23/16

import java.util.Scanner;
import java.io.*;

public class Lab07
{
	public static void main(String[] args) throws IOException
	{
		//Holds entered values
		Scanner keyboard = new Scanner(System.in);
	
		//Prompt user to enter size of world
		System.out.print("How big of a world? >");
	
		//Holds size of world
		int size = 0;
	
		//Value to ensure value entered is valid
		boolean goodInput = false;
	
		//Error check for valid input
		while(!goodInput)
		{
			//If value entered is an int
			if(keyboard.hasNextInt())
			{
				size = keyboard.nextInt();
			
				//If value entered less than or equal to 0
				if(size <= 0)
				{
					System.out.println("Invalid Input.");
					System.out.print("How big of a world? >");
				}
			
				//If value entered is valid
				else
				{
					goodInput = true;
				}
			}
		
			//If value entered is not an int
			else if (!keyboard.hasNextInt())
			{
				System.out.println("Invalid Input.");
				System.out.print("How big of a world? >");
				keyboard.nextLine();
			}
		}
		
		//Generate universe
		char[] universe = generateUniverse(size);
		
		//Display universe
		displayUniverse(universe);
		
		//Hold's user response
		String response = "A";
		
		//Clear keyboard buffer
		keyboard.nextLine();
		
		//User advances
		while(response.equalsIgnoreCase("A"))
		{
			System.out.print("A, Q, or S? >");
			response = keyboard.nextLine();
			
			//Error check for invalid input
			while(!response.equalsIgnoreCase("A") && !response.equalsIgnoreCase("S") && !response.equalsIgnoreCase("Q"))
			{
				System.out.println("Invalid Input.");
				System.out.print("A, Q, or S? >");
				response = keyboard.nextLine();
			}
			
			//Advance universe
			if(response.equalsIgnoreCase("A"))
			{
				advanceUniverse(universe);
				displayUniverse(universe);
				System.out.print("\n");
			}
		}
		
		//Save file
		if(response.equalsIgnoreCase("S"))
		{
			PrintWriter file = new PrintWriter("universe.txt");
			String universeString = new String(universe);
			int babies = 0;
			int children = 0;
			int adults = 0;
			
			//Count babies
			for(int j = 0; j < universe.length; j++)
			{
				if(universe[j] == '0')
				{
					babies += 1;
				}
			}
			
			//Count children
			for(int j = 0; j < universe.length; j++)
			{
				if(universe[j] == '1')
				{
					children += 1;
				}
			}
			
			//Count adults
			for(int j = 0; j < universe.length; j++)
			{
				if(universe[j] == '2')
				{
					adults += 1;
				}
			}
			
			//Save the file
			file.println(universeString);
			file.println("Babies: " + babies);
			file.println("Children: " + children);
			file.println("Adutls: " + adults);
			file.close();
		}
	}
	
	//Create universe
	public static char[] generateUniverse(int size)
	{
		char[] universePlaceHolder = new char[size];
		
		for(int j = 0; j < size; j++)
		{
			if((j % 7) == 0)
			{
				universePlaceHolder[j] = '0';
			}
			
			else if((j % 5) == 0)
			{
				universePlaceHolder[j] = '^';
			}
			
			else
			{
				universePlaceHolder[j] = '.';
			}
		}
		
		return universePlaceHolder;
	}
	
	//Display universe
	public static void displayUniverse(char[] universe)
	{
		for(int j = 0; j < universe.length; j++)
		{
			System.out.print(universe[j]);
		}
		
		System.out.print("\n");
	}
	
	//Advance universe
	public static char[] advanceUniverse(char[] universe)
	{
		for(int j = 0; j < universe.length; j ++)
		{
			//Advance 0 to 1
			if(universe[j] == '0')
			{
				universe[j] = '1';
			}
			
			//Advance 1 to 2
			else if(universe[j] == '1')
			{
				universe[j] = '2';
			}
			
			else if(!(j == (universe.length - 1)))
			{
				//Advance 2 if next char is '.'
				if(universe[j] == '2' && universe[j + 1] == '.')
				{
					j += 1;
					universe[j] = '2';
					universe[j - 1] = '.';
				}
			
				//Make 2 into a 0 again if next character is a ^
				else if(universe[j] == '2' && universe[j + 1] == '^')
				{
					j += 1;
					universe[j] = '0';
					universe[j - 1] = '.';
				}
			}
			
			//If j is at the last place in the universe, ensures program won't crash
			else if(j == ((universe.length) - 1))
			{
				if(universe[j - 1] == '2' && universe[j] == '.')
				{
					universe[j] = '2';
					universe[j -1] = '.';
				}
				
				else if(universe[j - 1] == '2' && universe[j] == '2')
				{
					universe[j] = '2';
					universe[j -1] = '2';
				}
			}
		}
		
		return universe;
	}
}