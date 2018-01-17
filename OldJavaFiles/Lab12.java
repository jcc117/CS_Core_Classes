// Jordan Carr
// Lab 12
// 4/12/16

import java.util.*;
import java.io.*;

public class Lab12
{
	public static void main(String[] args)
	{
		//IndexOutOfBoundsException
		try
		{
			int[] a = new int[6];
			int b = 40;
			int c = a[b];
		}
		catch(IndexOutOfBoundsException ex)
		{
			System.out.println(ex);
		}
		
		//NegativeArraySizeException
		try
		{
			int d = -9;
			int[] e = new int[d];
		}
		catch(NegativeArraySizeException e)
		{
			System.out.println(e);
		}
		
		//ArithmeticException
		try
		{
			int f = 0;
			int g = 5/f;
		}
		catch(ArithmeticException x)
		{
			System.out.println(x);
		}
		
		//NoogieException
		try
		{
			throw(new NoogieException());
		}
		catch(NoogieException n)
		{
			System.out.println(n);
		}
		
		//CoogieException
		try
		{
			Scanner keyboard = new Scanner(System.in);
			System.out.print("Enter the number of cats>");
			int y = keyboard.nextInt();
			throw(new CoogieException(y));
		}
		catch(CoogieException c)
		{
			System.out.println(c);
		}
		
		//FileNotFoundException
		try
		{
			File newFile = new File("Weeeeeee.txt");
			Scanner input = new Scanner(newFile);
		}
		catch(FileNotFoundException l)
		{
			System.out.println(l);
		}
		
		//InputMismatchException
		try
		{
			Scanner keyboard2 = new Scanner(System.in);
			System.out.print("Enter a double>");
			int x = keyboard2.nextInt();
		}
		catch(InputMismatchException w)
		{
			System.out.println(w);
		}
		
		//NumberFormatException
		try
		{
			String m = "Weeeeeee";
			int k = Integer.parseInt(m);
		}
		catch(NumberFormatException u)
		{
			System.out.println(u);
		}
	}
}

class NoogieException extends Exception
{

}

class CoogieException extends Exception
{
	int numCats;
	
	public CoogieException(int cats)
	{
		numCats = cats;
	}
	public String toString()
	{
		return numCats + " is too many cats!";
	}
}