#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

int main()
{
	int file = open("/dev/dice", O_RDONLY);
	if(file < 0)
	{
		perror("Open Error");
		return -1;
	}
	printf("How many nums? ");
	char buffer[200];
	fgets(buffer, sizeof(buffer), stdin);
	int num = atoi(buffer);
	char numbuf[num];
	int val = read(file, numbuf, sizeof(numbuf));
	if(val < 0)
	{
		perror("Read Error");
		return -1;
	}
	int i;
	for(i = 0; i < num; i++)
		printf("NUM %d: %d\n", i, numbuf[i]);
	close(file);
	return 0;
}