//Graphics header file for the library.c file
#ifndef GRAPHICS_H_
#define GRAPHICS_H_

//RGB conversion macro
#define RGB(red, green, blue) (((0x001f & red) << 11) | ((0x003f & green) << 5) | (0x001f & blue))

typedef unsigned short color_t;

void init_graphics();
void exit_graphics();
char getkey();
void sleep_ms(long ms);
void clear_screen(void *img);
void draw_pixel(void *img, int x, int y, color_t color);
void draw_line(void *img, int xl, int yl, int width, int height, color_t c);
void *new_offscreen_buffer();
void blit(void *src);

#endif