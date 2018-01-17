//Jordan Carr
//Assignment 1
//Created 9/14/16
//Modified 9/24/16

package cs445.a1;

import java.util.Arrays;

public class Set<T> implements SetInterface<T>
{
	/*Holds all of the values in the given set and records the size of the set*/
	private T[] setArray;
	private int size;
	
	/*Sets the size of the array to a default value of 10 in the case that
	the user does not set the size him/herself*/
	public Set()
	{
		setArray = (T[])new Object[10];
		size = 0;
	}
	
	/*Sets the size of the array to the value that user desires*/
	public Set(int capacity)
	{
		setArray = (T[])new Object[capacity];
		size = 0;
	}
	
	/*Determines the current size of the set and returns an int value of that size*/
	public int getCurrentSize()
	{
		return size;
	}
	
	/*Determines if the set is empty or not.
	Returns true if it is empty, false if it is not*/
	public boolean isEmpty()
	{
		if(size == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//NOT DONE
	/*Adds a new entry to the set. If the new argument given is null, than an exception is thrown. If the item
	already exists in the set than a value of false is returned.  If the set is full than a SetFullException is 
	thrown.  If the none of the above criteria are met than an item will be added to the set*/
	public boolean add(T newEntry) throws SetFullException, java.lang.IllegalArgumentException
	{
		if(newEntry == null)
		{
			throw new java.lang.IllegalArgumentException();
		}
		//Check to make sure the set doesn't already contain newEntry
		if(contains(newEntry))
		{
			return false;
		}
		
		//Resizes the array and adds newEntry in the case that the array is full
		else if(size >= setArray.length)
		{
			setArray = Arrays.copyOf(setArray, size*2);
			setArray[size] = newEntry;
			size++;
			return true;
		}
		
		//Adds newEntry normally
		else
		{
			setArray[size] = newEntry;
			size++;
			return true;
		}
	}
	
	/*Removes a specific item from the set. If able to be removed, value returned is true. If not, false is returned.*/
	public boolean remove(T entry) throws java.lang.IllegalArgumentException
	{
		if(entry == null)
		{
			throw new java.lang.IllegalArgumentException();
		}
		boolean found = false;
		int index = 0;
		
		//Search the array for the value
		for(int i = 0; setArray.length > i; i++)
		{
			if(setArray[i].equals(entry))
			{
				found = true;
				index = i;
				break;
			}
		}
		/*If found is true, than the location of where that item is is switched with the last item, and then that is removed.
		If not found, the fase is returned and the set is not modified*/
		if(found == false)
		{
			return false;
		}
		else
		{
			setArray[index] = setArray[size - 1];
			setArray[size - 1] = null;
			size--;
			return true;
		}
	}
	
	/*Removes an arbitrary value (the last one in the array) from the set and returns it to the user. If there is nothing in the set
	than null is returned. Otherwise the last value in the setArray is returned, the size of the setArray
	is deincremented by one, and the location of where that value was is set to null.*/
	public T remove()
	{
		if(size == 0)
		{
			return null;
		}
		
		else
		{
			T returnValue = setArray[size - 1];
			setArray[size -1] = null;
			size--;
			return returnValue;
		}
	}
	
	/*Clears all items in the set. If there are no items in the set, then the
	set will remain unchanged.*/
	public void clear()
	{
		if(size > 0)
		{
			for(int j = 0; setArray.length > j; j++)
			{
				setArray[j] = null;
			}
			
			size = 0;
		}
			
	}
	
	/*Determines whether the set contains the given entry. If the entry is null then an
	exception is thrown. If there is nothing in the set than false is returned. If the item
	cannot be found within the set false is returned. If the item is found within the set
	than true is returned.*/
	public boolean contains(T entry) throws java.lang.IllegalArgumentException
	{
		if(entry == null)
		{
			throw new java.lang.IllegalArgumentException();
		}
		else if(size == 0)
		{
			return false;
		}
		else
		{
			boolean found = false;
			for(int j = 0; setArray.length > j; j++)
			{
				if(setArray[j] == null)
				{
					break;
				}
				else if(setArray[j].equals(entry))
				{
					found = true;
					break;
				}
			}
			
			return found;
		}
	}
	
	/*A new array is returned to the user containing all of the values of the set minus
	any empty spots within it. In other words, the amount of items within the set is the size of
	the array being returned to the user so it contains no null values. References to those objects
	within the array are copied over to the new array.*/
	public T[] toArray()
	{
		@SuppressWarnings("unchecked")
		T[] returnArray = (T[])new Object[size];
		int index = 0;
		while(setArray.length > index && setArray[index] != null)
		{
			returnArray[index] = setArray[index];
			index++;
		}
		
		return returnArray;
	}
}