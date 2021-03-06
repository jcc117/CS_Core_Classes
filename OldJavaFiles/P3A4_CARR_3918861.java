//Jordan Carr
// This program uses object oriented programming to allow the user to guess secret words
// 12/7/15
// 12/8/15

import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class P3A4_CARR_3918861
{
	public static void main(String args[]) throws IOException
	{
		//Welcome message
		System.out.println("Welcome to the Rainbow Explosion game!");
		System.out.println("Try to guess the secret word, one letter at a time.");
		
		//Create an instance of the WordList class to store all of the possible words
		P3A4_CARR_3918861_WordList myWordList = new P3A4_CARR_3918861_WordList();

		//Prompt user to open a file for the secret words
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter a file of secret words.");
		String fileName = keyboard.nextLine();

		//Open file to the list of secret words
		File myFile = new File(fileName);

		//Make sure the file exists
		while(!myFile.exists())
		{
			System.out.println("That file does not exist. Please try again.");
			System.out.println("Enter a file of secret words.");
			fileName = keyboard.nextLine();
			myFile = new File(fileName);
		}
		
		Scanner inputFile = new Scanner(myFile);
		
		//Add words from the file to myWordList
		myWordList.addWordsFromFile(inputFile);
	
		inputFilej.close();

		//Create variables to store values whether to continue playing or not
		String answerInput;
		char answer;
		
		//Loop through the game
		do
		{	
			//Get a random word from the WordList class
			String secretWord = myWordList.getRandomWord();			

			//Create an array to store the correct letters
			char[] correctLetters = new char[secretWord.length()];
	
			//Store underscores as blanks in the array
			for(int i = 0; i < correctLetters.length; i++)
			{
				correctLetters[i] = '-';
			}
			
			//Separate secret word down into its letters and store each one in an index of an array
			char[] secretArray = new char[secretWord.length()];
			for(int j = 0; j < secretArray.length; j++)
			{
				char storingVariable = secretWord.charAt(j);
				secretArray[j] = storingVariable;
			}
	
			//Create array for storing the guessed letters, both right and wrong
			char[] guessedLetters = new char[26];

			//Store blank spaces in guessedLetters array
			for(int j = 0; j < guessedLetters.length; j++)
			{
				guessedLetters[j] = '-';
			}
			
			int guessedLettersIndex = 0;

			//Create variable to keep track of the wrong guesses
			int ticker = 0;

			//Loops until all correct letters have been guessed
			boolean arraysEqual = false;
			do
			{
				
				//Print the total guessed letters to the user
				for(int k = 0; k < correctLetters.length; k++)
				{
					System.out.print(correctLetters[k]);
				}
				System.out.print("\n");				
				
				//Prompt user to guess one letter or the entire word
				System.out.println("Would you like to guess just one letter or the entire word?");
				System.out.println("Enter 1 to guess just one letter or 2 to guess the entire word.");
				String optionSelect = keyboard.nextLine();

				//Error check to make sure only 1 or 2 was entered
				while(!optionSelect.equals("1") && !optionSelect.equals("2"))
				{
					System.out.println("Incorrect input. Please try again.");
					System.out.println("Enter 1 to guess just one letter or 2 to guess the entire word.");
					optionSelect = keyboard.nextLine();
				}
			
				if(optionSelect.equals("1"))
				{
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

					//Convert guess to a char value
					char charGuess = guess.charAt(0);
				
					//Test if the letter was already guessed
					boolean alreadyGuessed = false;
					for(int i = 0; i < guessedLetters.length; i++)
					{
						if(charGuess == guessedLetters[i])
						{
							alreadyGuessed = true;
							break;
						}
					}
				
					if(alreadyGuessed == false)
					{
						//Test whether the input matches a given letter
						boolean correctGuess = false;
						for(int k = 0; k < secretArray.length; k++)
						{
							if(charGuess == secretArray[k])
							{
							correctLetters[k] = charGuess;
							correctGuess = true;
							}
						
						}
				
						//Tell user if the correct input has been entered
						if(correctGuess)
							System.out.println("That was right!");
						else
						{	
							ticker++;
							System.out.println(rainbowTicker(ticker));
						}
			
						//Add the letter to the guessedLetters array
						guessedLetters[guessedLettersIndex] = charGuess;
						guessedLettersIndex++;

						//Determine if the entire word has been entered
						int index = 0;
						arraysEqual = true;
						while(arraysEqual && index < secretArray.length)
						{
							if(secretArray[index] != correctLetters[index])
							arraysEqual = false;
							index++;
						}
					}

					else
					{
						System.out.println("You already entered that letter. Please try again.");
					}
				}
				
				else
				{
					//Prompt the user to guess the entire word
					System.out.println("Please enter what you think the word is.");
					String entireWordGuess = keyboard.nextLine();
					if(secretWord.equals(entireWordGuess))
						arraysEqual = true;
					else
					{
						ticker++;
						System.out.println(rainbowTicker(ticker));
					}
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
	
	public static String rainbowTicker(int wrongGuessCount)
	{
		//Create variable to store which color the player is on
		String returnStatement = "";
		
		//Decide which color the player is on
		switch(wrongGuessCount)
		{
			case 1:
				returnStatement = "RED";
				break;
			case 2:
				returnStatement = "ORANGE";
				break;
			case 3: 
				returnStatement = "YELLOW";
				break;
			case 4: 
				returnStatement = "GREEN";
				break;
			case 5:
				returnStatement = "BLUE";
				break;
			case 6:
				returnStatement = "PURPLE";
				break;
		}
		return returnStatement;
	}
}