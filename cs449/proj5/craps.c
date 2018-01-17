//Jordan Carr
//jcc117

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

void chomp(char* string);
int main()
{
	//Print welcome message and ask for user's name
	printf("Welcome to Jordan's Casino! Please enter your name: ");
	char buf[200];
	fgets(buf, sizeof(buf), stdin);
	//Remove newline character
	chomp(buf);
	
	//Ask the player to play or quit
	printf("%s, would you like to Play or Quit? ", buf);
	int invalid = 1;
	int play = 0;
	
	//Check for invalid input
	while(invalid)
	{
		char buf2[200];
		fgets(buf2, sizeof(buf2), stdin);
		char* play_string = "play";
		char* quit = "quit";
		chomp(buf2);
		
		//Check for play
		if(strcmp(play_string, buf2) == 0)
		{
			invalid = 0;
			play = 1;
		}
		//Check for quit
		else if(strcmp(quit, buf2) == 0)
		{
			invalid = 0;
		}
		//Print error message
		else
		{
			printf("Invalid Input\n");
			printf("%s, would you like to Play or Quit? ", buf);
		}
	}
	//Play the game
	//Open the kernel module
	int file = open("/dev/dice", O_RDONLY);
	//Check if opened correctly
	if(file < 0)
	{
		printf("Error: /dev/dice failed to open\n");
		return -1;
	}
	while(play)
	{
		//Get random numbers
		int num1;
		int num2;
		unsigned char numbuf[2];
		int val = read(file, numbuf, sizeof(numbuf));
		if(val < 1)
		{
			printf("Error: could not read\n");
			return -1;
		}
		num1 = numbuf[0] + 1;
		num2 = numbuf[1] + 1;
		
		//Print what the user rolled
		int total = num1 + num2;
		printf("You rolled %d + %d = %d\n", num1, num2, total);
		
		//Print win message if total is 7 or 11
		if(total == 7 || total == 11)
		{
			printf("You Win!\n");
		}
		//Print lose message if total is 2, 3, or 12
		else if(total == 2 || total == 3 || total == 12)
		{
			printf("Sorry, you lose.\n");
		}
		//Roll again
		else
		{
			printf("Roll Again\n");
			int total2 = 0;
			int num3;
			int num4;
			unsigned char numbuf2[2];
			//Roll until player gets 7 or the previous total
			while(total2 != 7 && total2 != total)
			{
				int val2 = read(file, numbuf2, sizeof(numbuf2));
				if(val2 < 0)
				{
					printf("Error: could not read\n");
					return -1;
				}
				
				num3 = numbuf2[0] + 1;
				num4 = numbuf2[1] + 1;
				total2 = num3 + num4;
			}
			//If 7, print lose message
			if(total2 == 7)
			{
				printf("Sorry, you lose.\n");
			}
			//If equal to the first roll, print win message
			else
			{
				printf("You Win!\n");
			}
		}
		
		int invalid2 = 1;
		//Check for invalid input when asking to play again
		while(invalid2)
		{
			//Ask the user to play again
			printf("Would you like to play again? ");
			char* yes = "yes";
			char* no = "no";
			char buf3[200];
			fgets(buf3, sizeof(buf3), stdin);
			chomp(buf3);
			//Check if yes
			if(strcmp(buf3, yes) == 0)
			{
				invalid2 = 0;
			}
			//Check if no
			else if(strcmp(buf3, no) == 0)
			{
				play = 0;
				invalid2 = 0;
			}
			//Print error message
			else
			{
				printf("Invalid Input\n");
			}
		}
	}
	//Close the kernel module
	close(file);
	//Print exit messsage
	printf("Goodbye, %s!\n", buf);
	return 0;

}
//Remove newline character from input string
void chomp(char* string)
{
	int looping = 1;
	int i = 0;
	while(looping)
	{
		if(string[i] == '\n')
		{
			string[i] = '\0';
			looping = 0;
		}
		i++;
	}
}