// Jordan Carr
// Assignment 2 BlackJack Class
// Created 2/7/16
// Updated 2/20/16

import java.util.Scanner;
import java.io.*;
import java.util.Random;

public class Blackjack
{
	public static void main(String[] args) throws IOException
	{
		// Create variable for storing user input
		Scanner keyboard = new Scanner(System.in);
		
		//Create variable for storing random numbers
		Random numbers = new Random();
		
		// Prompt the user to enter his/her name to create or load a file
		System.out.println("What is your name?");
		String name = keyboard.nextLine();
		
		//Create an object for the new player
		Player newPlayer;
		
		//Open file if it exists or create a new one if it does not
		File file = new File(name + ".txt");
		//User's file does not already exist
		if(!file.exists())
		{
			PrintWriter newFile = new PrintWriter(name + ".txt");
			newPlayer = new Player(name);
			
			//Set defualt data to the user's new file
			newFile.println(0);		//User's total hands
			newFile.println(0);		//User's hands won
			newFile.println(100.0);	//User's money
			newFile.close();
			
			//Welcome message
			System.out.println("Welcome to Infinite Blackjack!");
		}
		
		//User's file already exists
		else
		{
			//Open the file and create a new player from it
			Scanner inputFile = new Scanner(file);
			int totalHands = inputFile.nextInt();
			int handsWon = inputFile.nextInt();
			double money = inputFile.nextDouble();
			newPlayer = new Player(name, totalHands, handsWon, money);
			inputFile.close();
			
			//Welcome message
			System.out.println("Welcome Back!");
		}
		
		//Create a variable that tells whether the play still wants to play or not
		boolean stillPlaying = true;
		
		do
		{
			//Display the menu to the user
			System.out.printf("Name: %25s\n", newPlayer.getName());
			System.out.printf("Total Hands: %18d\n", newPlayer.getTotalHands());
			System.out.printf("Hands Won: %20d\n", newPlayer.getHandsWon());
			System.out.printf("Money: %,24.2f\n", newPlayer.getMoney());
		
			//Ask the user to play a hand or not
			//Y starts the game
			//N quits the game
			System.out.println("\nPlay a hand? (Y/N)");
			String response = keyboard.nextLine();
			
			//Error check to check for invalid input
			while(!response.equalsIgnoreCase("Y") && !response.equalsIgnoreCase("N") && !response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("no"))
			{
				System.out.println("Invalid Input.");
				response = keyboard.nextLine();
			}
			
			//Player selectes yes, the game begins
			if(response.equalsIgnoreCase("Y") || response.equalsIgnoreCase("yes"))
			{
				//Ask the user how much he/she wishes to bet
				System.out.print("Enter amount to bet > ");
				double bettings = 0.0;
				boolean goodInput = false;
				
				//Error check to make sure bettings entered is a valid value and not a string
				while(!goodInput)
				{
					if(keyboard.hasNextDouble())
					{
						bettings = keyboard.nextDouble();
						
						//Amount betted is more than what the player has
						if(bettings > newPlayer.getMoney())
						{	
							System.out.println("Thats too much money. Try again.");
							System.out.print("Enter amount to bet > ");
						}
					
						//Amount betted is less than the 1 cent
						else if(bettings < .01)
						{
							System.out.println("Thats not enough money. Try again.");
							System.out.print("Enter amount to bet > ");
						}
						
						//Amount betted is valid
						else
						{
							goodInput = true;
						}
					}
					//Value entered is not a double
					else if (!keyboard.hasNextDouble())
					{
						System.out.println("Invalid Input.");
						System.out.print("Enter amount to bet > ");
						keyboard.nextLine();
					}
				}
			
				//Set the player's points to 0 to start the round
				int possiblePoints = 0;
				
				//Set the player's possible score to zero
				//This takes into account the possibilities of having an ace
				int score = 0;
				
				//Set the number of aces the player has to zero
				int numAces = 0;
				
				//Set the number of face cards the player has to zero
				//This is meant for determining whether the player wins with a blackjack or
				//by winning by just having 21 points
				int numFaceCards = 0;
					
				//Create the deck of cards
				Card playerDeck = new Card();
				
				//Clear keyboard buffer
				keyboard.nextLine();
				
				//Create variable to determine whether the player is hitting or not
				String anotherCardResponse = "H";
				while(anotherCardResponse.equalsIgnoreCase("H") || anotherCardResponse.equalsIgnoreCase("Hit"))
				{	
					//Draw a card for the player
					//Get a random card value
					int cardVal = numbers.nextInt(13);
					//Get a random suit
					int suit = numbers.nextInt(4);
					System.out.println("Your card is " + playerDeck.getCardValue(cardVal) + playerDeck.getSuit(suit));

					//Add 1 to the amount of aces if ace card is drawn
					if(playerDeck.getCardValue(cardVal).equals("A"))
					{
						numAces += 1;
					}
					
					//If face card or a 10, add 1 to its counter
					else if(playerDeck.getCardValue(cardVal).equals("K") || playerDeck.getCardValue(cardVal).equals("J") || playerDeck.getCardValue(cardVal).equals("Q") || playerDeck.getCardValue(cardVal).equals("T"))
					{
						numFaceCards += 1;
					}
					
					//Display cards
					playerDeck.toString(cardVal, suit);
					
					//Convert number card to amount of possible points
					possiblePoints += playerDeck.getCardPoints(cardVal);
					
					//Calculate the number of points the player has
					score = playerDeck.getScore(possiblePoints, numAces);
					
					//Display score
					System.out.println("Your score: " + score);
					
					//Makes sure the amount of points the user has is still under 21
					//If not, the player loses
					if(score > 21)
					{
						break;
					}
					
					//Ask the user to hit or stay
					System.out.print("[H]it or [S]tay? > ");
					anotherCardResponse = keyboard.nextLine();
				
					//Error check to make sure player has valid input
					while(!anotherCardResponse.equalsIgnoreCase("H") && !anotherCardResponse.equalsIgnoreCase("S") && !anotherCardResponse.equalsIgnoreCase("Hit")&& !anotherCardResponse.equalsIgnoreCase("Stay")) 
					{
						System.out.println("Invalid input.");
						anotherCardResponse = keyboard.nextLine();
					}
				}		
				//Determine the winnings or losings for the player
				//Player has 21 points and wins
				if(score == 21)
				{
					//Player wins with a blackjack
					if(numAces == 1 && numFaceCards == 1)
					{
						System.out.println("Blackjack");
						newPlayer.addTotalHands();
						newPlayer.addHandsWon();
						//Fix the bettings
						bettings *= 1.5;
						System.out.printf("You win $%,.2f\n", bettings);
						newPlayer.fixBettings(bettings);
					}
					//Player just has 21 points
					else
					{
						System.out.println("Player wins");
						newPlayer.addTotalHands();
						newPlayer.addHandsWon();
						System.out.printf("You win $%,.2f\n", bettings);
						//Fix the bettings
						newPlayer.fixBettings(bettings);
					}
				}
				
				//Player has over 21 points and loses
				else if(score > 21)
				{
					System.out.println("Player Busted");
					newPlayer.addTotalHands();
					System.out.printf("You lose $%,.2f\n", bettings);
					//Fix the bettings
					bettings *= -1;
					newPlayer.fixBettings(bettings);
				}
				
				//Player stays under 21 points and the dealer now plays
				else
				{
					//Create score and numAces for the dealer
					int dealerScore = 0;
					int dealerNumAces = 0;
					int dealerPossiblePoints = 0;
					Card dealerDeck = new Card();
					
					//Loop through the game for the dealer
					boolean dealerStillPlaying = true;
					while(dealerStillPlaying)
					{
						//Draw a card
						//Get a random card value
						int cardVal = numbers.nextInt(13);
						//Get a random suit
						int suit = numbers.nextInt(4);

						//Display dealer's card
						System.out.println("Dealer's card is " + dealerDeck.getCardValue(cardVal) + dealerDeck.getSuit(suit));
						
						//Add 1 to the amount of aces if ace card is draw
						if(dealerDeck.getCardValue(cardVal).equals("A"))
						{
							dealerNumAces += 1;
						}
						
						//Display cards
						dealerDeck.toString(cardVal, suit);
					
						//Convert cards to possible points
						dealerPossiblePoints += dealerDeck.getCardPoints(cardVal);
						
						//Calculate the number of points
						dealerScore = dealerDeck.getScore(dealerPossiblePoints, dealerNumAces);
				
						//Makes sure the amount of points the dealer has is still under 21
						//If not, the dealer loses
						if(dealerScore > 21)
						{
							dealerStillPlaying = false;
						}
						
						//Dealer stays if score is greater than or equal to 18 but less than or
						//equal to 21
						else if(dealerScore >= 18 && dealerScore <= 21)
						{
							dealerStillPlaying = false;
							System.out.println("Stay");
						}
						
						//Dealer stays if score is 17 and has no aces
						else if(dealerScore == 17 && dealerNumAces == 0)
						{
							dealerStillPlaying = false;
							System.out.println("Stay");
						}
						
						//Dealer hits again
						else
						{
							System.out.println("Hit");
						}
					}
					
					//Display both scores
					System.out.println("Dealer score:" + dealerScore);
					System.out.println("Your score: " + score);
					
					//House automatically wins if score is 21
					if(dealerScore == 21)
					{
						System.out.println("Dealer won");
						newPlayer.addTotalHands();
						System.out.printf("You lose $%,.2f\n", bettings);
						bettings *= -1;
						newPlayer.fixBettings(bettings);
					}
					
					//Dealer automatically loses if score is over 21
					else if(dealerScore > 21)
					{
						System.out.println("Dealer busted");
						newPlayer.addTotalHands();
						newPlayer.addHandsWon();
						System.out.printf("You win $%,.2f\n", bettings);
						newPlayer.fixBettings(bettings);
					}
					//Dealer wins if its score is greater than the players score and under 21
					else if(dealerScore > score)
					{
						System.out.println("Dealer won");
						newPlayer.addTotalHands();
						System.out.printf("You lose $%,.2f\n", bettings);
						bettings *= -1;
						newPlayer.fixBettings(bettings);
					}
					//Player neither wins nor loses if both scores are equal
					else if(score == dealerScore)
					{
						System.out.println("Push");
						newPlayer.addTotalHands();
					}
					//Player wins if its score is greater than the house score and under 21
					else
					{
						System.out.println("Player won");
						System.out.printf("You win $%,.2f\n", bettings);
						newPlayer.addTotalHands();
						newPlayer.addHandsWon();
						newPlayer.fixBettings(bettings);
					}
				}				
			}
			
			//Player selects no, ends program
			if (response.equalsIgnoreCase("N"))
			{
				stillPlaying = false;
			}
			
		} while(stillPlaying);
		
		//Save the player's data by overwriting his/her file
		PrintWriter savingFile = new PrintWriter(name + ".txt");
		savingFile.println(newPlayer.getTotalHands());
		savingFile.println(newPlayer.getHandsWon());
		savingFile.println(newPlayer.getMoney());
		savingFile.close();
		
	}
	
}