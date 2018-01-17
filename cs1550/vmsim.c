#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

//Jordan Carr
//CS1550
//Project 3

void print_stats(char* algo, int frames, int accesses, int faults, int writes);

typedef struct 
{
	int page_num;
	int dirty;
	int ref_bit;
	int valid; 
}PTE;

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
void create_pte(PTE *new, int page, char mode)
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
int find_page(PTE frames[], int size, int page)
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
void my_opt(int* options, FILE* file)
{
	printf("opt\n");
	
	//Set up
	PTE frames[options[1]];
	init_frames(frames, options[1]);

	int accesses= 0;
	int faults = 0;
	int writes = 0;

	//Begin the opt algorithm

	unsigned int address;
	char mode;

	//Set up skeleton for now
	//While the end of the file has not been reached
	while(fscanf(file, "%x %c", &address, &mode) != EOF)
	{

		//Get page number
		int page = address & 0xffff0000;
		int offset = address & 0x0000ffff;

		//Check if the page is already in a fram
		int found = find_page(frames, options[1], page);
		if(found < 1)
		{
			faults++;

			//Create the page table entry
			//printf("create a new entry\n");
			//PTE *new;
			//create_pte(new, page, mode);

			PTE new;
			new.page_num = page;
			new.ref_bit = 0;
			new.valid = 1;
			if(mode == 'W')
				new.dirty = 1;
			else
				new.dirty = 0;

			//printf("%d, %d, %d, %d\n", new->page_num, new->dirty, new->valid, new->ref_bit);

			//Assign the page number to a frame
			//printf("find a frame\n");
			int index = find_free_frame(frames, options[1]);
			if(index < 0)
			{
				fpos_t pos;
				fgetpos(file, &pos);
				int i;
				unsigned int scan_addr;
				char scan_mode;

				//Scan ahead in the file to inspect future page requests
				//printf("now scanning\n");
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
						int newpage = scan_addr & 0xffff0000;
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

				//If full, pagefault++ and scan ahead in the file, accesses++, and look and keep note if
				//a frame number is used
				//If you find one that's never used again pick that one
				//If not, pick the one farthest in the future- determined by
					//the number of skips needed ahead in the file
				//If that memory page is being written to, save it to disk writes++
				//Keep note to modify proper variables
				//This will be a very slow aglorithm
			
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

	print_stats("opt", options[1], accesses, faults, writes);
}

//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
void my_clock(int* options, FILE* file)
{
	printf("clock\n");

}

//****************************************************************************************************************************************************************
void my_nru(int* options, FILE* file)
{
	printf("nru\n");
	//Able to start

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

	//Set up skeleton for now
	//While the end of the file has not been reached
	while(fscanf(file, "%x %c", &address, &mode) != EOF)
	{

		//Get page number
		int page = address & 0xffff0000;
		int offset = address & 0x0000ffff;

		//Check if the page is already in a fram
		int found = find_page(frames, options[1], page);
		if(found < 1)
		{
			faults++;

			//Create the page table entry
			//printf("create a new entry\n");
			//PTE *new;
			//create_pte(new, page, mode);

			PTE new;
			new.page_num = page;
			new.ref_bit = 0;
			new.valid = 1;
			if(mode == 'W')
				new.dirty = 1;
			else
				new.dirty = 0;

			//printf("%d, %d, %d, %d\n", new->page_num, new->dirty, new->valid, new->ref_bit);

			//Assign the page number to a frame
			//printf("find a frame\n");
			int index = find_free_frame(frames, options[1]);
			if(index < 0)
			{

				//If the entry is dirty, write it to memory
				if(frames[most].dirty == 1)
				{
					writes++;
					printf("%x, %c, pagefault- evict dirty\n", address, mode);
				}
				else
					printf("%x, %c, pagefault- evict clean\n", address, mode);

				//Write the new page table entry to the table
			
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

	print_stats("nru", options[1], accesses, faults, writes);
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
void my_rand(int* options, FILE* file)
{
	printf("rand\n");
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
		int page = address & 0xffff0000;
		int offset = address & 0x0000ffff;

		//Check if the page is already in a fram
		int found = find_page(frames, options[1], page);
		if(found < 1)
		{
			faults++;

			//Create the page table entry
			//printf("create a new entry\n");
			//PTE *new;
			//create_pte(new, page, mode);

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
				int ev_index = rand() % (options[1] + 1 - 0);

				if(frames[ev_index].dirty == 1)
				{
					writes++;
					//printf("%x, %c, pagefault- evict dirty\n", address, mode);
				}
				else
				{
					//printf("%x, %c, pagefault- evict clean\n", address, mode);
				}

				//Overwrite the page with the new one
				frames[ev_index] = new;
			}
			else
			{
				frames[index] = new;
				//printf("%x, %c, pagefault- no evict\n", address, mode);
			}
		}
		else
		{
			if(mode == 'W')
				frames[found].dirty = 1;
			//printf("%x, %c, hit\n", address, mode);
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