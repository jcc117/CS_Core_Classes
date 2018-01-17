/*
 * "Dice nums" minimal kernel module - /dev version
 *
 * Jordan Carr <jcc117@pitt.edu>
 *
 */

#include <linux/fs.h>
#include <linux/init.h>
#include <linux/miscdevice.h>
#include <linux/module.h>
#include <linux/random.h>

#include <asm/uaccess.h>

/*
 * dice_read is the function called when a process calls read() on
 * /dev/dice.  It writes random numbers to the buffer passed in the
 * read() call.
 */

static ssize_t dice_read(struct file * file, char * buf, 
			  size_t count, loff_t *ppos)
{
	
	//Generator random numbers from 0 to 5
	int numbytes = 0;
	while(count > 0)
	{
		int i;
		int size = 0;
		//If number of bytes still needed is greater than 32, copy 32 bytes to the user
		if(count >= 32)
			size = 32;
		//Else, copy the number of bytes still needed to the user
		else
			size = count;
		
		//Get random bytes and modulo them to 6
		unsigned char buffer[32];
		get_random_bytes(buffer, sizeof(buffer));
		for(i = 0; i < sizeof(buffer); i++)
		{
			buffer[i] = buffer[i] % 6;
			//printk("Val: %d\n", buffer[i]);
		}
		//Copy the bytes to the user buffer
		//Copy starting from the end of the previous copied data and only copy "size" data
		if (copy_to_user(&buf[numbytes], buffer, size))
			return -EINVAL;
		
		//Decrement number of bytes still needed and increment number of bytes copied
		count -= size;
		numbytes += size;
	}

	/*
	 * If file position is non-zero, then assume the string has
	 * been read and indicate there is no more data to be read.
	 */
	//if (*ppos != 0)
		//return 0;
	
	/*
	 * Tell the user how much data we wrote.
	 */
	*ppos = *ppos + numbytes;

	return numbytes;
}

/*
 * The only file operation we care about is read.
 */

static const struct file_operations dice_fops = {
	.owner		= THIS_MODULE,
	.read		= dice_read,
};

static struct miscdevice dice_dev = {
	/*
	 * We don't care what minor number we end up with, so tell the
	 * kernel to just pick one.
	 */
	MISC_DYNAMIC_MINOR,
	/*
	 * Name ourselves /dev/dice.
	 */
	"dice",
	/*
	 * What functions to call when a program performs file
	 * operations on the device.
	 */
	&dice_fops
};

static int __init
dice_init(void)
{
	int ret;

	/*
	 * Create the "dice" device in the /sys/class/misc directory.
	 * Udev will automatically create the /dev/dice device using
	 * the default rules.
	 */
	ret = misc_register(&dice_dev);
	if (ret)
		printk(KERN_ERR
		       "Unable to register \"Dice Nums\" misc device\n");

	return ret;
}

module_init(dice_init);

static void __exit
dice_exit(void)
{
	misc_deregister(&dice_dev);
}

module_exit(dice_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Jordan Carr <jcc117@pitt.edu>");
MODULE_DESCRIPTION("\"Dice Nums\" minimal module");
MODULE_VERSION("dev");
