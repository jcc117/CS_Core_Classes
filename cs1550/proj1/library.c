#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <time.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <linux/fb.h>
#include "graphics.h"
#include <termios.h>
#include <sys/select.h>
#include <sys/types.h>

//Jordan Carr
//cs1550 Assignment 1

int fileDes;
void* mapDes;
void* map;
const char* file = "/dev/fb0";
int size;
int columns;
int rows;

//Initialize the graphics library
void init_graphics()
{
	fileDes = open(file, O_RDWR);
	
	//Check for errors
	if(fileDes < 0)
	{
		//write(1, &errno, sizeof(errno));
		exit(-1);
	}
	//Get Screen information
	struct fb_var_screeninfo v;
	
	int vscreen = ioctl(fileDes, FBIOGET_VSCREENINFO, &v);
	//Check for syscall error
	if(vscreen < 0)
	{
		//write(1, &errno, sizeof(errno));
		exit(-1);
	}
	
	struct fb_fix_screeninfo f;
	int fscreen = ioctl(fileDes, FBIOGET_FSCREENINFO, &f);
	//Check for syscall error
	if(fscreen < 0)
	{
		//write(1, &errno, sizeof(errno));
		exit(-1);
	}
	
	//Calculate the size of mempory mapping
	size = v.yres_virtual * f.line_length;
	columns = f.line_length/sizeof(short);
	rows = v.yres_virtual;
	
	//Make a memory mapping of the screen
	mapDes = mmap(NULL, size, PROT_WRITE | PROT_READ, MAP_SHARED, fileDes, 0);
	
	//Clear the terminal
	char* clear_term = "\033[2J";
	int wval = write(1, clear_term, sizeof(clear_term));
	//Check for syscall error
	if(wval < 0)
	{
		//write(1, &errno, sizeof(errno));
		exit(-1);
	}
	
	//Disable key press echo and keypress buffering
	struct termios y;
	int x = ioctl(1, TCGETS, &y);
	//Check for syscall error
	if(x < 0)
	{
		//write(1, &errno, sizeof(errno));
		exit(-1);
	}
	
	y.c_lflag &= ~(ICANON);
	y.c_lflag &= ~(ECHO);
	
	int z = ioctl(1, TCSETS, &y);
	//Check for syscall error
	if(z < 0)
	{
		//write(1, &errno, sizeof(errno));
		exit(-1);
	}
}

//Close the graphics library
void exit_graphics()
{
	//Reneable key press echo and keypress buffering
	struct termios y;
	int x = ioctl(1, TCGETS, &y);
	//Check for syscall error
	if(x < 0)
	{
		write(1, &errno, sizeof(errno));
		exit(-1);
	}
	
	y.c_lflag |= ICANON;
	y.c_lflag |= ECHO;
	
	int z = ioctl(1, TCSETS, &y);
	//Check for syscall error
	if(z < 0)
	{
		write(1, &errno, sizeof(errno));
		exit(-1);
	}
	
	//Close the file
	int val = close(fileDes);
	//Check for syscall error
	if(val < 0)
	{
		write(1, &errno, sizeof(errno));
		exit(-1);
	}
	
	//Unmap the memory
	int munval = munmap(mapDes, size);
	//Check for syscall error
	if(munval < 0)
	{
		write(1, &errno, sizeof(errno));
		exit(-1);
	}
}

//Get key pressed from the user
char getkey()
{
	//Check if the call will block
	fd_set readfds;
	FD_SET(0, &readfds);
	struct timeval timeout;
	timeout.tv_sec = 0;
	timeout.tv_usec = 0;
	int selVal = select(1, &readfds, NULL, NULL, &timeout);
	//Check for a syscall error
	if(selVal < 0)
	{
		//write(1, &errno, sizeof(errno));
		exit(-1);
	}
	//If there was a button pressed, read it and return the value
	else if(selVal >= 0)
	{
		char ret[1];
		int rVal = read(0, ret, sizeof(ret));
		//Check for syscall error
		if(rVal < 0)
		{
			//write(1, &errno, sizeof(errno));
			exit(-1);
		}
		return ret[0];
	}
	
	return 0;
}

//Make program sleep for specified interval ms between frames of graphics being drawn
void sleep_ms(long ms)
{
	long time = ms * 1000000;
	struct timespec req;
	req.tv_nsec = time;
	req.tv_sec = 0;
	int sVal = nanosleep(&req, NULL);
	if(sVal < 0)
	{
		exit(-1);
	}
}

//Clear the screen
void clear_screen(void *img)
{
	int i;
	//For the moment clears both the framebuffer and the copied buffer
	for(i = 0; i < size; i++)
	{
		((char*)img)[i]  =  0;
		((char*)mapDes)[i] = 0;
	}
}

//Draw a pixel on the screen
void draw_pixel(void *img, int x, int y, color_t color)
{
	if(x < rows && y < columns && x >= 0 && y >= 0)
		*((short*)img + x*columns + y) = color;
}

//Draw a line on the screen
//Source of modified algorithm: https://www.thecrazyprogrammer.com/2017/01/bresenhams-line-drawing-algorithm-c-c.html
void draw_line(void *img, int x1, int y1, int width, int height, color_t c)
{
	//Switch the coordinates if the value of x1 is greater than that of width
	if(x1 > width)
	{
		int temp = x1;
		x1 = width;
		width = temp;
		temp = y1;
		y1 = height;
		height = temp;
	}
		
	int dx, dy, p, x, y;
 
	dx = width - x1;
	dy = height - y1;
 
	x=x1;
	y=y1;
 
	p=2*dy-dx;
	//Calculate the slope of the line to determine its sign
	double slope;
	if(dx != 0)
		slope = dy/dx;
	else
		slope = 0.0;
	
	//Draw horizontal line
	if(x == width)
	{
		while((y - height) != 0)
		{
			draw_pixel(img, x, y, c);
			if(y > height)
				y--;
			else
				y++;
		}
	}
	//Draw positive sloped line
	else if(slope >= 0.0)
	{
		while(x<width)
		{
			if(p>=0)
			{
				//Check if in bounds
				draw_pixel(img, x, y, c);
				y=y+1;
				p=p+2*dy-2*dx;
			}
			else
			{
				//Check if in bounds
				draw_pixel(img, x, y, c);
				p=p+2*dy;
			}
			x=x+1;
		}
	}
	//Draw negative sloped line
	else if(slope < 0.0)
	{
		dy = y1 - height;
		while(x < width)
		{
			if(p>=0)
			{
				draw_pixel(img, x, y, c);
				y=y - 1;
				p=p+2*dy-2*dx;
			}
			else
			{
				draw_pixel(img, x, y, c);
				p=p+2*dy;
			}
			x=x+1;
		}
	}
}

//Create an offscreen buffer
void *new_offscreen_buffer()
{
	
	//void* buf;
	void* val = mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
	if(*((int*) val) < 0)
	{
		exit(-1);
	}
	return val;
}

//Copy the member from the offscreen buffer to the framebuffer
void blit(void *src)
{
	
	int i;
	
	for(i = 0; i < size; i++)
	{
		((char*)mapDes)[i]  =  ((char*)src)[i];
	}
}
