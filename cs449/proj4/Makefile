devbuild: myshell.c
	gcc -g -Wall -Werror -o myshell myshell.c
	gcc -g -Wall -Werror -o testfile testfile.c
finalbuild: myshell.c
	gcc -03 -Wall -Werror -o myshell myshell.c
	strip -o strippedshell myshell
clean:
	rm -f myshell
	rm -f strippedshell
