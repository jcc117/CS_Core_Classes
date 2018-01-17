#include <stdio.h>
#include <stdlib.h>
#include "linux-2.6.23.1/include/asm/unistd.h"
#include <sys/types.h>
#include <sys/mman.h>
#include <linux/sched.h>

//Try including the extended address for the unistd.h file to solve compilation errors

//Jordan Carr
//cs1550
//Assignment 2

struct Node
{
	struct task_struct* item;
	struct Node* next;
};
typedef struct
{
	int value;
	struct Node* head;		//process queue
	struct Node* rear;
	struct Node* temp;		//for enqueueing processes so they don't have to have a place in local memory
}cs1550_sem;


//Wrapper functions for the up and down syscalls
void down(cs1550_sem *sem)
{
	syscall(__NR_cs1550_down, sem);
}
void up(cs1550_sem *sem)
{
	syscall(__NR_cs1550_up, sem);
}

int main(int argc, char *argv[])
{
	//Get number of produces, consumers, and buffer size
	if(argc != 4)
	{
		printf("Error: wrong number of arguments\n");
		exit(-1);
	}
	//Make another error check for argument types
	
	int chefs = atoi(argv[1]);
	int customers = atoi(argv[2]);
	int buff_size = atoi(argv[3]);
	
	int buffer[buff_size];
	int j;
	for(j = 0; j < buff_size; j++)
	{
		buffer[j] = 0;
	}
	//Create the shared memory space for the processes
	//printf("Setting up shared memory\n");
	void *ptr = mmap(NULL, sizeof(int[buff_size]) + sizeof(cs1550_sem)*3 + sizeof(int)*2, PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
	
	//Set up all semaphores and other shared data
	//Place semaphores in the shared memory
	//printf("Delcare sems\n");
	cs1550_sem* empty = ptr;	//p1
	cs1550_sem* full = empty + sizeof(empty);	//p2
	cs1550_sem* mutex = full + sizeof(full);	//p3
	
	//printf("Set values\n");
	empty->value = buff_size;
	full->value = 0;
	mutex->value = 1;
	
	//printf("Set heads to null\n");
	empty->head = NULL;
	full->head = NULL;
	mutex->head = NULL;
	
	//printf("Set rears to null \n");
	empty->rear = NULL;
	full->rear = NULL;
	mutex->rear = NULL;
	
	
	//Set up temporary nodes as a place holder for the queue
	//printf("Set up temorary nodes info\n");
	empty->temp = NULL;
	full->temp = NULL;
	mutex->temp = NULL;
	
	//printf("Set up and and out variables\n");
	//Place in and out in the shared memory
	int* in = ptr + sizeof(cs1550_sem)*3;
	int* out = in + 1;
	*in = 0;
	*out = 0;
	
	//printf("Set up the buffer\n");
	//Declare the buffer and initialize everything to 0
	int** p4 = ptr + sizeof(cs1550_sem)*3 + sizeof(int)*2;
	
	//Create all producer processes
	int i;
	int pid;
	for(i = 0; i < chefs; i++)
	{
		pid = fork();
		//Please do not fork bomb
		if (pid == 0)
		{
			printf("Producer%d\n", i);
			break;
		}
	}
	
	if(pid == 0)
	{
		//Produce some pancakes
		while(1)
		{
			down(empty);		//down empty
			down(mutex);		//down mutex
				buffer[*in] = 1;
				*in = (*in + 1) % buff_size;
				printf("Chef%d Produced: Pancake%d\n", i, *in);
			up(mutex);		//up mutex
			up(full);		//up full
		}
		printf("hello\n");
		exit(0);
	}
	
	else
	{
		int pid2;
		//Create all consumer processes
		for(i = 0; i < customers; i++)
		{
			pid2 = fork();
			//Please do not fork bomb
			if(pid2 == 0)
			{
				printf("Consumer%d\n", i);
				break;
			}
		}
		
		int holder;
		if(pid2 == 0)
		{
			//Consume some pancakes
			while(1)
			{
				down(full);		//down full
				down(mutex);		//down mutex
					holder = buffer[*out];
					*out = (*out + 1) % buff_size;
					printf("Customer%d Consumed: Pancake%d\n", i, *out);
				up(mutex);		//up mutex
				up(empty);		//up empty
			}
			printf("hello2\n");
			exit(0);
		}
	}
	
	return 0;
}
