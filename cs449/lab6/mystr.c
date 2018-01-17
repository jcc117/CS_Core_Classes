void my_strcpy(char *dest, char *src)
{
 while(*dest++ = *src++);
}

int my_strlen(char *src)
{
	int index = 0;
	int counter = 0;
	while(src[index] != 0)
	{
		counter++;
		index++;
	}
	
	return counter;
}