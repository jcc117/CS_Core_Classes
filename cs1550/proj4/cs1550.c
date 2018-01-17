/*
	FUSE: Filesystem in Userspace
	Copyright (C) 2001-2007  Miklos Szeredi <miklos@szeredi.hu>

	This program can be distributed under the terms of the GNU GPL.
	See the file COPYING.
*/

/*
	Jordan Carr
	jcc117
	CS1550
	Project 4
*/

#define	FUSE_USE_VERSION 26

#include <fuse.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>

//size of a disk block
#define	BLOCK_SIZE 512

//size of free refrencing block
#define FREE_STRUCTURE_SIZE (512 * 3)
#define NUM_BLOCKS (10240 - 4)

//we'll use 8.3 filenames
#define	MAX_FILENAME 8
#define	MAX_EXTENSION 3

//How many files can there be in one directory?
#define MAX_FILES_IN_DIR (BLOCK_SIZE - sizeof(int)) / ((MAX_FILENAME + 1) + (MAX_EXTENSION + 1) + sizeof(size_t) + sizeof(long))

//The attribute packed means to not align these things
struct cs1550_directory_entry
{
	int nFiles;	//How many files are in this directory.
				//Needs to be less than MAX_FILES_IN_DIR

	struct cs1550_file_directory
	{
		char fname[MAX_FILENAME + 1];	//filename (plus space for nul)
		char fext[MAX_EXTENSION + 1];	//extension (plus space for nul)
		size_t fsize;					//file size
		long nStartBlock;				//where the first block is on disk
	} __attribute__((packed)) files[MAX_FILES_IN_DIR];	//There is an array of these

	//This is some space to get this to be exactly the size of the disk block.
	//Don't use it for anything.  
	char padding[BLOCK_SIZE - MAX_FILES_IN_DIR * sizeof(struct cs1550_file_directory) - sizeof(int)];
} ;

typedef struct cs1550_root_directory cs1550_root_directory;

#define MAX_DIRS_IN_ROOT (BLOCK_SIZE - sizeof(int)) / ((MAX_FILENAME + 1) + sizeof(long))

struct cs1550_root_directory
{
	int nDirectories;	//How many subdirectories are in the root
						//Needs to be less than MAX_DIRS_IN_ROOT
	struct cs1550_directory
	{
		char dname[MAX_FILENAME + 1];	//directory name (plus space for nul)
		long nStartBlock;				//where the directory block is on disk
	} __attribute__((packed)) directories[MAX_DIRS_IN_ROOT];	//There is an array of these

	//This is some space to get this to be exactly the size of the disk block.
	//Don't use it for anything.  
	char padding[BLOCK_SIZE - MAX_DIRS_IN_ROOT * sizeof(struct cs1550_directory) - sizeof(int)];
} ;


typedef struct cs1550_directory_entry cs1550_directory_entry;

//How much data can one block hold?
#define	MAX_DATA_IN_BLOCK (BLOCK_SIZE - sizeof(long))

struct cs1550_disk_block
{
	//The next disk block, if needed. This is the next pointer in the linked 
	//allocation list
	long nNextBlock;

	//And all the rest of the space in the block can be used for actual data
	//storage.
	char data[MAX_DATA_IN_BLOCK];
};

typedef struct cs1550_disk_block cs1550_disk_block;

//Ceiling function used to calculate how many blocks a file uses
static int ceiling(double num)
{
	int inum = (int)num;
	if(num == (double)inum)
		return inum;
	return inum + 1;
}

static char get_bit(char* bitmap, int n)
{
	char bit = bitmap[n/8] & (1 << n%8);
	return bit != 0;
	//return bitmap[n];
}

//Find a free space to use in the bit map
//Return the index of the block when found
//Return -1 if not found
//Needs fixed
static int find_free_block(char* bitmap)
{
	int found = 0;
	int i;
	//int j;
	//char thisByte;
	//(3 * block_size * 8)
	for(i = 1; i < (FREE_STRUCTURE_SIZE); i++)
	{
		//New implementatin
		char bit = get_bit(bitmap, i);
		//printf("Bit %d: %d\n", i, bit);
		if(bit == 0)
		{
			found = 1;
			break;
		}

	}
	if(found)
	{
		//Take into account that root is taken up, so add 1
		//return i*8 + (j + 1);
		//printf("Free block is: %d\n", i);
		return i;
	}
	else
		return -1;
}

//Write a block as not free to the bit map
static void write_to_map(char* bitmap, int index)
{
	//Find which bit to go to
	//index--;
	bitmap[index / 8] |= (1 << (index % 8));
	//bitmap[index] = 1;
	//printf("Written to %d\n", bitmap[index]);
}


/*
 * Called whenever the system wants to know the file attributes, including
 * simply whether the file exists or not. 
 *
 * man -s 2 stat will show the fields of a stat structure
 */
static int cs1550_getattr(const char *path, struct stat *stbuf)
{
	int res = 0;

	memset(stbuf, 0, sizeof(struct stat));
   
	//is path the root dir?
	if (strcmp(path, "/") == 0) {
		stbuf->st_mode = S_IFDIR | 0755;
		stbuf->st_nlink = 2;
		stbuf->st_size = BLOCK_SIZE;
		stbuf->st_blksize = BLOCK_SIZE;
		stbuf->st_blocks = 1;
	} else {

	//Check if name is subdirectory

	//Read in the root from disk
		FILE *file = fopen(".disk", "rb");
		cs1550_root_directory root;
		fread(&root, sizeof(root), 1, file);

		//printf("num dirs- %d\n", root.nDirectories);

		//Check subdirectories actually exist
		if(root.nDirectories == 0)
		{
			fclose(file);
			return -ENOENT;
		}

		//Parse the path to get the name of the subdirectory
		//printf("%s\n", path);
		char subname[MAX_FILENAME];
		char filename[MAX_FILENAME];
		char ext[MAX_EXTENSION];
		int num = sscanf(path, "/%[^/]/%[^.].%s", subname, filename, ext);
		//printf("num scanned: %d\n", num);

	//Check if the subdirectory exists
		int found = 0;
		int i;
		for(i = 0; i < root.nDirectories; i++)
		{
			//printf("%s, %s\n", subname, root.directories[i].dname);
			if(strcmp(root.directories[i].dname, subname) == 0)
			{
				found = 1;
				break;
			}
		}
		//Check if the subdirectory was found
		if(found)
		{
			//If filename is null, no file was specified in the path and what is requested is a directory
			if(num == 1)
			{
				//This block is originally commented out
				//Might want to return a structure with these fields
				//printf("I'm a directory that exists\n");
				stbuf->st_mode = S_IFDIR | 0755;
				stbuf->st_nlink = 2;
				stbuf->st_blksize = BLOCK_SIZE;
				stbuf->st_blocks = 1;
				stbuf->st_size = BLOCK_SIZE;
				res = 0; //no error
			}
			else
			{
				//printf("I'm a file\n");

				//Look up the files in this directory to see if the file exists
				fseek(file, root.directories[i].nStartBlock, SEEK_SET);
				cs1550_directory_entry dir;
				fread(&dir, sizeof(cs1550_directory_entry), 1, file);

				//Make sure the directory has files
				if(dir.nFiles == 0 || ext == NULL)
				{
					fclose(file);
					return -ENOENT;
				}

				int j;
				found = 0;
				for(j = 0; j < dir.nFiles; j++)
				{
					if(strcmp(dir.files[j].fname, filename) == 0 && strcmp(dir.files[j].fext, ext) == 0)
					{
						found = 1;
						break;
					}
				}

				if(found)
				{
					//This block is originally commented out
					//Check if name is a regular file
					//regular file, probably want to be read and write
					//printf("I'm a file that exists\n");
					stbuf->st_mode = S_IFREG | 0666; 
					stbuf->st_nlink = 1; //file links
					stbuf->st_size = dir.files[j].fsize; //file size - make sure you replace with real size!
					stbuf->st_blocks = ceiling((double)dir.files[j].fsize/(double)MAX_DATA_IN_BLOCK);	//Round up the number of blocks needed
					stbuf->st_blksize = BLOCK_SIZE;
					res = 0; // no error
				}
				else{
					fclose(file);
					return -ENOENT;
				}
			}
		}
		else
		{
			//Else return that path doesn't exist
			res = -ENOENT;
		}
		fclose(file);
	}
	return res;
}

/* 
 * Called whenever the contents of a directory are desired. Could be from an 'ls'
 * or could even be when a user hits TAB to do autocompletion
 */
static int cs1550_readdir(const char *path, void *buf, fuse_fill_dir_t filler,
			 off_t offset, struct fuse_file_info *fi)
{
	//Since we're building with -Wall (all warnings reported) we need
	//to "use" every parameter, so let's just cast them to void to
	//satisfy the compiler
	(void) offset;
	(void) fi;

	FILE *file = fopen(".disk", "rb");

	//Read in the root to get the subdirectories
	cs1550_root_directory root;
	fread(&root, sizeof(root), 1, file);

	//This line assumes we have no subdirectories, need to change
	if (strcmp(path, "/") == 0)
	{
		//return -ENOENT;
		int i;
		for(i = 0; i < root.nDirectories; i++)
		{
			filler(buf, root.directories[i].dname, NULL, 0);
		}
	}
	else
	{
		//Check the path to see if its a directory
		char directory[MAX_FILENAME];
		char fname[MAX_FILENAME];
		char extension[MAX_EXTENSION];
		int num = sscanf(path, "/%[^/]/%[^.].%s", directory, fname, extension);

		//Indicates the path is a file
		if(num > 1)
		{
			fclose(file);
			return -ENOENT;
		}
		else
		{
			//Make sure the subdirectory exists
			int found = 0;
			int i;
			for(i = 0; i < root.nDirectories; i++)
			{
				if(strcmp(directory, root.directories[i].dname) == 0)
				{
					found = 1;
					break;
				}
			}
			if(found)
			{
				//Go to that specific block and get its directories
				fseek(file, root.directories[i].nStartBlock, SEEK_SET);
				cs1550_directory_entry dir;
				fread(&dir, sizeof(dir), 1, file);

				//Put the contents of the directory in the list
				int j;
				for(j = 0; j < dir.nFiles; j++)
				{
					char* filename = dir.files[j].fname;
					strcat(filename, ".");
					strcat(filename, dir.files[j].fext);
					filler(buf, filename, NULL, 0);
				}
			}
			else
			{
				fclose(file);
				return -ENOENT;
			}
		}
	}
	//the filler function allows us to add entries to the listing
	//read the fuse.h file for a description (in the ../include dir)
	filler(buf, ".", NULL, 0);
	filler(buf, "..", NULL, 0);

	/*
	//add the user stuff (subdirs or files)
	//the +1 skips the leading '/' on the filenames
	filler(buf, newpath + 1, NULL, 0);
	*/
	fclose(file);
	return 0;
}

/* 
 * Creates a directory. We can ignore mode since we're not dealing with
 * permissions, as long as getattr returns appropriate ones for us.
 */
static int cs1550_mkdir(const char *path, mode_t mode)
{
	//(void) path;
	(void) mode;

	//printf("%s\n", path);
	
	//Check the length of its name
	char directory[MAX_FILENAME + 1];
	char fname[MAX_FILENAME + 1];
	char extension[MAX_EXTENSION + 1];

	int num = sscanf(path, "/%[^/]/%[^.].%s", directory, fname, extension);
	//printf("%s, %s, %s\n", directory, fname, extension);

	if(num > 1)
		return -EPERM;
	if(strlen(directory) > MAX_FILENAME)
		return -ENAMETOOLONG;

	//printf("Not too long, able to be made\n");

	FILE *file = fopen(".disk", "rb");
	cs1550_root_directory root;
	fread(&root, sizeof(root), 1, file);
	char bitmap[FREE_STRUCTURE_SIZE];
	fseek(file, -FREE_STRUCTURE_SIZE, SEEK_END);
	fread(bitmap, 1, FREE_STRUCTURE_SIZE, file);
	/*unsigned int freeNum;
	fseek(file, sizeof(unsigned int) + 1, SEEK_END);*/
	//fread(&freeNum, sizeof(freeNum), 1, file);
	fclose(file);

	//Maker sure this directory doesn't already exist
	int i;
	for(i = 0; i < root.nDirectories; i++)
	{
		//printf("%s, %s\n", directory, root.directories[i].dname);

		if(strcmp(directory, root.directories[i].dname) == 0)
			return -EEXIST;
	}

	//printf("Doesn't already exist\n");

	//Make the new directory in root
	if(root.nDirectories == MAX_DIRS_IN_ROOT)
		return -ENOSPC;

	//int startBlock;
	int startBlock = find_free_block(bitmap);
	if(startBlock == -1)
		return -ENOSPC;
	else
	{
		//printf("Write block before write is: %d\n", bitmap[startBlock]);
		write_to_map(bitmap, startBlock);
		//printf("Write block is: %d\n", bitmap[startBlock]);
		//printf("%d\n", startBlock);
		startBlock *= BLOCK_SIZE;
	}

	//Set up the directory in the root directory
	//root.directories[root.nDirectories].nStartBlock = (BLOCK_SIZE * (root.nDirectories + 1));
	root.directories[root.nDirectories].nStartBlock = startBlock;

	int k;
	for(k = 0; k < MAX_FILENAME + 1; k++)
	{
		root.directories[root.nDirectories].dname[k] = directory[k];
		if(directory[k] == '\0')
			break;
	}
	root.nDirectories++;

	cs1550_directory_entry entry = {.nFiles = 0};

	//Set up the directory on disk
	FILE *wFile = fopen(".disk", "rb+");
	fwrite(&root, sizeof(root), 1, wFile);
	//fseek(wFile, root.directories[root.nDirectories - 1].nStartBlock, SEEK_SET);
	fseek(wFile, startBlock, SEEK_SET);
	fwrite(&entry, sizeof(entry), 1, wFile);

	//Rewrite the Bit map
	fseek(wFile, -FREE_STRUCTURE_SIZE, SEEK_END);
	fwrite(bitmap, sizeof(char), FREE_STRUCTURE_SIZE, wFile);
	//fseek(wFile, sizeof(int) + 1, SEEK_END);
	//fwrite(&freeNum, sizeof(freeNum), 1, wFile);

	//Close file
	fclose(wFile);

	//printf("Everything set up\n");

	return 0;
}

/* 
 * Removes a directory.
 */
static int cs1550_rmdir(const char *path)
{
	(void) path;
    return 0;
}

/* 
 * Does the actual creation of a file. Mode and dev can be ignored.
 *
 */
static int cs1550_mknod(const char *path, mode_t mode, dev_t dev)
{
	(void) mode;
	(void) dev;

	//Check the path
	char directory[MAX_FILENAME + 1];
	char filename[MAX_FILENAME + 1];
	char extension[MAX_EXTENSION + 1];

	int num = sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);

	//Check the path isn't the root
	if(strcmp(path, "/") == 0)
	{
		return -EPERM;
	}

	//Read in the root
	FILE* file = fopen(".disk", "rb");
	cs1550_root_directory root;
	fread(&root, sizeof(root), 1, file);

	//Check the directory exists
	int found = 0;
	int i;
	for(i = 0; i < root.nDirectories; i++)
	{
		if(strcmp(root.directories[i].dname, directory) == 0)
		{
			found = 1;
			break;
		}
	}
	if(found)
	{
		//Check the file doesn't already exist
		fseek(file, root.directories[i].nStartBlock, SEEK_SET);
		cs1550_directory_entry dir;
		fread(&dir, sizeof(dir), 1, file);

		long dirBlock = root.directories[i].nStartBlock;
		//printf("dirBlock is: %ld\n", dirBlock); 

		for(i = 0; i < dir.nFiles; i++)
		{
			//Check for both the file name and the extension name
			//printf("File: %s, %s\n", dir.files[i].fname, filename);
			if(strcmp(dir.files[i].fname, filename) == 0 && strcmp(dir.files[i].fext, extension) == 0)
			{
				fclose(file);
				return -EEXIST;
			}
		}

		//Check the length of the file name
		if(strlen(filename) > MAX_FILENAME)
		{
			fclose(file);
			return -ENAMETOOLONG;
		}

		//Check the max number of files hasn't been hit
		if(dir.nFiles >= MAX_FILES_IN_DIR)
		{
			fclose(file);
			return -ENOSPC;
		}

		//Check for an extension?
		if(num == 2)
		{
			fclose(file);
			return -ENOENT;
		}

		//Check for length of the extension name
		if(strlen(extension) > MAX_EXTENSION)
		{
			fclose(file);
			return -ENAMETOOLONG;
		}

		//Look for a free block in memory: temporary
		//======================================================================================================================================
		int startBlock = 0;

		//Instead, create a bit map: 1280 bytes (2.5 blocks), round up to 3 blocks
		//Read in the bitmap
		char bitmap[FREE_STRUCTURE_SIZE];
		fseek(file, -FREE_STRUCTURE_SIZE, SEEK_END);
		fread(bitmap, FREE_STRUCTURE_SIZE, 1, file);

		startBlock = find_free_block(bitmap);

		if(startBlock == -1)
		{
			fclose(file);
			return -ENOSPC;	//Not enough room on disk to allocate the file
		}
		else
		{
			write_to_map(bitmap, startBlock);
			//printf("%d\n", startBlock);
			startBlock *= BLOCK_SIZE;
		}
		//======================================================================================================================================================
		
		//Set up the block info
		cs1550_disk_block newBlock = {.nNextBlock = EOF};

		//Set up the file in the directory
		dir.files[dir.nFiles].nStartBlock = startBlock;
		dir.files[dir.nFiles].fsize = 0;
		//Copy filename to file entry
		int j;
		for(j = 0; j < MAX_FILENAME + 1; j++)
		{
			dir.files[dir.nFiles].fname[j] = filename[j];
			if(filename[j] == '\0')
				break;
		}
		//Copy extension name to entry
		for(j = 0; j < MAX_EXTENSION + 1; j++)
		{
			dir.files[dir.nFiles].fext[j] = extension[j];
			if(extension[j] == '\0')
				break; 
		}
		dir.nFiles++;
		//printf("New Dir info\n");
		//printf("dir file name: %s\n", dir.files[dir.nFiles - 1].fname);
		//printf("dir file extension: %s\n", dir.files[dir.nFiles -1].fext);
		//printf("number of files: %d\n", dir.nFiles);
		//printf("file's startblock: %ld\n", dir.files[dir.nFiles -1].nStartBlock);
		//printf("Writing new block to: %d\n", startBlock);

		//Write the data to disk
		fclose(file);
		FILE* wFile = fopen(".disk", "rb+");
		fwrite(&root, sizeof(root), 1, wFile);	//Experiment: rewrite the root
		fseek(wFile, dirBlock, SEEK_SET);
		fwrite(&dir, sizeof(dir), 1, wFile);	//Write data to directory
		//printf("Size of dir: %ld\n", sizeof(dir));
		fseek(wFile, startBlock, SEEK_SET);
		fwrite(&newBlock, sizeof(newBlock), 1, wFile);	//Write new block to disk
		//printf("Size of newBlock: %ld\n", sizeof(newBlock));
		fseek(wFile, -FREE_STRUCTURE_SIZE, SEEK_END);
		fwrite(bitmap, FREE_STRUCTURE_SIZE, 1, wFile);	//Write bitmap to disk
		fclose(wFile);
	}
	else
	{
		fclose(file);
		return -ENOENT;
	}

	return 0;
}

/*
 * Deletes a file
 */
static int cs1550_unlink(const char *path)
{
    (void) path;

    return 0;
}

/* 
 * Read size bytes from file into buf starting from offset
 *
 */
static int cs1550_read(const char *path, char *buf, size_t size, off_t offset,
			  struct fuse_file_info *fi)
{
	(void) fi;
	//(void) path;
	//(void) buf;
	//(void) size;
	//(void) offset;

	//check to make sure path exists
	//check that size is > 0
	//check that offset is <= to the file size
	//read in data
	//set size and return, or error
	
	//check to make sure path exists
	char directory[MAX_FILENAME + 1];
	char filename[MAX_FILENAME + 1];
	char extension[MAX_EXTENSION + 1];

	int num = sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);

	//Check the path isn't the root
	if(strcmp(path, "/") == 0)
	{
		return -EISDIR;
	}

	//Read in the root
	FILE* file = fopen(".disk", "rb");
	cs1550_root_directory root;
	fread(&root, sizeof(root), 1, file);

	char bitmap[FREE_STRUCTURE_SIZE];
	fseek(file, -FREE_STRUCTURE_SIZE, SEEK_END);
	fread(bitmap, FREE_STRUCTURE_SIZE, 1, file);

	//Check the directory exists
	int found = 0;
	int i;
	for(i = 0; i < root.nDirectories; i++)
	{
		if(strcmp(root.directories[i].dname, directory) == 0)
		{
			found = 1;
			break;
		}
	}
	if(found)
	{
		//Check if the path is a directory
		if(num == 1)
		{
			fclose(file);
			return -EISDIR;
		}

		//Open the directory and check if the file is there
		fseek(file, root.directories[i].nStartBlock, SEEK_SET);
		cs1550_directory_entry dir;
		fread(&dir, sizeof(dir), 1, file);

		int found_file = 0; 

		for(i = 0; i < dir.nFiles; i++)
		{
			//Check for both the file name and the extension name
			//printf("File: %s, %s\n", dir.files[i].fname, filename);
			if(strcmp(dir.files[i].fname, filename) == 0 && strcmp(dir.files[i].fext, extension) == 0)
			{
				found_file = 1;
				break;
			}
		}

		if(!found_file)
		{
			fclose(file);
			return -ENOENT;
		}

		//Check size requested is greater than 0
		if(size <= 0)
		{
			fclose(file);
			return 0;
		}

		//Check offset is <= file size
		if(offset > dir.files[i].fsize)
		{
			fclose(file);
			return -EFBIG;
		}

		//Read in the data
		//Find the correct block to start from
		fseek(file, dir.files[i].nStartBlock, SEEK_SET);
		cs1550_disk_block readBlock;
		fread(&readBlock, sizeof(readBlock), 1, file);

		int numBlocks = 1;
		while((offset/(numBlocks * MAX_DATA_IN_BLOCK)) != 0)
		{
			fseek(file, readBlock.nNextBlock, SEEK_SET);
			fread(&readBlock, sizeof(readBlock), 1, file);
			numBlocks++;
		}

		//Go to the correct position in the block
		//fseek(file, (offset % BLOCK_SIZE), SEEK_CUR);

		//Begin reading in the data to buffer
		int buf_count = 0;
		int bl_count = offset;

		while(buf_count < size && buf_count < dir.files[i].fsize)
		{
			//Need to go to the next block
			if(bl_count % MAX_DATA_IN_BLOCK == 0 && bl_count != 0)
			{
				if(readBlock.nNextBlock == EOF)
					break;

				//Read in the next block
				fseek(file, readBlock.nNextBlock, SEEK_SET);
				fread(&readBlock, sizeof(readBlock), 1, file);

				bl_count = 0;
			}
			buf[buf_count] = readBlock.data[bl_count];
			buf_count++;
			bl_count++;
		} 

		fclose(file);
		return buf_count;

	}
	else
	{
		fclose(file);
		return -ENOENT;
	}

	size = 0;

	return size;
}

/* 
 * Write size bytes from buf into file starting from offset
 *
 */
static int cs1550_write(const char *path, const char *buf, size_t size, 
			  off_t offset, struct fuse_file_info *fi)
{
	(void) fi;

	//check to make sure path exists
	char directory[MAX_FILENAME + 1];
	char filename[MAX_FILENAME + 1];
	char extension[MAX_EXTENSION + 1];

	int num = sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);

	//Check the path isn't the root
	if(strcmp(path, "/") == 0)
	{
		return -EISDIR;
	}

	//Read in the root
	FILE* file = fopen(".disk", "rb");
	cs1550_root_directory root;
	fread(&root, sizeof(root), 1, file);

	char bitmap[FREE_STRUCTURE_SIZE];
	fseek(file, -FREE_STRUCTURE_SIZE, SEEK_END);
	fread(bitmap, FREE_STRUCTURE_SIZE, 1, file);

	//Check the directory exists
	int found = 0;
	int i;
	for(i = 0; i < root.nDirectories; i++)
	{
		if(strcmp(root.directories[i].dname, directory) == 0)
		{
			found = 1;
			break;
		}
	}
	if(found)
	{
		//Check the file path was formatted correctly
		if(num == 1)
		{
			fclose(file);
			return -EISDIR;
		}
		if(num == 2)
		{
			fclose(file);
			return -ENOENT;
		}

		//Check the file doesn't already exist
		fseek(file, root.directories[i].nStartBlock, SEEK_SET);
		cs1550_directory_entry dir;
		fread(&dir, sizeof(dir), 1, file);

		long dirBlock = root.directories[i].nStartBlock;

		int found_file = 0;
		for(i = 0; i < dir.nFiles; i++)
		{
			//Check for both the file name and the extension name
			if(strcmp(dir.files[i].fname, filename) == 0 && strcmp(dir.files[i].fext, extension) == 0)
			{
				found_file = 1;
				break;
			}
		}
		if(found_file)
		{
			//check that size is > 0
			if(size == 0)
			{
				fclose(file);
				return 0;
			}
			//check that offset is <= to the file size
			if(offset > dir.files[i].fsize)
			{
				fclose(file);
				return -EFBIG;
			}
			
			//Get the data to write to
			cs1550_disk_block fBlock;
			fseek(file, dir.files[i].nStartBlock, SEEK_SET);
			fread(&fBlock, sizeof(fBlock), 1, file);

			//Find the block where the offset is
			int numBlocks = 1;
			while(numBlocks * MAX_DATA_IN_BLOCK <= offset)
			{
				fseek(file, fBlock.nNextBlock, SEEK_SET);
				fread(&fBlock, sizeof(fBlock), 1, file);
				numBlocks++;
			}

			//Begin writing to disk using the buffer
			int j;
			int current_offset = offset % MAX_DATA_IN_BLOCK;
			//int add_blocks = 0;
			int add_bytes = dir.files[i].fsize;
			int curr_size = offset;
			long current_startBlock = dir.files[i].nStartBlock;
			for(j= 0; j < size; j++)
			{
				//Check you aren't going over on the block size- if you are, get another one
				if((current_offset % MAX_DATA_IN_BLOCK == 0) && current_offset != 0)
				{
					//Check if you need to append to the file or just get the next block
					if (fBlock.nNextBlock == EOF)
					{
						//Set up disk stuff
						fclose(file);
						FILE* wFile = fopen(".disk", "rb+");

						//Find another free block
						//If one is not available, return the ENOSPC
						int newBlock = find_free_block(bitmap);
						if(newBlock < 0)
							return -ENOSPC;
						else
						{
							//printf("Write block before write is: %d\n", bitmap[startBlock]);
							write_to_map(bitmap, newBlock);
							//printf("Write block is: %d\n", bitmap[startBlock]);
							//printf("%d\n", startBlock);
							newBlock *= BLOCK_SIZE;
						}
						//Set the current pointer of this block to the new block
						fBlock.nNextBlock = newBlock;
						//Write the current block to memory
						fseek(wFile, current_startBlock, SEEK_SET);
						fwrite(&fBlock, sizeof(fBlock), 1, wFile);


						//Set the current block to the new block
						current_offset = 0;
						current_startBlock = newBlock;

						cs1550_disk_block nextBlock = {.nNextBlock = EOF};
						fBlock = nextBlock;
						
						//Increment number of blocks added
						//add_blocks++;

						//Set up disk stuff
						fclose(wFile);
						file = fopen(".disk", "rb+");
					}
					else
					{
						//Get the next block in the list
						cs1550_disk_block nextBlock;
						fseek(file, fBlock.nNextBlock, SEEK_SET);
						fread(&nextBlock, sizeof(nextBlock), 1, file);
						current_startBlock = fBlock.nNextBlock;
						fBlock = nextBlock;
						current_offset = 0;
					}
				}
				fBlock.data[current_offset] = buf[j];
				current_offset++;
				curr_size++;

				if(curr_size > add_bytes)
					add_bytes = curr_size;
			}

			//Write the current block to memory
			fclose(file);

			FILE *wFile = fopen(".disk", "rb+");
			fseek(wFile, current_startBlock, SEEK_SET);
			fwrite(&fBlock, sizeof(fBlock), 1, wFile);

			//Rewrite the new bit map to memory
			fseek(wFile, -FREE_STRUCTURE_SIZE, SEEK_END);
			fwrite(bitmap, FREE_STRUCTURE_SIZE, 1, wFile);

			//Rewrite the updated info on file size to memory
			//set size (number of blocks) and return, or error
			dir.files[i].fsize = add_bytes;
			fseek(wFile, 0, SEEK_SET);
			fwrite(&root, sizeof(root), 1, wFile); //experiment
			fseek(wFile, dirBlock, SEEK_SET);
			fwrite(&dir, sizeof(dir), 1, wFile);
			fclose(wFile);
			return size;
		}
		else
		{
			fclose(file);
			return -ENOENT;
		}
	}
	else
	{
		fclose(file);
		return -ENOENT;
	}
}

/******************************************************************************
 *
 *  DO NOT MODIFY ANYTHING BELOW THIS LINE
 *
 *****************************************************************************/

/*
 * truncate is called when a new file is created (with a 0 size) or when an
 * existing file is made shorter. We're not handling deleting files or 
 * truncating existing ones, so all we need to do here is to initialize
 * the appropriate directory entry.
 *
 */
static int cs1550_truncate(const char *path, off_t size)
{
	(void) path;
	(void) size;

    return 0;
}


/* 
 * Called when we open a file
 *
 */
static int cs1550_open(const char *path, struct fuse_file_info *fi)
{
	(void) path;
	(void) fi;
    /*
        //if we can't find the desired file, return an error
        return -ENOENT;
    */

    //It's not really necessary for this project to anything in open

    /* We're not going to worry about permissions for this project, but 
	   if we were and we don't have them to the file we should return an error

        return -EACCES;
    */

    return 0; //success!
}

/*
 * Called when close is called on a file descriptor, but because it might
 * have been dup'ed, this isn't a guarantee we won't ever need the file 
 * again. For us, return success simply to avoid the unimplemented error
 * in the debug log.
 */
static int cs1550_flush (const char *path , struct fuse_file_info *fi)
{
	(void) path;
	(void) fi;

	return 0; //success!
}


//register our new functions as the implementations of the syscalls
static struct fuse_operations hello_oper = {
    .getattr	= cs1550_getattr,
    .readdir	= cs1550_readdir,
    .mkdir	= cs1550_mkdir,
	.rmdir = cs1550_rmdir,
    .read	= cs1550_read,
    .write	= cs1550_write,
	.mknod	= cs1550_mknod,
	.unlink = cs1550_unlink,
	.truncate = cs1550_truncate,
	.flush = cs1550_flush,
	.open	= cs1550_open,
};

//Don't change this.
int main(int argc, char *argv[])
{
	return fuse_main(argc, argv, &hello_oper, NULL);
}
