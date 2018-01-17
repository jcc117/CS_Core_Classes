#include <stdio.h>
/* 	
	Jordan Carr
	jcc117
*/

int main(int argc, char** argv)
{
	//Open file
	FILE* f = fopen(argv[1], "rb");
	char array[5];
	int readableCount = 0;
	while(!feof(f))
	{
		char buffer[1];
		fread(&buffer, sizeof(char), 1, f);
		/*Read the first 4 characters in a given series*/
		if(((buffer[0] < 32) || (buffer[0] > 126)))
		{
			readableCount = 0;
		}
		else
		{
			array[readableCount] = buffer[0];
			readableCount = readableCount + 1;
		}
	
		/*Read additional characters after the first 4 readable ones*/
		if(readableCount == 4)
		{
			//Print the string of 4 chars
			array[4] = 0;
			printf("%s", array);
			char mini[2];
			while(!feof(f))
			{
				//Read a single char into a mini array and print single chars until
				//an unprintable one is found
				fread(&mini, sizeof(char), 1, f);
				if((mini[0] >= 32) && (mini[0] <= 126))
				{
					mini[1] = 0;
					printf("%s", mini);
				}
				else
				{
					readableCount = 0;
					printf("\n");
					break;
				}
			}
		}
	}
	//Close file
	fclose(f);
	return 0;
}
