#include <stdio.h>
#include <stdlib.h>
#include <time.h>

const char rock[4] = {'r', 'o', 'c', 'k',};
const char paper[5] = {'p', 'a', 'p', 'e', 'r',};
const char scissors[8] = {'s', 'c', 'i', 's', 's', 'o', 'r', 's',};

/*Determines whether the user entered yes, no, or invalid input*/
int yesOrNO(char buffer[])
{
	/*Return 1 for yes, 0 for no, 5 for invalid input*/
	if(buffer[0] == 'y' && buffer[1] == 'e' && buffer[2] == 's' && buffer[3] == '\n')
	{
		return 1;
	}
	else if(buffer[0] == 'n' && buffer[1] == 'o' && buffer[2] == '\n')
	{
		return 0;
	}
	else
	{
		return 5;
	}
}

/*Determines whether the user entered rock, paper, scissors, or invalid input*/
int identifyString(char buffer[])
{
	int j;
	/*If answer is rock, return 0. If paper, return 1. If scissors, return 2. If invalid input, return 5.*/
	if(buffer[0] == 'r')
	{
		for(j = 0; j < 4; j ++)
		{
			if(rock[j] != buffer[j])
				return 5;
		}
		if(buffer[4] == '\n')
			return 0;
		else
			return 5;
	}
	else if(buffer[0] == 'p')
	{
		for(j = 0; j < 5; j ++)
		{
			if(paper[j] != buffer[j])
				return 5;
		}
		if(buffer[5] == '\n')
			return 1;
		else
			return 5;
	}
	else if(buffer[0] == 's')
	{
		for(j = 0; j < 8; j ++)
		{
			if(scissors[j] != buffer[j])
				return 5;
		}
		if(buffer[8] == '\n')
			return 2;
		else
			return 5;
	}
	else
	{
		return 5;
	}
}

/*Determines whether the user won, lost, or tied*/
int determineWin(int value, int answer)
{
	/*0 is rock, 1 is paper, 2 is scissors*/
	/*Return value: 1 is win, 0 is lose, 5 is tie*/
	
	/*Print what the computer chose*/
	if(value == 0)
		printf("The computer chose rock.\n");
	else if(value == 1)
		printf("The computer chose paper.\n");
	else
		printf("The computer chose scissors.\n");
	
	/*Return the outcome of the match*/
	if(value == answer)
	{
		return 5;
	}
	else if(value == 0 && answer == 1)
	{
		return 1;
	}
	else if(value == 0 && answer == 2)
	{
		return 0;
	}
	else if(value == 1 && answer == 0)
	{
		return 0;
	}
	else if(value == 1 && answer == 2)
	{
		return 1;
	}
	else if(value == 2 && answer == 0)
	{
		return 1;
	}
	else
	{
		return 0;
	}
}


int main()
{
	srand((unsigned int)time(NULL));
	int playerWins = 0;
	int compWins = 0;
	
	printf("Welcome to Rock, Paper, Scissors.\n\n");
	printf("Would you like to play? ");
	char buffer[20];
	fgets(buffer, sizeof(buffer), stdin);
	
	int flag = yesOrNO(buffer);
	
	/*Error check for answer*/
	while(flag == 5)
	{
		printf("Invalid input.\n");
		printf("Would you like to play? ");
		fgets(buffer, sizeof(buffer), stdin);
		flag = yesOrNO(buffer);
	}
	
	/*Cycle through the game*/
	while(flag == 1)
	{
		playerWins = 0;
		compWins = 0;
		/*Cycle through the match*/
		while ((playerWins < 3) && (compWins < 3))
		{
			char buffer2[20];
			printf("What is your choice? ");
			fgets(buffer2, sizeof(buffer2), stdin);
			int value = rand() % (2 - 0 + 1) + 0;
			
			/*Identify answer*/
			int answer = 5;
			answer = identifyString(buffer2);
			
			while(answer == 5)
			{
				printf("Invalid Input.\n");
				printf("What is your choice? ");
				fgets(buffer2, sizeof(buffer2), stdin);
				answer = identifyString(buffer2);
			}
			
			int win = determineWin(value, answer);
			
			/*Update player scores*/
			if(win == 1)
			{
				printf("You win the round!\n");
				playerWins++;
			}
			else if(win == 0)
			{
				printf("You lose the round.\n");
				compWins++;
			}
			else
			{
				printf("The game is a tie and does not count.\n");
			}
			printf("The score is now you: %d computer: %d\n\n", playerWins, compWins);
		}
		
		if(playerWins == 3)
			printf("You won the match!\n");
		else
			printf("You lost the match.\n\n");
		
		printf("Would you like to play again? ");
		fgets(buffer, sizeof(buffer), stdin);
	
		flag = yesOrNO(buffer);
	
		/*Error check for answer*/
		while(flag == 5)
		{
			printf("Invalid input.\n");
			printf("Would you like to play? ");
			fgets(buffer, sizeof(buffer), stdin);
			flag = yesOrNO(buffer);
		}
	}
	
	return 0;
	
}
