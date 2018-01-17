#include <stdio.h>

int main(int argc, char* argv[])
{
	printf("%s\n", argv[1]);
	char buf[100];
	fgets(buf, sizeof(buf), stdin);
	printf("%s\n", buf);
	return 0;
}