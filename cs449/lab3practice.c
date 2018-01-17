#include <stdio.h>
#include <string.h>

int main()
{
	char str[] = "string!";
	int len = strlen(str);
	char* end = str + len;
	
	char* p;
	for(p = str; p < end; p++)
		printf("%s\n", p);
}
