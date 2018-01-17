// Jordan Carr
// Card Class, includes all informaiton regarding cards
// Created 2/7/16
// Updated 2/20/16

import java.util.ArrayList;

public class Card
{
  private String[] faceValue = new String[13];
  private String[] suit = new String[4];
  private ArrayList<String> hand = new ArrayList<String>();
  
  //Sets up arrays to hold face values of the cards and suit types
  public Card()
  {
	//Create array of possible card values
	faceValue[0] = "2";
	faceValue[1] = "3";
	faceValue[2] = "4";
	faceValue[3] = "5";
	faceValue[4] = "6";
	faceValue[5] = "7";
	faceValue[6] = "8";
	faceValue[7] = "9";
	faceValue[8] = "T";
	faceValue[9] = "J";
	faceValue[10] = "Q";
	faceValue[11] = "K";
	faceValue[12] = "A";
	
	//Create array of possible card suits
	suit[0] = "c";
	suit[1] = "s";
	suit[2] = "d";
	suit[3] = "h";
  }
  
  //Returns a random face value to the player
  public String getCardValue(int cardValue)
  {
	return faceValue[cardValue];
  }
  
  //Returns a random suit value to the card
  public String getSuit(int suitType)
  {
	 return suit[suitType];
  }
  
  public int getScore(int points, int numAces)
  {
	  int score = 0;

	// If there are no aces, or if score is less than 21 with aces at
	// 11 points each, then the actual score is just
	// equal to the number of points.
	
	if (numAces == 0 || points <= 21) 
	{
	    score = points;
	} 
	
	else 
	{

	    // Otherwise, we need to check what is the BEST score is,
	    // and that gets a little complicated.  We set a placeholder
	    // -1 for best score, and a placeholder potential score.
	    // We will keep track of what the best score is, and try
	    // different potential scores against it.  Whatever is
	    // highest without going over 21 will win as the best score.
	    
	    int bestScore = -1;
	    int potentialScore = points;

	    // Loop through _number of aces_ times.  Each time, try an
	    // increasing number of aces as a 1 value instead of an
	    // 11 value (thus, subtract 10 * j from the total points
	    // value, which assumes all Aces are equal to 11 points).
	    
	    for (int j = 0; j <= numAces; j++) {
		potentialScore = (points - (10 * j));

		// For each iteration, if the potential score is
		// better than the already-best score, but it is NOT
		// over 21 (causing us to bust), then the
		// potential score should count as our new best score.
		
		if (potentialScore > bestScore && potentialScore <= 21) {
		    bestScore = potentialScore;
		}
	    }

	    // We could have busted even when all of our aces were set
	    // to one point.  In this case, we might never have gotten a
	    // valid "best" score.  But our best potential score is the closest
	    // to a best score we have, so we will replace our placeholder -1
	    // best with the best potential score we got.

	    // Otherwise, just set the score to the best score.
	    
	    if (bestScore == -1) 
		{
		score = potentialScore;
	    } 
		else 
		{
		score = bestScore;
	    }
	}
	return score;
  }
  
  //Calculates the amount of points an individual card is worth
  public int getCardPoints(int cardValue)
  {
	//Assume if the card is an ace it is worth 11 points
	if (faceValue[cardValue].equals("A"))
	{
		return 11;
	}
	//Convert face cards and '10' cards to 10 points
	else if(faceValue[cardValue].equals("K") || faceValue[cardValue].equals("J") || faceValue[cardValue].equals("Q") || faceValue[cardValue].equals("T"))
	{
		return 10;
	}
	//Convert number cards to an int to get its point value
	else
	{
		return Integer.parseInt(faceValue[cardValue]);
	}
  }
  
  //Adds the cards to the player's deck and dislpays them
  public void toString(int cardValue, int suitType)
  {
	  //Add new card to the hand
	  hand.add(faceValue[cardValue] + suit[suitType]);
	  
	  System.out.print("Cards: ");
	  
	  //Loop through array list and display cards
	  for(int j = 0; j < hand.size(); j++)
	  {
		  System.out.print(hand.get(j) + " ");
	  }
	  
	  System.out.print("\n");
  }

}