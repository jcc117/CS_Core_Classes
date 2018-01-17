#include <assert.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "mymalloc.h"

//Jordan Carr
//jcc117

// The smallest allocation possible is this many bytes.
// Any allocations <= this size will b put in bin 0.
#define MINIMUM_ALLOCATION  16

// Every bin holds blocks whose sizes are a multiple of this number.
#define SIZE_MULTIPLE       8

// The biggest bin holds blocks of this size. Anything bigger will go in the overflow bin.
#define BIGGEST_BINNED_SIZE 512

// How many bins there are. There's an "underflow" bin (bin 0) and an overflow bin (the last bin).
// That's where the '2' comes from in this formula.
#define NUM_BINS            (2 + ((BIGGEST_BINNED_SIZE - MINIMUM_ALLOCATION) / SIZE_MULTIPLE))

// The index of the overflow bin.
#define OVERFLOW_BIN        (NUM_BINS - 1)

// How many bytes the block header is, in a USED block.
// NEVER USE sizeof(BlockHeader) in your calculations! Use this instead.
#define BLOCK_HEADER_SIZE   offsetof(BlockHeader, prev_free)

// The smallest number of bytes a block (including header and data) can be.
#define MINIMUM_BLOCK_SIZE  (MINIMUM_ALLOCATION + BLOCK_HEADER_SIZE)

typedef struct BlockHeader
{
	unsigned int size; // The byte size of the data area of this block.
	int in_use;        // 1 if allocated, 0 if free.

	// Doubly-linked list pointers for the previous and next *physical* blocks of memory.
	// All blocks, allocated or free, must keep track of this for coalescing purposes.
	struct BlockHeader* prev_phys;
	struct BlockHeader* next_phys;

	// These next two members are only valid if the block is not in use (on a free list).
	// If the block is in use, the user-allocated data starts here instead!
	struct BlockHeader* prev_free;
	struct BlockHeader* next_free;
} BlockHeader;

// Your array of bins.
BlockHeader* bins[NUM_BINS] = {};

// The LAST allocated block on the heap.
// This is used to keep track of when you should contract the heap.
BlockHeader* heap_tail = NULL;

// =================================================================================================
// Math helpers
// =================================================================================================

// Given a pointer and a number of bytes, gives a new pointer that points to the original address
// plus or minus the offset. The offset can be negative.
// Since this returns a void*, you have to cast the result to another pointer type to use it.
void* ptr_add_bytes(void* ptr, int byte_offs)
{
	return (void*)(((char*)ptr) + byte_offs);
}

// Gives the number of bytes between the two pointers. first must be <= second.
unsigned int bytes_between_ptrs(void* first, void* second)
{
	return (unsigned int)(((char*)second) - ((char*)first));
}

// Given a pointer to a block header, gives the pointer to its data (such as what you'd return
// from my_malloc).
void* block_to_data(BlockHeader* block)
{
	return (void*)ptr_add_bytes(block, BLOCK_HEADER_SIZE);
}

// Given a data pointer (such as passed to my_free()), gives the pointer to the block that
// contains it.
BlockHeader* data_to_block(void* data)
{
	return (BlockHeader*)ptr_add_bytes(data, -BLOCK_HEADER_SIZE);
}

// Given a data size, gives how many bytes you'd need to allocate for a block to hold it.
unsigned int data_size_to_block_size(unsigned int data_size)
{
	return data_size + BLOCK_HEADER_SIZE;
}

// Rounds up a data size to an appropriate size for putting into a bin.
unsigned int round_up_size(unsigned int data_size)
{
	if(data_size == 0)
		return 0;
	else if(data_size < MINIMUM_ALLOCATION)
		return MINIMUM_ALLOCATION;
	else
		return (data_size + (SIZE_MULTIPLE - 1)) & ~(SIZE_MULTIPLE - 1);
}

// Given a data size in bytes, gives the correct bin index to put it in.
unsigned int size_to_bin(unsigned int data_size)
{
	unsigned int bin = (round_up_size(data_size) - MINIMUM_ALLOCATION) / SIZE_MULTIPLE;

	if(bin > OVERFLOW_BIN)
		return OVERFLOW_BIN;
	else
		return bin;
}

// =================================================================================================
// Your functions!
// =================================================================================================

// Put any of your code here.

// =================================================================================================
// Public functions
// =================================================================================================
int is_heap_tail(BlockHeader* ptr);
void add_to_bin(BlockHeader* ptr, int bin_index);
void remove_from_bin(BlockHeader* ptr, int bin_index);
BlockHeader* coalesce_blocks(BlockHeader* ptr, int state);

void* my_malloc(unsigned int size)
{
	if(size == 0)
		return NULL;
	int index = 0;
	size = round_up_size(size);
	BlockHeader* new_block = NULL;
	// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
	// Any allocation code goes here!
	//Search for a free block
	
	//Search for a block of perfect size in the small bins
	int found_free_block = 0;
	BlockHeader* free_block = bins[index];
	while (index < NUM_BINS -1)//Change this for splitting blocks!!!!!!!!!
	{
		//Set free block to the next bin
		free_block = bins[index];
		while(free_block != NULL)
		{
			if(free_block->size == size)//Change this for splitting the blocks!!!!!!!!!
			{
				found_free_block = 1;
				break;
			}
			else
			{
				free_block = free_block->next_free;
			}
		}
		//Leave loop if suitable block is found
		if(found_free_block)
		{
			break;
		}
		index++;
	}
	//If a free block that is big enough is found, use it
	void* temp;
	if(found_free_block)
	{
		remove_from_bin(free_block, index);
		new_block = free_block;
		new_block->in_use = 1;
	}
	//If not, look for a bigger one to split
	else
	{
		index = 0;
		free_block = bins[index];
		while (index < NUM_BINS - 1)
		{
			free_block = bins[index];
			while(free_block != NULL)
			{
				if(free_block->size >= (size + MINIMUM_BLOCK_SIZE))
				{
					found_free_block = 1;
					break;
				}
				else
				{
					free_block = free_block->next_free;
				}
			}
			if(found_free_block)
				break;
			index++;
			
		}
		if(found_free_block)
		{
			//Split the block
			//Set split block's new size
			int split_block_size = free_block->size - size - BLOCK_HEADER_SIZE;
			BlockHeader* split_block = (BlockHeader*)ptr_add_bytes(free_block, size + BLOCK_HEADER_SIZE);
			//Remove that block from the free list
			remove_from_bin(free_block, index);
			//Set split block's previous block
			split_block->prev_phys = free_block;
			//Set split block's next block
			if(is_heap_tail(free_block))
			{
				heap_tail = split_block;
				split_block->next_phys = NULL;
			}
			else
			{
				split_block->next_phys = free_block->next_phys;
			}
			//Have free_block's next block point to the split block if not NULL
			if(free_block->next_phys != NULL)
				free_block->next_phys->prev_phys = split_block;
			free_block->next_phys = split_block;
			//Set size, use, and prev/next free blocks
			split_block->size = split_block_size;
			split_block->in_use = 0;
			split_block->prev_free = NULL;
			split_block->next_free = NULL;
			//Put split block into a new bin
			int split_index = size_to_bin(split_block->size);
			add_to_bin(split_block, split_index);
			//Set the new block to the current free block
			free_block->in_use = 1;
			free_block->size = size;
			new_block = free_block;
		}
		//Look in the OVERFLOW_BIN
		else
		{
			free_block = bins[OVERFLOW_BIN];
			int splittable = 0;
			while(free_block != NULL)
			{
				if(free_block->size >= (size + MINIMUM_BLOCK_SIZE))
				{
					found_free_block = 1;
					splittable = 1;
					break;
				}
				else if(free_block->size >= size)
				{
					found_free_block = 1;
					break;
				}
				else
				{
					free_block = free_block->next_free;
				}
			}
			//If splittable, split the block
			if (splittable && found_free_block)
			{
				//Split the block
				//Set the size of split block
				int split_block_size = free_block->size - size - BLOCK_HEADER_SIZE;
				BlockHeader* split_block = (BlockHeader*)ptr_add_bytes(free_block, size + BLOCK_HEADER_SIZE);
				//remove the block from the free bin
				remove_from_bin(free_block, OVERFLOW_BIN);
				//Set the prev block
				split_block->prev_phys = free_block;
				//Set the next block
				if(is_heap_tail(free_block))
				{
					heap_tail = split_block;
					split_block->next_phys = NULL;
				}
				else
				{
					split_block->next_phys = free_block->next_phys;
				}
				if(free_block->next_phys != NULL)
					free_block->next_phys->prev_phys = split_block;
				//Have free block point to the split block
				free_block->next_phys = split_block;
				//Set the size, use, prev, and next blocks
				split_block->size = split_block_size;
				split_block->in_use = 0;
				split_block->prev_free = NULL;
				split_block->next_free = NULL;
				//Add the block to its new free bin
				int split_index = size_to_bin(split_block->size);
				add_to_bin(split_block, split_index);
				free_block->in_use = 1;
				free_block->size = size;
				new_block = free_block;
			}
			//If not splittable, just assign it regularly
			else if(found_free_block)
			{
				//Assign the block
				remove_from_bin(free_block, OVERFLOW_BIN);
				new_block = free_block;
				new_block->in_use = 1;
			}
			//Add a new block to the end of the list (sbrk)
			else
			{
				temp = sbrk(size + BLOCK_HEADER_SIZE);
				if(heap_tail == NULL)
				{
					new_block = data_to_block(temp);
					new_block->size = size;
					new_block->in_use = 1;
					new_block->prev_phys = NULL;
					new_block->next_phys = NULL;
					heap_tail = new_block;
				}
				else
				{
					BlockHeader* prev_block = heap_tail;
					new_block = data_to_block(temp);
					new_block->size = size;
					new_block->in_use = 1;
					new_block->prev_phys = prev_block;
					new_block->next_phys = NULL;
					prev_block->next_phys = new_block;
					heap_tail = new_block;
				}
			}
		}
	}
	//return the newly allocated block
	return block_to_data(new_block);
	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
}

void my_free(void* ptr)
{
	if(ptr == NULL)
		return;

	BlockHeader* block_to_free = data_to_block(ptr);
	// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
	// Any deallocation code goes here!
	//If both blocks next to the block to free are free, coalesce them
	if((block_to_free->prev_phys != NULL) && (block_to_free->next_phys != NULL) && (block_to_free->prev_phys->in_use == 0) && (block_to_free->next_phys->in_use == 0))
	{
		//Set the prev block's nodes
		if(!is_heap_tail(block_to_free->next_phys))
		{
			block_to_free->prev_phys->next_phys = block_to_free->next_phys->next_phys;
			block_to_free->next_phys->next_phys->prev_phys = block_to_free->prev_phys;
		}
		else
		{
			heap_tail = block_to_free->prev_phys;
			block_to_free->prev_phys->next_phys = NULL;
		}
		block_to_free = coalesce_blocks(block_to_free, 2);
	}
	//If only the previous one is also free, coalesce them
	else if ((block_to_free->prev_phys != NULL) && (block_to_free->prev_phys->in_use == 0))
	{
		block_to_free = coalesce_blocks(block_to_free, 1);
	}
	//If only the next one is free, coaslesce them
	else if((block_to_free->next_phys != NULL) && (block_to_free->next_phys->in_use == 0))
	{
		//Set the block's nodes
		if(block_to_free->next_phys->next_phys != NULL)
			block_to_free->next_phys->next_phys->prev_phys = block_to_free;
		block_to_free = coalesce_blocks(block_to_free, 0);
		block_to_free->next_phys = block_to_free->next_phys->next_phys;
	}
	
	/*Check if the block is the only one on the physical list*/
	if((block_to_free->prev_phys == NULL) && (block_to_free->next_phys == NULL))
	{
		heap_tail = NULL;
		sbrk(-(block_to_free->size + BLOCK_HEADER_SIZE));
	}
	/*Check if the block is the heap tail*/
	else if(is_heap_tail(block_to_free))
	{
		/*If the block before the heap tail isn't null, set that block as the heap tail, otherwise set the heap tail to NULL*/
		block_to_free->prev_phys->next_phys = NULL;
		if(block_to_free->prev_phys != NULL)
		{
			heap_tail = block_to_free->prev_phys;
		}
		else
		{
			heap_tail = NULL;
		}
		block_to_free->prev_phys = NULL;
		int num_bytes_to_free = block_to_free->size + BLOCK_HEADER_SIZE;
		sbrk(-(num_bytes_to_free));
		
	}
	//If not the heap tail, set the block's use as free
	else
	{
		block_to_free->in_use = 0;
		int index = size_to_bin(block_to_free->size);
		add_to_bin(block_to_free, index);
	}
	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
}

//Check to see if the block is the header. Return 1 if yes, 0 if no
int is_heap_tail(BlockHeader* ptr)
{
	if(heap_tail == ptr)
	{
		return 1;
	}
	else
	{
		return 0;
	}
}

//Adds the block to the head of the given bin
void add_to_bin(BlockHeader* ptr, int bin_index)
{
	if(bins[bin_index] == NULL)
	{
		bins[bin_index] = ptr;
		ptr->next_free = NULL;
		ptr->prev_free = NULL;
	}
	else
	{
		bins[bin_index]->prev_free = ptr;
		ptr->next_free = bins[bin_index];
		bins[bin_index] = ptr;
		ptr->prev_free = NULL;
	}
}

//Removes a free block from a bin, takes a phys pointer
void remove_from_bin(BlockHeader* ptr, int bin_index)
{
	//If the block is only one in the list, set the list to NULL
	if((ptr->prev_free == NULL) && (ptr->next_free == NULL))
	{
		bins[bin_index] = NULL;
	}
	//If the block is the first in the list, set the next block to the head of the list
	else if(ptr->prev_free == NULL)
	{
		ptr->next_free->prev_free = NULL;
		bins[bin_index] = ptr->next_free;
		ptr->next_free = NULL;
	}
	//If the block is at the end of the list
	else if(ptr->next_free == NULL)
	{
		ptr->prev_free->next_free = NULL;
		ptr->prev_free = NULL;
	}
	//If the block is in the middle of the list
	else
	{
		ptr->prev_free->next_free = ptr->next_free->prev_free;
		ptr->next_free->prev_free = ptr->prev_free->next_free;
		ptr->prev_free = NULL;
		ptr->next_free = NULL;
	}
		
}

//Coalesces free blocks
BlockHeader* coalesce_blocks(BlockHeader* ptr, int state)
{
	int index = 0;
	//Coalesce with prev and next blocks
	if (state == 2)
	{
		/*Modify free list*/
		index = size_to_bin(ptr->prev_phys->size);
		remove_from_bin(ptr->prev_phys, index);
		index = size_to_bin(ptr->next_phys->size);
		remove_from_bin(ptr->next_phys, index);
		/*Modify physical list*/
		ptr->in_use = 0;
		ptr->prev_phys->size += (ptr->size + ptr->next_phys->size + 2*BLOCK_HEADER_SIZE);
		
		return ptr->prev_phys;
	}
	//Coalesce with only prev block
	else if (state == 1)
	{
		/*Modify free list*/
		index = size_to_bin(ptr->prev_phys->size);
		remove_from_bin(ptr->prev_phys, index);
		/*Modify physical list*/
		ptr->in_use = 0;
		ptr->prev_phys->next_phys = ptr->next_phys;
		ptr->prev_phys->size += (ptr->size + BLOCK_HEADER_SIZE);
		if(ptr->next_phys != NULL)
			ptr->next_phys->prev_phys = ptr->prev_phys;
		if(is_heap_tail(ptr))
			heap_tail = ptr->prev_phys;
		return ptr->prev_phys;
	}
	//Coalesce with only next block
	else
	{
		/*Modify free list*/
		index = size_to_bin(ptr->next_phys->size);
		remove_from_bin(ptr->next_phys, index);
		/*Modify physical list*/
		ptr->size += (ptr->next_phys->size + BLOCK_HEADER_SIZE);
		return ptr;
	}
}
