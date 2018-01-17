// Jordan Carr
// This program uses methods to sum to numbers together
// 10/8/15
// 10/8/15

import javax.swing.JOptionPane;

public class CARR
{
	public static void main (String[] args)
	{
	// Create two number variables
	
	String number1;
	String number2;	

	// Show welcome message
	welcome();

	// Collect the numbers from the user
	
	number1 = JOptionPane.showInputDialog("Enter the first number");
	int number01 = Integer.parseInt(number1);
	
	number2 = JOptionPane.showInputDialog("Enter the second number");
	int number02 = Integer.parseInt(number2);
	
	// Sum the numbers together
	int total = sum(number01, number02);
	
	// Show the result
	JOptionPane.showMessageDialog(null, "The sume of the numbers is " + total);
	
	}	
	
	public static void welcome()
	{
		JOptionPane.showMessageDialog(null, "Welcome to my program!");
	}

	public static int sum(int num1, int num2)
	{
		int adder = num1 + num2;
		return adder;
	}
}

	