#include <stdio.h>

int main()
{
	printf("Please enter the weight you'd like to convert: ");
	char line[10];
	fgets(line, sizeof(line), stdin);
	int weight;
	sscanf(line, "%d", &weight);
	
	int mercWeight = weight * 0.38;
	int venWeight = weight * 0.91;
	int marWeight = weight * 0.38;
	int jupWeight = weight * 2.54;
	int satWeight = weight * 1.08;
	int uraWeight = weight * 0.91;
	int nepWeight = weight * 1.19;
	
	printf("\nHere is your weight on other planets:\n\n");
	printf("Mercury\t %d lbs\n", mercWeight);
	printf("Venus\t %d lbs\n", venWeight);
	printf("Mars\t %d lbs\n", marWeight);
	printf("Jupiter\t %d lbs\n", jupWeight);
	printf("Saturn\t %d lbs\n", satWeight);
	printf("Uranus\t %d lbs\n", uraWeight);
	printf("Neptune\t %d lbs\n", nepWeight);
}
