#include <stdio.h>
#include <string.h>

void str_reverse(char* dest, const char* source)
{
	int length = strlen(source);
	const char* p = &source[length - 1];
	
	int increment = 0;
	for(p; p >= source; p--)
	{
		dest[increment] = *p;
		increment++;
	}
	dest[increment] = '\0';
}
int main()
{
	const char* strArray[] = {"pullup", "plop", "racecar", "racecat", "rats", "deleveled"};
	int i;
	for(i = 0; i < 6; i++)
	{
		char buff[20];
		str_reverse(buff, strArray[i]);
		
		char output[200];
		if(strcmp(buff, strArray[i]) == 0)
		{
			snprintf(output, sizeof(output), "\"%s\" backwards is \"%s\", and it is a palindrome!", strArray[i], buff);
		}
		else
		{
			snprintf(output, sizeof(output), "\"%s\" backwards is \"%s\", and it is not a palindrome!", strArray[i], buff);
		}
		puts(output);
	}
}
