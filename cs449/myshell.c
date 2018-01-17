//Jordan Carr
//jcc117

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>

void print_dirs(char** list, int index);
void add_to_dir_stack(char** list, int index);
void get_tokens(char** token_list, char* buffer, char* token_delim);
void exit_myshell(char** token_list);
void push_to_stack(char** token_list, int* stack_index, char** dir_stack);
void pop_from_stack(char** token_list, int* stack_index, char** dir_stack);
void load_args(char** token_list, char** arg_list);
void redirect_output(char** token_list, int* output_index);
void redirect_input(char** token_list, int* input_index);
void search_in_out(char** token_list, int* input_flag, int* output_flag, int* input_index, int* output_index);
int main()
{
	//Ignore Control C
	signal(SIGINT, SIG_IGN);
	char buffer[500];
	int exit_shell = 1;
	char* token_delim = " \t\n()|&;";
	char* exit_command = "exit";
	char* cd_command = "cd";
	char* pop_command = "popd";
	char* push_command = "pushd";
	char* token_list[30];
	char* dir_stack[4];
	int* stack_index = malloc(sizeof(int*));
	*stack_index = 0;
	while(exit_shell)
	{
		//Get command line input
		printf("myshell>");
		fgets(buffer, sizeof(buffer), stdin);
		
		//Tokenize the input
		get_tokens(token_list, buffer, token_delim);
		//Make sure the user entered something
		if(token_list[0] != NULL)
		{
			//If the token is an exit command
			if(strcmp(token_list[0], exit_command) == 0)
			{
				exit_myshell(token_list);
			}
			//If the token is a cd command
			else if(strcmp(token_list[0], cd_command) == 0)
			{
				if(token_list[1] != NULL)
				{
					int ret;
					ret = chdir(token_list[1]);
					if(ret < 0)
					{
						int error = errno;
						printf("Error: %s\n", strerror(error));
					}
				}
			}
			//If the token is pushd
			else if(strcmp(token_list[0], push_command) == 0)
			{
				push_to_stack(token_list, stack_index, dir_stack);
			}
			//If the token is popd
			else if(strcmp(token_list[0], pop_command) == 0)
			{
				pop_from_stack(token_list, stack_index, dir_stack);
			}
			//Else, run the command with fork
			else
			{
				//Child
				if(fork() == 0)
				{
					signal(SIGINT, SIG_DFL);
					// This will execute the program whose name is in the first
					// command-line argument, and whose arguments are after it.
					
					//Check for input and output redirection in the tokens
					int* input_flag = malloc(sizeof(int*));
					*input_flag = 0;
					int* output_flag = malloc(sizeof(int*));
					*output_flag = 0;
					int* output_index = malloc(sizeof(int*));
					int* input_index = malloc(sizeof(int*));
					*output_index = 0;
					*input_index = 0;
					search_in_out(token_list, input_flag, output_flag, input_index, output_index);
					//Check for output redirection
					if(*output_flag)
					{
						redirect_output(token_list, output_index);
					}
					//Check for input redirection
					if(*input_flag)
					{
						redirect_input(token_list, input_index);
					}
					//Set the arguments for the process so it won't include <, >, or their respective arguments
					char* arg_list[30] = { NULL };
					load_args(token_list, arg_list);
					
					int res = execvp(token_list[0], &arg_list[0]);
					
					//catch execvp if the result fails
					//NEVER EVER DELETE THIS EVER
					//IF YOU DO, YOU WILL FORK BOMB!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					if(res < 0)
					{
						//printf("couldn't run code: ");
						perror("Error");
						exit(-1);
					}
				}
				//Parent
				else
				{
					// This waits for a child to exit in some way. *How* it exited
					// will be put in the stat variable.
					int stat;
					int childpid = waitpid(-1, &stat, 0);
					
					//Print how the child exited
					//Some are for debugging purposes
					if(childpid < 0)
						perror("Couldn't run program");
					//else if(WIFEXITED(stat) && WEXITSTATUS(stat) == 0)
						//printf("exited successfully\n");
					else if(WIFSIGNALED(stat))
						printf("terminated due to signal %s\n", strsignal(WTERMSIG(stat)));
					//else if(WIFEXITED(stat) && WEXITSTATUS(stat) != 0)
						//printf("exited with code %d\n", WEXITSTATUS(stat));
					//else
						//printf("terminated some other way!\n");
				}
			}	
		}
	}
	return 0;
	
}

void print_dirs(char** list, int index)
{
	index--;
	while(index >= 0)
	{
		printf("%s ", list[index]);
		index--;
	}
	printf("\n");
}

void add_to_dir_stack(char** list, int index)
{
	list[index] = getcwd(NULL, 0);
	/*if(cwd == NULL)
	{
		perror("Error");
	}*/
	//printf("%s\n", list[index]);
}
//Process the incoming tokens
void get_tokens(char** token_list, char* buffer, char* token_delim)
{
	//Get first token of the input
		token_list[0] = strtok(buffer, token_delim);
		
		//Get other tokens
		int i = 0;
		while(token_list[i] != NULL)
		{
			i++;
			token_list[i] = strtok(NULL, token_delim);
		}
}
//Exit the shell
void exit_myshell(char** token_list)
{
	//If there are none, exit as normal
	if(token_list[1] == NULL)
		exit(0);
	//else, return the given argument (an int) to bash
	else
	{
		int val = atoi(token_list[1]);
		exit(val);
	}
}
//Push entry to the dir stack
void push_to_stack(char** token_list, int* stack_index, char** dir_stack)
{
	if(*stack_index == 4)
	{
		printf("Error: stack is full\n");
	}
	else
	{
		add_to_dir_stack(dir_stack, *stack_index);
		*stack_index = *stack_index + 1;
		int ret = chdir(token_list[1]);
		if(ret < 0)
		{
			perror("pushd Error");
			*stack_index = *stack_index - 1;
			dir_stack[*stack_index] = NULL;
		}
		else
		{
			print_dirs(dir_stack, *stack_index);
		}
					
	}
}
//Pop entry from the dir stack
void pop_from_stack(char** token_list, int* stack_index, char** dir_stack)
{
	if(*stack_index == 0)
	{
		printf("Error: stack is empty\n");
	}
	else
	{
		int ret = chdir(dir_stack[*stack_index - 1]);
		if(ret < 0)
		{
			perror("popd Error");
		}
		else
		{
			*stack_index = *stack_index - 1;
			free(dir_stack[*stack_index]);
			dir_stack[*stack_index] = NULL;
			print_dirs(dir_stack, *stack_index);	
		}
	}
}
//load arguments for the fuction to run
void load_args(char** token_list, char** arg_list)
{
	arg_list[0] = token_list[0];
	int i = 1;
	int j = 1;
	char* string1 = ">";
	char* string2 = "<";
	while(token_list[i] != NULL)
	{
		//Check its not the command itself
		if(i != 0)
		{
			//Check it is not an output token
			if(strcmp(token_list[i], string1) != 0)
			{
				//Check it is not an output arg
				if(strcmp(token_list[i-1], string1) != 0)
				{
					//Check it is not an input token
					if(strcmp(token_list[i], string2) != 0)
					{
						//Check it is not an input arg
						if(strcmp(token_list[i-1], string2) != 0)
						{
							arg_list[j] = token_list[i];
							j++;
						}
					}
				}
			}
		}
		i++;
	}
}
//Redirects output for a process
void redirect_output(char** token_list, int* output_index)
{
	//Open the file, if one does not exist create one
	int fd_out = open(token_list[*output_index], O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
	if(fd_out < 0)
	{
		perror("Output Error");
		exit(-1);
	}
	//Redirect output
	int val_out = dup2(fd_out, 1);
	if(val_out < 0)
	{
		perror("Output Error");
		exit(-1);
	}
	int val = close(fd_out);
	if(val < 0)
	{
		perror("Output Error");
		exit(-1);
	}
}
//Redirects input for a process
void redirect_input(char** token_list, int* input_index)
{
	int fd_in = open(token_list[*input_index], O_RDONLY);
	if(fd_in < 0)
	{
		perror("Input Error");
		exit(-1);
	}
	//Redirect input
	int val_in = dup2(fd_in, 0);
	if(val_in < 0)
	{
		perror("Input Error");
		exit(-1);
	}
	//Close fd
	int val = close(fd_in);
	if(val < 0)
	{
		perror("Input Error");
		exit(-1);
	}
}
void search_in_out(char** token_list, int* input_flag, int* output_flag, int* input_index, int* output_index)
{
	int j = 0;
	char input[] = "<";
	char output[] = ">";
	while(token_list[j] != 0)
	{
		//Check for input redirection
		if((strcmp(token_list[j], input) == 0) && !*input_flag)
		{
			//Check that it is not the first token
			if(j == 0)
			{
				printf("Error: invalid input token\n");
				exit(-1);
			}
			else
			{
				*input_flag = 1;
				*input_index = j + 1;
			}
		}
		//Check there is not more than one input redirection
		else if((strcmp(token_list[j], input) == 0) && *input_flag)
		{
			printf("Error: more than 1 input\n");
			exit(-1);
		}
	
		//Check for output redirection
		else if((strcmp(token_list[j], output) == 0) && !*output_flag)
		{
			if(j == 0)
			{
				printf("Error: invalid output token\n");
				exit(-1);
			}
			else
			{
				*output_flag = 1;
				*output_index = j + 1;
			}
		}
		//Check there is not more than one output redirection
		else if((strcmp(token_list[j], output) == 0) && *output_flag)
		{
			printf("Error: more than 1 output redirection\n");
			exit(-1);
		}
		j++;
	}
}
