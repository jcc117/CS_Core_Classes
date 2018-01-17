#include <stdio.h>
#include <unistd.h>

#include "mymalloc.h"

//Jordan Carr
//jcc117

void test1()
{
	void* block = my_malloc(1000);
	void* block2 = my_malloc(10);
	void* block3 = my_malloc(900);
	void* block4 = my_malloc(200);
	void* block5 = my_malloc(300);
	void* block6 = my_malloc(1000);
	block = (char*)block;
	my_free(block);
	//void* block3 = my_malloc(900);
	
	my_free(block3);
	my_free(block2);
	my_free(block5);
	my_free(block4);
	void* big_ass_block = my_malloc(2200);
	if(block == big_ass_block)
		printf("yeeeeeeee\n");
	my_free(big_ass_block);
	my_free(block6);
	
	
	//block3 = (char*)block3;
	//block4 = (char*)block4;
	//block5 = (char*)block5;
	//block6 = (char*)block6;
	//my_free(block4);
	//my_free(block5);
	//my_free(block6);
	//my_free(block3);
}

int main()
{
	void* heap_at_start = sbrk(0);

	// Put your testing code between here and the other comment.

	test1();

	// The code below and at the beginning of the function checks
	// that you contracted the heap properly (assuming you've freed
	// everything that you allocated).

	void* heap_at_end = sbrk(0);
	unsigned int heap_size_diff = (unsigned int)(heap_at_end - heap_at_start);

	if(heap_size_diff)
		printf("Hmm, the heap got bigger by %u (0x%X) bytes...\n", heap_size_diff, heap_size_diff);
	else
		printf("yay\n");

	return 0;
}