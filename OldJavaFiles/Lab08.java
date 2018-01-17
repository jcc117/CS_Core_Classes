// Jordan Carr
// Lab 8
// 3/1/16

import java.util.Random;

public class Lab08
{
	public static void main(String[] args)
	{
		//Number of rutabagas
		int rutabaga = 0;
		
		Random number = new Random();
		//Numbers of items in the arrays
		int numItemsMartin = 0;
		int numItemsPangloss = 0;
		//Number of resizes for the arrays
		int numResizeMartin = 0;
		int numResizePangloss = 0;
		//Keep track whether the arrays resized or not
		boolean martinResize = false;
		boolean panglossResize = false;
		
		String[] martin = {"***", "***", "***", "***", "***"};
		String[] pangloss = {"***", "***", "***", "***", "***"};
		
		//Loop through 40 seasons
		for(int j = 0; j < 40; j++)
		{
			rutabaga = number.nextInt(5);
			System.out.println("Season: " + (j + 1) + ", " + rutabaga + " rutabaga(s)");
			
			//Add rutabagas to the arrays
			for(int i = 0; i < rutabaga; i ++)
			{
				//Add item to martin
				martin[numItemsMartin] = "" + rutabaga;
				numItemsMartin++;
				//Check to resize martin
				if(numItemsMartin == martin.length)
				{
					martin = resizeMartin(martin);
					numResizeMartin++;
					martinResize = true;
				}
				
				//Add item to pangloss
				pangloss[numItemsPangloss] = "" + rutabaga;
				numItemsPangloss++;
				//Check to resize pangloss
				if(numItemsPangloss == pangloss.length)
				{
					pangloss = resizePangloss(pangloss);
					numResizePangloss++;
					panglossResize = true;
				}
				
				//Display new size of arrays if they were resized
				if(martinResize)
				{
					System.out.println("Resized Martin's Garden to " + martin.length);
				}
				if(panglossResize)
				{
					System.out.println("Resized Pangloss's Garden to " + pangloss.length);
				}
			
				//Reset resize flags
				martinResize = false;
				panglossResize = false;
			}
		}
		
		//Display results
		displayArray(martin);
		System.out.println("Martin Garden Size: " + martin.length + ", Resized " + numResizeMartin + " times");
		displayArray(pangloss);
		System.out.println("Pangloss Garden Size: " + pangloss.length + ", Resized " + numResizePangloss + " times");
	}
	
	//Resize Martin
	public static String[] resizePangloss(String[] original)
	{
		int oldLength = original.length;
		int newLength = oldLength * 2;
		String [] toReturn = new String[newLength];
	
		//Copy old data
		for (int j = 0; j < oldLength; j++) 
		{
			toReturn[j] = original[j];
		}
		
		//Fill in the rest of the indexes with default value
		for (int i = oldLength; i < newLength; i++)
		{
			toReturn[i] = "***";
		}
	
		return toReturn;
	}
	
	//Resize Pangloss
	public static String[] resizeMartin(String[] original)
	{
		int oldLength = original.length;
		int newLength = oldLength + 2;
		String [] toReturn = new String[newLength];
		
		//Copy old data
		for (int j = 0; j < oldLength; j++) 
		{
			toReturn[j] = original[j];
		}
		
		//Fill in the rest of indexes with default value
		for (int i = oldLength; i < newLength; i++)
		{
			toReturn[i] = "***";
		}
	
		return toReturn;
	}
	
	//Display the array
	public static void displayArray(String[] array)
	{
		System.out.print("[ ");
		for(int j = 0; j < array.length; j++)
		{
			if(j == (array.length - 1))
			{
				System.out.print(array[j]);
			}
			else
			{
				System.out.print(array[j] + ", ");
			}
		}
		System.out.println("]");
	}
}