#include<stdio.h>

/*This program will take a jpg file as input on the command, read the file as a binary file
and print information about image. The image must be of the right format for it to work properly.
The image must be little endian format. Any other errors will be printed by program.*/
typedef struct
{
	unsigned short identifier;
	unsigned short type;
	int itemCount;
	unsigned int offset;
}TIFF;

typedef struct
{
	unsigned short start;
	unsigned short app1;
	short length;
	char exif[4];
	short term;
	char endianness[2];
	short version;
	int offset;
}Jpeg;


const unsigned int jpgMarker = 0xd8ff;
const unsigned int appiMarker = 0xe1ff;
const char exifarray[] = {'E', 'x', 'i', 'f',};
const int exifOffset = 12;

int main(int argc, char** argv)
{
	/*Open the file*/
	FILE* f = fopen(argv[1], "rb");
	if(f == NULL)
	{
		printf("That file doesn't exist!\n");
		return 1;
	}
	
	Jpeg image;
	fread(&image, sizeof(Jpeg), 1, f);
	
	/*Make sure the file is a jpeg file*/
	if (image.start == jpgMarker)
	{
		/*Make sure it's the correct endianness*/
		if (image.endianness[0] != 'I')
		{
			printf("We do not support this type: Wrong Endianness\n");
			return 1;
		}
		
		/*Make sure Exif is in the correct place*/
		int k;
		int flag = 0;
		int size = sizeof(exifarray);
		for(k = 0; k < size; k++)
		{
			if(exifarray[k] != image.exif[k])
			{
				flag = 1;
				break;
			}
		}
		if(flag != 0)
		{
			printf("We do not support this type: Wrong Exif Tag\n");
			return 1;
		}
		
		unsigned short count;
		fread(&count, sizeof(count), 1, f);
		
		/*Find the 0x010f, 0x0110, and 0x8769 Tiff tags*/
		int j;
		for(j = 0; j < count; j++)
		{
			TIFF temp;
			fread(&temp, sizeof(TIFF), 1, f);
			long pos = ftell(f);
			int foundExtra = 0;
			switch (temp.identifier)
			{
				/*Find camera manufacturer*/
				case 0x10f:
				{
					fseek(f, (exifOffset + temp.offset), SEEK_SET);
					unsigned char manu[temp.itemCount];
					fread(&manu, sizeof(manu), 1, f);
					printf("Manufacturer:\t\t%s\n", manu);
					fseek(f, pos, SEEK_SET);
					break;
				}
				/*Find camera model*/
				case 0x110:
				{
					fseek(f, (exifOffset + temp.offset), SEEK_SET);
					unsigned char model[temp.itemCount];
					fread(&model, sizeof(model), 1, f);
					printf("Model:\t\t\t%s\n", model);
					fseek(f, pos, SEEK_SET);
					break;
				}
				case 0x8769:
				{
					fseek(f, (exifOffset + temp.offset), SEEK_SET);
					foundExtra = 1;
					break;
				}
			}
			/*Once the 0x8769 tag is found, the loop stops and jumps to the next set of TIFF tags*/
			if(foundExtra)
				break;
		}
		
		/*Read in the additional TIFF tags if they exist*/
		fread(&count, sizeof(count), 1, f);
		for(j = 0; j < count; j++ )
		{
			TIFF temp2;
			fread(&temp2, sizeof(TIFF), 1, f);
			long pos = ftell(f);
			switch (temp2.identifier)
			{
				/*Find width*/
				case 0xa002:
				{
					printf("Width:\t\t\t%d pixels\n", temp2.offset);
					break;
				}
				/*Find height*/
				case 0xa003:
				{
					printf("Height:\t\t\t%d pixels\n", temp2.offset);
					break;
				}
				/*Find ISO speed*/
				case 0x8827:
				{
					printf("ISO:\t\t\tISO %d\n", temp2.offset);
					break;
				}
				/*Find exposure speed*/
				case 0x829a:
				{
					fseek(f, (temp2.offset + exifOffset), SEEK_SET);
					unsigned int exposure[2];
					fread(&exposure, sizeof(exposure), 1, f);
					printf("Exposure Time:\t\t%d/%d second\n", exposure[0], exposure[1]);
					fseek(f, pos, SEEK_SET);
					break;
				}
				/*Find F-stop*/
				case 0x829d:
				{
					/*Ask what he wants for this*/
					fseek(f, (temp2.offset + exifOffset), SEEK_SET);
					unsigned int fstop[2];
					fread(&fstop, sizeof(fstop), 1, f);
					double stop = (double)fstop[0]/fstop[1];
					printf("F-stop:\t\t\tf/%.1f\n", stop);
					fseek(f, pos, SEEK_SET);
					break;
				}
				/*Find focal length*/
				case 0x920a:
				{
					/*Ask what he wants for this*/
					fseek(f, (temp2.offset + exifOffset), SEEK_SET);
					unsigned int focal[2];
					fread(&focal, sizeof(focal), 1, f);
					printf("Focal Length:\t\t%d mm\n", (focal[0]/focal[1]));
					fseek(f, pos, SEEK_SET);
					break;
				}
				/*Find date taken*/
				case 0x9003:
				{
					fseek(f, (temp2.offset + exifOffset), SEEK_SET);
					unsigned char date[temp2.itemCount];
					fread(&date, sizeof(date), 1, f);
					printf("Date Taken:\t\t%s\n", date);
					fseek(f, pos, SEEK_SET);
					break;
				}
			}
		}
		
	}
	
	else
		printf("This file is not a jpeg file\n");
	
	fclose(f);
	return 0;
}
