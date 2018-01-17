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
#include <signal.h>

//Jordan Carr
//Assignment 1 test client

//Signal handler, allows the program to exit properly
void sig_handler(int signum)
{
	exit_graphics();
	exit(signum);
}

int main()
{
	//Handle signal so key buffering and echos can be reenabled
	signal(SIGINT, sig_handler);
	
	//Initialize the graphics library
	init_graphics();
	char key;
	
	void* img = new_offscreen_buffer();
	
	//Color macro tests
	color_t color = RGB(0x11, 0x1d, 0x1c);	//Light blue
	color_t color2 = RGB(0X02, 0x38, 0x00);	//Light green
	color_t yellow = RGB(0x1f, 0x3f, 0);	//Yellow
	color_t purple = RGB(0x1f, 0, 0x1f);
	color_t red = RGB(0x1f, 0, 0);
	int w = 0;
	int z = 0;
	//Test draw_pixel function and its error checking (will go out of bounds but won't cause the client to crash)
	while(w < 1000)
	{
		z = 0;
		while(z < 1000)
		{
			draw_pixel(img, w, z, color2);
			z++;
		}
		w++;
	}
	blit(img);
	sleep_ms(1);
	
	//Wait for key presses
	while(1)
	{
		key = getkey();
		//Exit the program on 'q'
		if(key == 'q')
		{
			exit_graphics();
			return 0;
		}
		//Clear the screen
		else if(key == 'c')
		{
			clear_screen(img);
			sleep_ms(500);
		}
		//Draw an red x - for testing negative slopes - with green background
		else if(key == 'x')
		{
			clear_screen(img);
			w = 0;
			while(w < 1000)
			{
				z = 0;
				while(z < 1000)
				{
					draw_pixel(img, w, z, color2);
					z++;
				}
				w++;
			}
			draw_line(img, 200, 200, 300, 300, red);
			draw_line(img, 200, 300, 300, 200, red);
			//draw_line(img, 100, 400, 50, 150, color);
			blit(img);
			sleep_ms(500);
		}
		//Draw a not good looking yellow triangle with purple background
		else if(key == 't')
		{
			clear_screen(img);
			w = 0;
			while(w < 1000)
			{
				z = 0;
				while(z < 1000)
				{
					draw_pixel(img, w, z, purple);
					z++;
				}
				w++;
			}
			draw_line(img, 100, 100, 300, 300, yellow);
			draw_line(img, 100, 300, 300, 100, yellow);
			draw_line(img, 300, 100, 300, 300, yellow);
			blit(img);
			sleep_ms(500);
		}
		//Draw a blue square with orange-ish background
		else if(key == 's')
		{
			clear_screen(img);
			w = 0;
			while(w < 1000)
			{
				z = 0;
				while(z < 1000)
				{
					draw_pixel(img, w, z, RGB(0x1f, 0x10, 0x00));
					z++;
				}
				w++;
			}
			draw_line(img, 100, 100, 100, 200, color);
			draw_line(img, 100, 100, 200, 100, color);
			draw_line(img, 100, 200, 200, 200, color);
			draw_line(img, 200, 100, 200, 200, color);
			blit(img);
			sleep_ms(500);
		}
	}
	return 0;
}