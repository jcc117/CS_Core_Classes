#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

//Jordan Carr
//jcc117
//CS1550
//Project 3

void print_stats(char* algo, int frames, int accesses, int faults, int writes);

typedef struct 
{
	unsigned int page_num;
	int dirty;
	int ref_bit;
	int valid; 
}PTE;

//A node for linked lists for implemenatation of the opt algorithm
struct OptNode
{
	unsigned int page;
	int location;
	struct OptNode *next;
	struct OptNode *prev;
};

//Initialize all the frames of a table to free
int init_frames(PTE frames[], int size)
{
	int i;
	for(i = 0; i < size; i++)
	{
		frames[i].page_num = 0;
		frames[i].dirty = 0;
		frames[i].ref_bit = 0;
		frames[i].valid = 0;
	}
}

//Find a free frame to put page table entry into
//Returns index if found, -1 if none are free
int find_free_frame(PTE frames[], int size)
{
	int i;
	for(i = 0; i < size; i++)
	{
		if(frames[i].valid == 0)
			return i;
	}
	return -1;
}

//Create a new page table entry
void create_pte(PTE *new, unsigned int page, char mode)
{
	//printf("start\n");
	new->page_num = page;
	new->ref_bit = 0;
	new->valid = 1;
	if(mode == 'W')
		new->dirty = 1;
	else
		new->dirty = 0;
	//printf("Created\n");
}

//Find a specific page in the page table
//Return index if found, -1 if not found
int find_page(PTE frames[], int size, unsigned int page)
{
	int i;
	for(i = 0; i < size; i++)
	{
		if(frames[i].page_num == page)
			return i;
	}
	return -1;
}

//==================================================================================================================================================
//Optmial Replacemet Algorithm
void my_opt(int* options, FILE* file)
{
	//Set up
	PTE frames[options[1]];
	init_frames(frames, options[1]);

	int accesses= 0;
	int faults = 0;
	int writes = 0;

	//Begin the opt algorithm

	unsigned int address;
	char mode;
	//Create a list that can hold a linked list for every possible page of 20 bits
	struct OptNode *map[1048575] = { NULL };

	//Prescan the file to take into account what is in the future for each page
	fpos_t pos;
	fgetpos(file, &pos);
	int counter = 0;
	while(fscanf(file, "%x %c", &address, &mode) != EOF)
	{
		unsigned int page = address & 0xfffff000;
		int index = page >> 12;
		
		//Create a new node
		struct OptNode *new = malloc(sizeof(struct OptNode*));
		new->page = page;
		new->location = counter;
		new->next = NULL;
		new->prev = NULL;

		//New page to take into account
		if(map[index] == NULL)
		{
			map[index] = new;
		}
		//Add a page to the beginning of its existing list
		else
		{
			new->next = map[index];
			map[index]->prev = new;
			map[index] = new;
		}
		counter++;
	}

	//Set all of the lists to last element so they start on the earliest locations
	int j;
	for(j = 0; j < 1048575; j++)
	{
		if(map[j] != NULL)
		{
			while(map[j]->next != NULL)
				map[j] = map[j]->next;
		}
	}


	//Reset the file pointer for proper scanning
	fsetpos(file, &pos);
	
	counter = 0;
	//While the end of the file has not been reached
	while(fscanf(file, "%x %c", &address, &mode) != EOF)
	{

		//Get page number from the trace file
		unsigned int page = address & 0xfffff000;
		int offset = address & 0x00000fff;

		//Check if the page is already in a fram
		int found = find_page(frames, options[1], page);
		if(found < 0)
		{
			faults++;

			//Create the page table entry
			PTE new;
			new.page_num = page;
			new.ref_bit = 0;
			new.valid = 1;
			if(mode == 'W')
				new.dirty = 1;
			else
				new.dirty = 0;

			//Assign the page number to a frame
			int index = find_free_frame(frames, options[1]);
			if(index < 0)
			{
				//The following is a naive approach to find the next page to replace
				//Kept for documentation purposes
				/*fpos_t pos;
				fgetpos(file, &pos);
				int i;
				unsigned int scan_addr;
				char scan_mode;

				//Scan ahead in the file to inspect future page requests
				//Needs redone: Way too slow
				
				int most = 0;
				int amount = 0;
				int count = 0;
			
				for(i = 0; i < options[1]; i++)
				{
					int found = 0;
					//Increment how many instructions ahead a certain page request is
					while(fscanf(file, "%x %c", &scan_addr, &scan_mode) != EOF && found == 0)
					{
						count++;
						int newpage = scan_addr & 0xfffff000;
						if(newpage == frames[i].page_num)
							found = 1;
					}
					if(count > amount)
					{
						amount = count;
						most = i;
					}
					count = 0;
					//Reset the file pointer and continue for all other pages in the frame table
					fsetpos(file, &pos);
				}

				fsetpos(file, &pos);
				*/
				
				//Most is the index of the page farthest in the future
				//Amount is the location in the file of that page
				int most = 0;
				int amount = 0;
				//New Opt Algorithm starts here
				int i;
				for(i = 0; i < options[1]; i++)
				{
					//Shift the bits over to find the page's linked list
					int index = frames[i].page_num >> 12;
					//Loop through the list to find where the next instance of the page is
					while(map[index] != NULL && map[index]->location < counter)
					{
						//printf("%d, %d\n", map[index]->location, counter);
						map[index] = map[index]->prev;
					}

					//If the page is never used again, use that page
					if(map[index] == NULL)
					{
						most = i;
						break;
					}
					//If that page is farthest in the future, mark that page accordingly
					else if(map[index]->location > amount)
					{
						amount = map[index]->location;
						most = i;
					}
				}
				
				//If the entry is dirty, write it to memory
				if(frames[most].dirty == 1)
				{
					writes++;
					printf("%x, %c, pagefault- evict dirty\n", address, mode);
				}
				else
					printf("%x, %c, pagefault- evict clean\n", address, mode);

				//Write the new page table entry to the table
				frames[most] = new;
			}
			else
			{
				frames[index] = new;
				printf("%x, %c, pagefault- no evict\n", address, mode);
			}
		}
		else
		{
			if(mode == 'W')
				frames[found].dirty = 1;
			printf("%x, %c, hit\n", address, mode);
		}
		accesses++;
		counter++;
	}

	print_stats("opt", options[1], accesses, faults, writes);
}

//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
//Clock replacement algorithm
void my_clock(int* options, FILE* file)
{
	//Set up
	PTE frames[options[1]];
	init_frames(frames, options[1]);

	int accesses= 0;
	int faults = 0;
	int writes = 0;

	unsigned int address;
	char mode;

	//While the end of the file has not been reached
	while(fscanf(file, "%x %c", &address, &mode) != EOF)
	{

		//Get page number
		unsigned int page = address & 0xfffff000;
		int offset = address & 0x00000fff;
		int pointer = 0;

		//Check if the page is already in a frame
		int found = find_page(frames, options[1], page);
		if(found < 0)
		{
			faults++;

			//Create a new page table entry
			PTE new;
			new.page_num = page;
			new.ref_bit = 1;
			new.valid = 1;
			if(mode == 'W')
				new.dirty = 1;
			else
				new.dirty = 0;

			//Assign the page number to a frame
			//Check if there is a free frame
			int index = find_free_frame(frames, options[1]);
			if(index < 0)
			{
				//Begin clock algorithm
				int found_index = 0;
				while(found_index == 0)
				{
					//Find an unreferenced page
					if(frames[pointer].ref_bit == 0)
						found_index = 1;
					//If that page is referenced, mark it as unreferenced and move around the clock
					if(found_index == 0)
					{
						frames[pointer].ref_bit = 0;
						pointer = (pointer + 1) % options[1];
					}
				}

				//If the entry is dirty, write it to memory
				if(frames[pointer].dirty == 1)
				{
					writes++;
					printf("%x, %c, pagefault- evict dirty\n", address, mode);
				}
				else
					printf("%x, %c, pagefault- evict clean\n", address, mode);

				//Write the new page table entry to the table
				frames[pointer] = new;
				pointer = (pointer + 1) % options[1];
			}
			else
			{
				frames[index] = new;
				printf("%x, %c, pagefault- no evict\n", address, mode);
			}
		}
		else
		{
			//Modify the page table entry if it is being written to and mark it as referenced
			if(mode == 'W')
				frames[found].dirty = 1;
			frames[found].ref_bit = 1;
			printf("%x, %c, hit\n", address, mode);
		}
		accesses++;
	}

	print_stats("clock", options[1], accesses, faults, writes);

}

//****************************************************************************************************************************************************************
//NRU replacement algorithm
void my_nru(int* options, FILE* file)
{
	//Set up
	PTE frames[options[1]];
	init_frames(frames, options[1]);

	int accesses= 0;
	int faults = 0;
	int writes = 0;

	//Begin the nru algorithm

	unsigned int address;
	char mode;

	int counter = 0;
	int limit = options[2];

	while(fscanf(file, "%x %c", &address, &mode) != EOF)
	{
		//Reset all of the reference bits after the allotted time has ellapsed
		if(counter == limit)
		{
			int i;
			for(i = 0; i < options[1]; i++)
			{
				frames[i].ref_bit = 0;
			}
			counter = 0;
		}

		//Get page number
		unsigned int page = address & 0xfffff000;
		int offset = address & 0x00000fff;

		//Check if the page is already in a frame
		int found = find_page(frames, options[1], page);
		if(found < 0)
		{
			faults++;

			//Create a new page table entry
			PTE new;
			new.page_num = page;
			new.ref_bit = 1;
			new.valid = 1;
			if(mode == 'W')
				new.dirty = 1;
			else
				new.dirty = 0;

			//Assign the page number to a frame
			int index = find_free_frame(frames, options[1]);
			if(index < 0)
			{
				//NRU algorithm
				int found = 0;
				int j;
				//Search for a clean, unreferenced page
				for(j = 0; j < options[1]; j++)
				{
					if(frames[j].ref_bit == 0 && frames[j].dirty == 0)
					{
						found = 1;
						break;
					}
				}

				if(found == 0)
				{
					//If not found, search for a dirty, unreferenced page
					for(j = 0; j < options[1]; j++)
					{
						if(frames[j].ref_bit == 0)
						{
							found = 1;
							break;
						}
					}
					if(found == 0)
					{
						//If not found, search for a clean referenced page
						for(j = 0; j < options[1]; j++)
						{
							if(frames[j].dirty == 0)
							{
								found = 1;
								break;
							}
						}
						//No good choice found at all, so just remove the first entry
						if(found == 0)
							j = 0;
					}
				}
			

				//If the entry is dirty, write it to memory
				if(frames[j].dirty == 1)
				{
					writes++;
					printf("%x, %c, pagefault- evict dirty\n", address, mode);
				}
				else
					printf("%x, %c, pagefault- evict clean\n", address, mode);

				//Write the new page table entry to the table
				frames[j] = new;			
			}
			else
			{
				frames[index] = new;
				printf("%x, %c, pagefault- no evict\n", address, mode);
			}
		}
		else
		{
			if(mode == 'W')
				frames[found].dirty = 1;
			frames[found].ref_bit = 1;
			printf("%x, %c, hit\n", address, mode);
		}
		accesses++;
		counter++;
	}

	print_stats("nru", options[1], accesses, faults, writes);
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
//Random replacement algorithm
void my_rand(int* options, FILE* file)
{
	//Set up
	PTE frames[options[1]];
	init_frames(frames, options[1]);

	int accesses= 0;
	int faults = 0;
	int writes = 0;

	//Begin the rand algorithm

	unsigned int address;
	char mode;

	//Scan through the file
	while(fscanf(file, "%x %c", &address, &mode) != EOF)
	{

		//Get page number
		unsigned int page = address & 0xfffff000;
		int offset = address & 0x00000fff;

		//Check if the page is already in a frame
		int found = find_page(frames, options[1], page);
		if(found < 0)
		{
			faults++;

			//Create the page table entry
			PTE new;
			new.page_num = page;
			new.ref_bit = 0;
			new.valid = 1;
			if(mode == 'W')
				new.dirty = 1;
			else
				new.dirty = 0;

			//Assign the page number to a frame
			int index = find_free_frame(frames, options[1]);
			if(index < 0)
			{
				//Implement the rand algorithm
				//Using a random number generator, generate an index at random
				int ev_index = rand() % (options[1] - 0);

				if(frames[ev_index].dirty == 1)
				{
					writes++;
					printf("%x, %c, pagefault- evict dirty\n", address, mode);
				}
				else
				{
					printf("%x, %c, pagefault- evict clean\n", address, mode);
				}

				//Overwrite the page with the new one
				frames[ev_index] = new;
			}
			else
			{
				frames[index] = new;
				printf("%x, %c, pagefault- no evict\n", address, mode);
			}
		}
		else
		{
			if(mode == 'W')
				frames[found].dirty = 1;
			printf("%x, %c, hit\n", address, mode);
		}
		accesses++;
	}

	print_stats("rand", options[1], accesses, faults, writes);
}

//Helper method for printing results
void print_stats(char* algo, int frames, int accesses, int faults, int writes)
{
	printf("Algorithm: %s\n", algo);
	printf("Number of frames: %d\n", frames);
	printf("Total memory accesses: %d\n", accesses);
	printf("Total page faults: %d\n", faults);
	printf("Total writes to disk: %d\n", writes);
}


//Helper method for printing argument error message
void exit_with_arg_error(int x)
{
	printf("Error: incorrect argument format %d\n", x);
	exit(-1);
}

//Process the arguments
int* process_args(int* argc, char** args, int* options)
{
	char* current_arg;
	options[2] = 0;

	current_arg = args[1];
	//Check for number of frames
	if(strcmp(current_arg, "-n") == 0)
	{
		options[1] = atoi(args[2]);
		//Check for a proper number of frames
		if(options[1] <= 0)
		{
			printf("Error: number of frames must be greater than 0\n");
			exit(-1);
		}
	}
	else
		exit_with_arg_error(3);

	current_arg = args[3];
	//Check for algorithm
	if(strcmp(current_arg, "-a") == 0)
	{
		current_arg = args[4];
		if(strcmp(current_arg, "opt") == 0)
			options[0] = 0;
		else if(strcmp(current_arg, "clock") == 0)
			options[0] = 1;
		else if(strcmp(current_arg, "nru") == 0)
			options[0] = 2;
		else if(strcmp(current_arg, "rand") == 0)
			options[0] = 3;
		else
			exit_with_arg_error(2);

	}
	else
		exit_with_arg_error(1);


	//Check for possible refresh option
	if(options[0] == 2)
	{
		current_arg = args[5];
		if(strcmp(current_arg, "-r") == 0)
		{
			options[2] = atoi(args[6]);	
		}
		else
			exit_with_arg_error(4);	
	}

}

int main(int* argc, char** args)
{
	//Process  the incoming arguments
	int options[3];
	//if(*argc < 6)
		//exit_with_arg_error(1);
	process_args(argc, args, options);

	printf("All processed\n");

	printf("%d, %d, %d, %s\n", options[0], options[1], options[2], args[5]);

	//Set up trace file info
	FILE *file;
	if(options[0] != 2)
		file = fopen(args[5], "r");
	else
		file = fopen(args[7], "r");

	//Run the algorithms
	if(options[0] == 0)
		my_opt(options, file);
	else if(options[0] == 1)
		my_clock(options, file);
	else if(options[0] == 2)
		my_nru(options, file);
	else
		my_rand(options, file);

	fclose(file);
	return 0;
}