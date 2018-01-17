#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>

// =================================================================================================
//
//
// Read the lab PDF. The comments in here are to help you. They don't describe the lab.
//
//
// =================================================================================================

// As you can see, when printed correctly your output should be:
// this is the correct order for output!
const char* msg[] =
	{ "this ", "is ", "the ", "correct ", "order ", "for ", "output!" };

// The output buffer that the threads use to copy their pieces of the message, and its mutex.
char output_buffer[200];
pthread_mutex_t output_buffer_mutex = PTHREAD_MUTEX_INITIALIZER;

// The ID of the last thread that finished, and its associated mutex and condition variable.
int last_thread_finished = -1;
pthread_mutex_t finished_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t finished_cond = PTHREAD_COND_INITIALIZER;

void my_strcat(char* dest, const char* src);

// =================================================================================================
// DO NOT MODIFY ANYTHING ABOVE THIS LINE!
// =================================================================================================

void copy_message(const char* msg)
{
	pthread_mutex_lock(&output_buffer_mutex);
	my_strcat(output_buffer, msg);
	pthread_mutex_unlock(&output_buffer_mutex);
}

void wait_for_thread(int id)
{
	pthread_mutex_lock(&finished_mutex);
	while(last_thread_finished != id)
	{
		pthread_cond_wait(&finished_cond, &finished_mutex);
	}
	pthread_mutex_unlock(&finished_mutex);
}

void finish_thread(int id)
{
	pthread_mutex_lock(&finished_mutex);
	last_thread_finished = id;
	pthread_cond_broadcast(&finished_cond);
	pthread_mutex_unlock(&finished_mutex);
}

// =================================================================================================
// DO NOT MODIFY ANYTHING BELOW THIS LINE!
// =================================================================================================

void* thread_main(void* ctx)
{
	int id = (int)(long)ctx;
	const char* message = msg[id];

	// All threads except thread 0 wait for the previous thread to finish.
	if(id != 0)
		wait_for_thread(id - 1);

	// Then they copy their piece of the message...
	copy_message(message);
	// ..and then indicate that they've completed their work.
	finish_thread(id);
	return NULL;
}

int main()
{
	pthread_t threads[7];
	int i;

	// create the 7 threads...
	for(i = 0; i < 7; i++)
		pthread_create(&threads[i], NULL, &thread_main, (void*)(long)i);

	// ...then wait for them all to finish.
	for(i = 0; i < 7; i++)
		pthread_join(threads[i], NULL);

	// Then print the message the threads created.
	printf("%s\n", output_buffer);
	return 0;
}

// We're using this instead of strcat() since the strings are so short that strcat()
// is unlikely to be preempted while it's copying the strings. So we'll yield in the
// middle of copying to force another thread to be scheduled.
void my_strcat(char* dest, const char* src)
{
	int srclen = strlen(src);
	int destlen = strlen(dest);
	int i, j;

	for(i = 0, j = destlen; i < srclen; i++, j++)
	{
		sched_yield();
		dest[j] = src[i];
	}

	dest[j] = 0;
}
