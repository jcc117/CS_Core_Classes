#include <stdio.h>
#include <stdlib.h>

typedef struct Node
{
	struct Node* next;
	int value;
}Node;

Node* create_node(int value)
{
	Node* node = malloc(sizeof(Node));
	node->value = value;
	node->next = NULL;
	return node;
}

void print_list(Node* n)
{
	if(n->next == NULL)
		printf("%d\n", n->value);
	else
	{
		printf("%d\n", n->value);
		print_list(n->next);
	}
}

void free_list(Node* n)
{
	if(n == NULL)
	{
		//Do nothing
	}
	else if(n->next == NULL)
	{
		free(n);
	}
	else
	{
		free_list(n->next);
		free(n);
	}
}

int main()
{
	//Node* new_node = create_node(500);
	//printf("value = %d, next = %p\n", new_node->value, new_node->next);
	//Node* a = create_node(10);
	//Node* b = create_node(20);
	//Node* c = create_node(30);
	//a->next = b;
	//b->next = c;
	//print_list(a);
	//free_list(a);
	
	Node* a;
	char line[20];
	printf("Enter a number: ");
	fgets(line, sizeof(line), stdin);
	int input;
	sscanf(line, "%d", &input);
	int index = 0;
	if(input != -1)
	{
		Node* array[10000];
		a = create_node(input);
		array[index] = a;
		printf("Enter a number: ");
		fgets(line, sizeof(line), stdin);
		sscanf(line, "%d", &input);
		while(input != -1)
		{
			Node *b = create_node(input);
			b->next = array[index];
			index++;
			array[index] = b;
			a = b;
			printf("Enter a number: ");
			fgets(line, sizeof(line), stdin);
			sscanf(line, "%d", &input);
		}
		
		print_list(a);
		free_list(a);
	}
	
	return 0;
}
