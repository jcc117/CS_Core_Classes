//Jordan Carr
// This program allows the user to guess one predetermined secret word
// 11/17/15
// 12/4/15

import java.util.Scanner;

public class P3A1_CARR_3918861
{
	public static void main(String args[])
	{
		//Welcome message
		System.out.println("Welcome to the Rainbow Explosion game!");
		System.out.println("Try to guess the secret word, one letter at a time.");
		
		//Set predetermined secret word
		String secretWord = "wolf";
		
		//Create an array to store the correct letters
		String[] guessedLetters = {"_", "_", "_", "_"};
		
		//Create a string to concatenate the final guess
		String totalGuess;

		//Create variable to store input data from keyboard
		Scanner keyboard = new Scanner(System.in);
		
		//Variable to keep track of what the user has gotten wrong
		int ticker = 0;

		//Loops until all correct letters have been guessed
		do
		{
			//Concatenates the results of the guesses to form the secret word
			totalGuess =  guessedLetters[0] + guessedLetters[1] + guessedLetters[2] + guessedLetters[3];
			System.out.println(totalGuess);			

			//Prompt the user to enter a letter
			System.out.println("Enter one letter.");
			String guess = keyboard.nextLine();

			//Error check to make sure only 1 character was entered
			while(guess.length() != 1)
			{
				System.out.println("You did not enter just one letter.");
				System.out.println("Enter one letter.");
				guess = keyboard.nextLine();
			}
			
			//Stores "w" in array if w was guessed
			if(guess.equalsIgnoreCase("w"))
			{
				guessedLetters[0] = "w";
				System.out.println("That was correct.");
			}
			
			//Stores "o" in array if o was guessed
			else if(guess.equalsIgnoreCase("o"))
			{
				guessedLetters[1] = "o";
				System.out.println("That was correct.");
			}
			
			//Stores "l" in array if l was guessed
			else if(guess.equalsIgnoreCase("l"))
			{
				guessedLetters[2] = "l";
				System.out.println("That was correct.");
			}
			
			//Stores "f" in array if f was guessed
			else if(guess.equalsIgnoreCase("f"))
			{
				guessedLetters[3] = "f";
				System.out.println("That was correct.");
			}
			
			//If guess is wrong
			else
			{
				ticker++;
				switch(ticker)
				{
					case 1:
						System.out.println("RED");
						break;
					case 2:
						System.out.println("ORANGE");
						break;
					case 3: 
						System.out.println("YELLOW");
						break;
					case 4: 
						System.out.println("GREEN");
						break;
					case 5:
						System.out.println("BLUE");
						break;
					case 6:
						System.out.println("PURPLE");
						break;
				}
			}

			//Concatenates the results of the guesses to form the secret word
			totalGuess =  guessedLetters[0] + guessedLetters[1] + guessedLetters[2] + guessedLetters[3];
			System.out.println(totalGuess);

		}while(!secretWord.equals(totalGuess) && ticker < 6);
		
		//Display win or lose messages
		if(ticker < 6)
			System.out.println("You win! The secret word is " + secretWord);
		else
		{
			System.out.println("BOOM!");
			System.out.println("Game Over");
		}
	}
}