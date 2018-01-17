//Jordan Carr
// This program allows the user to guess a word from a list of words in a separate text file
// 11/19/15
// 12/2/15

import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class P3A2_CARR_3918861
{
	public static void main(String args[]) throws IOException
	{
		//Welcome message
		System.out.println("Welcome to the Rainbow Explosion game!");
		System.out.println("Try to guess the secret word, one letter at a time.");
		
		//Open file to the list of secret words
		File myFile = new File("P3A2_CARR_3918861.txt");
		Scanner inputFile = new Scanner(myFile);
		
		//Create an array to store the words
		ArrayList<String> wordList = new ArrayList<String>();
		
		//Loop through the file and assign a word to the array until the end has been reached
		while(inputFile.hasNext())
		{
			//Assign a word to the array
			wordList.add(inputFile.nextLine());
		}

		//Create variables to store values whether to continue playing or not
		String answerInput;
		char answer;
		
		//Loop through the game
		do
		{
			//Generate random number to select a random word from the wordList array
			Random selection = new Random();
		
			int selection1 = selection.nextInt(wordList.size());
		
			//Store selected word under a string variable
			String secretWord = wordList.get(selection1);
					
			//Create an array to store the correct letters
			char[] guessedLetters = new char[secretWord.length()];
	
			//Store underscores as blanks in the array
			for(int i = 0; i < guessedLetters.length; i++)
			{
				guessedLetters[i] = '_';
			}
			
			//Separate secret word down into its letters and store each one in an index of an array
			char[] secretArray = new char[secretWord.length()];
			for(int j = 0; j < secretArray.length; j++)
			{
				char storingVariable = secretWord.charAt(j);
				secretArray[j] = storingVariable;
			}	
		
			//Create variable to store input data from keyboard
			Scanner keyboard = new Scanner(System.in);
	
			//Create variable to keep track of wrong guesses
			int ticker = 0;

			//Loops until all correct letters have been guessed
			boolean arraysEqual;
			do
			{
				
				//Print the total guessed letters to the user
				for(int k = 0; k < guessedLetters.length; k++)
				{
					System.out.print(guessedLetters[k]);
				}
				System.out.print("\n");				
				
				//Prompt user to enter a letter
				System.out.println("Enter one letter.");
				String guess = keyboard.nextLine();

				//Error check to make sure only 1 character was entered
				while(guess.length() != 1)
				{
					System.out.println("You did not enter just one letter.");
					System.out.println("Enter one letter.");
					guess = keyboard.nextLine();
				}
				
				//Test whether the input matches a given letter
				char charGuess = guess.charAt(0);
				boolean correctGuess = false;
				for(int k = 0; k < secretArray.length; k++)
				{
					if(charGuess == secretArray[k])
					{
						guessedLetters[k] = charGuess;
						correctGuess = true;
					}
						
				}
				
				//Tell user if the correct input has been entered
				if(correctGuess)
					System.out.println("That was right!");
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
			
				//Determine if the entire word has been entered
				int index = 0;
				arraysEqual = true;
				while(arraysEqual && index < secretArray.length)
				{
					if(secretArray[index] != guessedLetters[index])
						arraysEqual = false;
					index++;
				}
				
			}while(arraysEqual == false && ticker < 6);
		
			//Display win or lose message
			if(arraysEqual)
				System.out.println("You win! The secret word is " + secretWord);
			else
			{
				System.out.println("BOOM!");
				System.out.println("Game Over");
			}

			//Ask the user if he wants to play again
			System.out.println("Would you like to play again? Enter \"y\" for yes or \"n\" for no.");
			answerInput = keyboard.nextLine();
			answer = answerInput.charAt(0);
			
			//Error check for input
			while(answer != 'y' && answer != 'Y' && answer != 'n' && answer != 'N')
			{
				System.out.println("Incorrect input. Try again.");
				System.out.println("Would you like to play again? Enter \"y\" for yes or \"n\" for no.");
				answerInput = keyboard.nextLine();
				answer = answerInput.charAt(0);
			}
		}while(answer == 'y' || answer == 'Y');
	}
}