// Jordan Carr
// Assignment 2 BlackJack Class
// Created 2/7/16
// Updated 2/10/16

import java.util.Scanner;
import java.io.*;

public class BlackJack
{
	public static void main(String[] args) throws IOException
	{
		// Create variable for storing user input
		Scanner keyboard = new Scanner(System.in);
		
		// Prompt the user to enter his/her name to create or load a file
		System.out.println("What is your name?");
		String name = keyboard.nextLine();
		
		//Open file if it exists or create a new one if it does not
		File file = new File(name + ".txt");
		if(!file.exists())
		{
			PrintWriter newFile = new PrintWriter(name + ".txt");
		}
		
		Player newPlayer = new Player(name);
		
		//Create a variable that tells whether the play still wants to play or not
		boolean stillPlaying = true;
		
		do
		{
			//Display the menu to the user
			System.out.println("Name: " + newPlayer.getName());
			System.out.println("Total Hands: " + newPlayer.getTotalHands());
			System.out.println("Hands Won: " + newPlayer.getHandsWon());
			System.out.println("Money: " + newPlayer.getMoney());
			
			System.exit(0);
		
			//Ask the user to play a hand or not
			//Y starts the game
			//N quits the game
			System.out.println("\nPlay a hand? (Y/N)");
			String response = keyboard.nextLine();
		
			//Insert error check here
		
			if(response.equals("Y"))
			{
				//Ask the user how much he/she wishes to bet
				System.out.print("Enter amount to bet > ");
				double bettings = keyboard.nextInt();
		
				//Insert prompt here showing new cards to the player
		
				System.out.print("[H]it or [S]tay? > ");
				String anotherCardResponse = keyboard.nextLine();
			
				//Insert error check here
			}
			
			else
			{
				stillPlaying = false;
			}
		}while(stillPlaying);
	}
}