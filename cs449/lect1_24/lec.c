#include <stdio.h>
#include <string.h>

//DONT DO THIS SHIT
char* func()
{
	char str[10];
	strcpy(str, "Hi there");
	return str;
}
int main()
{
	char* str = func();
}