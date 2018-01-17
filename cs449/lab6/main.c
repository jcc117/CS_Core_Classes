#include <stdio.h>
// This header contains the dlopen/dlsym/dlclose functions.
#include <dlfcn.h>
int main()
{
 void* handle;
 void (*my_str_copy)(char*, char*); // gross, a function pointer!
 handle = dlopen("mystr.so", RTLD_LAZY); // open the shared object
 if(handle == NULL)
 {
 printf("Couldn't load library! %s\n", dlerror());
 return 1;
 }

 my_str_copy = dlsym(handle, "my_strcpy"); // lookup the function by name
 if(my_str_copy == NULL)
 {
 printf("Couldn't load function! %s\n", dlerror());
 return 1;
 }
 // Let’s test it.
 char dest[100];
 my_str_copy(dest, "Hello World!");
 printf ("%s\n", dest);
 
 
 int (*my_str_len)(char*); //function pointer
 
 my_str_len = dlsym(handle, "my_strlen"); //lookup the function by name
 if(my_str_len == NULL)
 {
	printf("Couldn't load function! %s\n", dlerror());
	return 1;
 }
 
 //Test the function
 char string[100] = "Let's go Pens!";
 printf("%s\n", string);
 int len = my_str_len(string);
 printf("Length: %d\n", len);
 dlclose(handle);

 return 0;
}