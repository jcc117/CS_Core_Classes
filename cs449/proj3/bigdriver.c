#include <stdio.h>
#include <unistd.h>

#include "mymalloc.h"

/*Ooooh, pretty colors...*/
#define CRESET   "\e[0m"
#define CRED     "\e[31m"
#define CGREEN   "\e[32m"
#define CYELLOW  "\e[33m"
#define CBLUE    "\e[34m"
#define CMAGENTA "\e[35m"
#define CCYAN    "\e[36m"

/*You can use these in printf format strings like:

	printf("This is " RED("red") " text.\n");

NO COMMAS!!*/
#define RED(X)     CRED X CRESET
#define GREEN(X)   CGREEN X CRESET
#define YELLOW(X)  CYELLOW X CRESET
#define BLUE(X)    CBLUE X CRESET
#define MAGENTA(X) CMAGENTA X CRESET
#define CYAN(X)    CCYAN X CRESET

#define PTR_ADD_BYTES(ptr, byte_offs) ((void*)(((char*)(ptr)) + (byte_offs)))

void check_heap_size(const char* where, void* heap_at_start)
{
	void* heap_at_end = sbrk(0);
	unsigned int heap_size_diff = (unsigned int)(heap_at_end - heap_at_start);

	if(heap_size_diff)
		printf(RED("After %s the heap got bigger by %u (0x%X) bytes...\n"),
			where, heap_size_diff, heap_size_diff);
	else
		printf(GREEN("Yay, after %s, everything was freed!\n"), where);
}

void fill_array(int* arr, int length)
{
	int i;

	for(i = 0; i < length; i++)
		arr[i] = i + 1;
}

int* make_array(int length)
{
	int* ret = my_malloc(sizeof(int) * length);
	fill_array(ret, length);
	return ret;
}
void test2()
{
	int* a = make_array(1000);
	int* b = make_array(30);
	my_free(a);
	int* c = make_array(30);
	int* d = make_array(10);
	if(a == c)
		printf(GREEN("you didn't fuck up\n"));
	my_free(b);
	my_free(c);
	my_free(d);
}

/*A very simple test that allocates two blocks, fills them with data,
then frees them in reverse order. Even the simplest allocator should
work for this, and the heap should be back where it started afterwards.*/
void test_simple()
{
	void* heap_at_start = sbrk(0);
	int* a = make_array(10);
	int* b = make_array(10);

	/*Just to make sure..*/
	int i;
	for(i = 0; i < 10; i++)
		printf("a[%d] = %d, b[%d] = %d\n", i, a[i], i, b[i]);

	/*Freeing in reverse order.*/
	my_free(b);
	my_free(a);

	check_heap_size("test_simple", heap_at_start);
}

/*A more complex test that makes sure you can deallocate in any order and
that those deallocated blocks can be reused.*/
void test_reuse()
{
	void* heap_at_start = sbrk(0);
	int* a = make_array(20);
	int* b = make_array(20);
	my_free(a);

	/*
	After that free, your heap should have two blocks:
	- A free 80-byte block at the beginning
	- Then a used 80-byte block as the heap_tail

	So when we allocate another block of a *smaller* size,
	it should reuse the first one: */

	int* c = make_array(10);

	if(a != c)
		printf(RED("You didn't reuse the free block!\n"));

	/*
	Here, if you DIDN'T implement splitting,
	you will still have two blocks:
	- A used 80-byte block (NOT 40 bytes!) at the beginning
	- A used 80-byte block as the heap_tail

	But if you DID implement splitting, you will have:
	- A used 40-byte block at the beginning
	- A free *24-byte* block in the middle
	- A used 80-byte block as the heap_tail*/

	my_free(c);

	/*
	No matter what, here you will have two blocks:
	- A free 80-byte block at the beginning
	- A used 80-byte block as the heap_tail */

	my_free(b);

	/*
	Finally, if you DIDN'T implement coalescing, you
	will still have one free 80-byte block on the heap,
	and you'll get a message saying the heap grew
	by 96 bytes.

	But if you DID implement coalescing, you will have
	nothing left on the heap here! */

	check_heap_size("test_reuse", heap_at_start);
}

/*Makes sure that your coalescing works.*/
void test_coalescing()
{
	void* heap_at_start = sbrk(0);
	int* a = make_array(10);
	int* b = make_array(10);
	int* c = make_array(10);
	int* d = make_array(10);
	int* e = make_array(10);

	/*Should have 5 used 40-byte blocks.

	Now let's test freeing. The first free will test
	freeing at the beginning of the heap, and the next
	ones will test with a single previous free neighbor.

	After each of these frees, you should have ONE
	free block of the given size:*/
	my_free(a); /* 40B */
	my_free(b); /* 96B */
	my_free(c); /* 152B */
	my_free(d); /* 208B */

	/*This should reuse a's block.*/
	int* f = make_array(52);

	if(a != f)
		printf(RED("You didn't reuse the coalesced block!\n"));

	/*Now, when we free these, they should coalesce into
	a single big block, and then be sbrk'ed away!*/
	my_free(f);
	my_free(e);

	check_heap_size("part 1 of test_coalescing", heap_at_start);

	/* Let's re-allocate... */
	a = make_array(10);
	b = make_array(10);
	c = make_array(10);
	d = make_array(10);
	e = make_array(10);

	/*Now let's test freeing random blocks. my_free(a)
	will test freeing with a single next free neighbor, and
	my_free(c) will test with two free neighbors.

	After each you should have:*/
	my_free(b); /* one free 40B */
	my_free(d); /* two free 40B */
	my_free(a); /* one free 96B, one free 40B */
	my_free(c); /* one free 208B */
	my_free(e); /* nothing left! */

	check_heap_size("part 2 of test_coalescing", heap_at_start);

	/*Finally, let's make sure coalescing at the beginning
	and end of the heap work properly.*/

	a = make_array(10);
	b = make_array(10);
	c = make_array(10);
	d = make_array(10);
	e = make_array(10);

	/*After each you should have:*/
	my_free(b); /* one free 40B, four used 40B */
	my_free(a); /* one free 96B, three used 40B */
	my_free(d); /* one free 96B, one free 40B, two used 40B */
	my_free(e); /* one free 96B, one used 40B */
	my_free(c); /* nothing left! */

	check_heap_size("part 3 of test_coalescing", heap_at_start);
}

/*Makes sure that your block splitting works.*/
void test_splitting()
{
	void* heap_at_start = sbrk(0);

	int* medium = make_array(64); /*make a 256-byte block.*/
	int* holder = make_array(4);  /*holds the break back.*/
	my_free(medium);

	/*Now there should be a 256-byte block in a bin.*/

	/*THIS allocation SHOULD NOT split the block, since it's
	too big (would leave a too-small block).*/
	int* too_big = make_array(57);

	/*Now you should have three blocks on the heap.*/

	/*Let's see if your algorithm can find small blocks to split.

	MAKE SURE YOUR BINNING WORKS RIGHT ON THESE. When you split
	a block, it should be taken out of the old bin, and the new
	smaller block should be put in a new bin!

	After each, you should be left with a free block of the given
	size:*/
	int* tiny1 = make_array(4); /* 224B */
	int* tiny2 = make_array(4); /* 192B */
	int* tiny3 = make_array(4); /* 160B */
	int* tiny4 = make_array(4); /* 128B */

	if(tiny1 != medium)
		printf(RED("You didn't split the 256B block!\n"));
	else if(tiny2 != PTR_ADD_BYTES(tiny1, 32))
		printf(RED("You didn't split the 224B block!\n"));
	else if(tiny3 != PTR_ADD_BYTES(tiny2, 32))
		printf(RED("You didn't split the 192B block!\n"));
	else if(tiny4 != PTR_ADD_BYTES(tiny3, 32))
		printf(RED("You didn't split the 160B block!\n"));

	my_free(tiny1);
	my_free(tiny2);
	my_free(tiny3);
	my_free(tiny4);
	my_free(too_big);
	my_free(holder);
	check_heap_size("test_splitting part 1", heap_at_start);

	int* huge = make_array(1024);
	holder = make_array(4);
	/*This huge array should go in the overflow bin.*/
	my_free(huge);

	/*You should have a 4096B block in the overflow bin. Let's
	split it. The remainders will stay in the overflow bin and
	will be the following sizes:*/
	tiny1 = make_array(4); /* 4064B */
	tiny2 = make_array(4); /* 4032B */
	tiny3 = make_array(4); /* 4000B */
	tiny4 = make_array(4); /* 3968B */

	if(tiny1 != huge)
		printf(RED("You didn't split the 4096B block!\n"));
	else if(tiny2 != PTR_ADD_BYTES(tiny1, 32))
		printf(RED("You didn't split the 4064B block!\n"));
	else if(tiny3 != PTR_ADD_BYTES(tiny2, 32))
		printf(RED("You didn't split the 4032B block!\n"));
	else if(tiny4 != PTR_ADD_BYTES(tiny3, 32))
		printf(RED("You didn't split the 4000B block!\n"));

	/*Last, let's see if you can reuse that *whole* 3968B block,
	since we are using first-fit in the overflow bin, and can
	use blocks even if they're not splittable*/

	int* right_size = make_array(3968/4);

	if(right_size != PTR_ADD_BYTES(tiny4, 32))
		printf(RED("You didn't reuse the 3968B block!\n"));

	/*You should have NO free blocks at this point.*/

	my_free(tiny1);
	my_free(tiny2);
	my_free(tiny3);
	my_free(tiny4);
	my_free(holder);
	my_free(right_size);
	check_heap_size("test_splitting part 2", heap_at_start);
}

int main()
{
	void* heap_at_start = sbrk(0);

	/*Uncomment a test and recompile before running it.
	When complete, you should be able to uncomment all the tests
	and they should run flawlessly.*/
	test2();
	test_simple();
	test_reuse();
	test_coalescing();

	/*There's no specific test for binning, but you should
	print out debug info in your allocator with all the tests
	to make sure your binning works.*/

	//test_splitting();

	/*Just to make sure!*/
	check_heap_size("main", heap_at_start);
	return 0;
}