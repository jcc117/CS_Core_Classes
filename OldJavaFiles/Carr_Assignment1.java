//Jordan Carr
//This is a shopping program in which buyers can add and remove items from their cart
//and become eligible to recieve discounts when they pay
//Created 1/16/16
//Updated 1/31/16

import java.util.Scanner;

public class Carr_Assignment1
{
	public static void main(String[] args)
	{
		//Create a variable to store input from user
		Scanner keyboard = new Scanner(System.in);
		
		//Create a variable to determine if there are still customers
		//If the variable equals 1, there are still customers
		//If it equals 2, there are no more customers
		int stillShoppers = 1;
		
		//Print welcome message to the user
		System.out.println("Hello! Welcome to the shopping program!");
		
		//Ask the user if there are customers in line
		System.out.println("Are there customers waiting in line?");
		System.out.println("Enter 1 for yes or 2 for no.");
		stillShoppers = keyboard.nextInt();
		
		//Error check to make sure the user entered either a 1 or a 2
		while(stillShoppers != 1 && stillShoppers != 2)
		{
			System.out.println("Wrong input. Please try again.");
			System.out.println("Are there customers waiting in line?");
			System.out.println("Enter 1 for yes or 2 for no.");
			stillShoppers = keyboard.nextInt();
			
		}
		
		//Create variable to store how many customers have used the system
		int numberOfCustomers = 0;
		
		while(stillShoppers == 1)
		{
			//Create variable to store if the customer is still shopping or is ready to check out
			boolean isStillShopping = true;
			
			//Create variables to store products and their costs
			int books = 0;
			int bookMarkers = 0;
			int bookPaintings = 0;
			double bookCosts = 0.0;
			double bookMarkCosts = 0.0;
			double bookPaintingCosts = 0.0;
			
			while(isStillShopping)
			{
				//Display a menu of what can be bought and what has been bought to the user
				System.out.println("**********************************************");
				System.out.println("\nBooks- $5\nBookmarks- $1, get 6 for $5\nBook Paintings- $100 \n");
				System.out.println("Cart:");
				System.out.printf("Books: %d, $%.2f\n", books, bookCosts);
				System.out.printf("Bookmarks: %d, $%.2f\n", bookMarkers, bookMarkCosts);
				System.out.printf("Book Paintings: %d, $%.2f\n", bookPaintings, bookPaintingCosts);
				System.out.println("**********************************************\n");
					
				//Ask user to either add or remove items from the cart
				System.out.println("Press 1 to add an item to your cart.");
				System.out.println("Press 2 to remove an item from your cart.");
				System.out.println("Press 3 to proceed to checkout.");
			
				//Create variable to store input on whether he wants to add or remove an item
				int addOrRemoveItem = keyboard.nextInt();
			
				//Error check to make sure the user entered either a 1, 2, or 3
				while(addOrRemoveItem != 1 && addOrRemoveItem != 2 && addOrRemoveItem != 3)
				{
					System.out.println("Wrong input. Please try again.");
					System.out.println("Enter 1 to add an item,  2 to remove one, or 3 to check out.");
					addOrRemoveItem = keyboard.nextInt();
				}
			
				//If user wishes to add an item
				if(addOrRemoveItem == 1)
				{
					//Ask the user to select an item to add
					System.out.println("What would you like to add?");
					System.out.println("Press 1 for books, 2 for bookmarks, or 3 for book paintings.");
					
					//Create variable to store input on what the customer has chosen
					int selectedItem = keyboard.nextInt();
					
					//Error check to make sure the user entered either a 1, 2, or 3
					while(selectedItem != 1 && selectedItem != 2 && selectedItem != 3)
					{
						System.out.println("Wrong input. Please try again.");
						System.out.println("Press 1 for books, 2 for bookmarks, or 3 for book paintings.");
						selectedItem = keyboard.nextInt();
					}
					
					//If user selects books
					if(selectedItem == 1)
					{
						//Prompt user to enter how many books to add
						System.out.println("How many books would you like to add?");
						int booksAdded = keyboard.nextInt();
						
						//Error check to make sure user entered a positive number of books to add
						while(booksAdded < 0)
						{
							System.out.println("Error: Invalid input.");
							System.out.println("How many books would you like to add?");
							booksAdded = keyboard.nextInt();
						}
						
						//Add books to the cart
						books += booksAdded;
						
						//Calculate costs of the books
						bookCosts = books * 5.0;
					}
					
					//If user selects bookmarks
					else if(selectedItem == 2)
					{
						//Prompt user to enter how many bookmarks to add
						System.out.println("How many bookmarks would you like to add?");
						System.out.println("Buy 6, get 1 free.");
						int bookMarkersAdded = keyboard.nextInt();
						
						//Error check to make sure the user entered a positive number of bookmarks to add
						while(bookMarkersAdded < 0)
						{
							System.out.println("Error: Invalid input.");
							System.out.println("How many bookmarks would you like to add?");
							bookMarkersAdded = keyboard.nextInt();
						}
						
						//Add books to the cart
						bookMarkers += bookMarkersAdded;
						
						//Determine how many bookmark packs there are
						int bookMarkPacks = bookMarkers / 6;
						
						//Determine how many individual bookmarks there are
						int individualBookMarks = bookMarkers % 6;
						
						//Determine costs of bookmarks
						bookMarkCosts = (bookMarkPacks * 5.0) + (individualBookMarks * 1.0);
					}
					
					//If user selects bookpaintings
					else
					{
						//Prompt user to enter how many book paintings to add
						System.out.println("How many book paintings would you like to add?");
						int bookPaintingsAdded = keyboard.nextInt();
						
						
						//Error check to make sure the user entered a positive number of bookmarks to add
						while(bookPaintingsAdded < 0)
						{
							System.out.println("Error: Invalid input.");
							System.out.println("How many book paintings would you like to add?");
							bookPaintingsAdded = keyboard.nextInt();
						}
						
						//Add books to the cart
						bookPaintings += bookPaintingsAdded;
						
						//Determine costs of book paintings
						bookPaintingCosts = bookPaintings * 100.0;
					}
				}
			
				//If user wishes to remove an item
				else if(addOrRemoveItem == 2)
				{
					//Ask the user to select an item to add
					System.out.println("What would you like to remove?");
					System.out.println("Press 1 for books, 2 for bookmarks, or 3 for book paintings.");
					
					//Create variable to store input on what the customer has chosen
					int selectedItem = keyboard.nextInt();
					
					//Error check to make sure the user entered either a 1, 2, or 3
					while(selectedItem != 1 && selectedItem != 2 && selectedItem != 3)
					{
						System.out.println("Wrong input. Please try again.");
						System.out.println("Press 1 for books, 2 for bookmarks, or 3 for book paintings.");
						selectedItem = keyboard.nextInt();
					}
					
					//If user selects books
					if(selectedItem == 1)
					{
						//Error check to make sure books are actually in the cart
						if(books > 0)
						{
							//Prompt user to remove books
							System.out.println("How many books would you like to remove?");
							int booksRemoved = keyboard.nextInt();
							
							//If user accidentally makes the number removed less than 0 or the number entered is greater than 
							//what is in the cart, then an error message will be printed
							while(booksRemoved < 0 || booksRemoved > books)
							{
								System.out.println("Error: Invalid Input.");
								System.out.println("How many books would you like to remove?");
								booksRemoved = keyboard.nextInt();
							}
						
							//Remove the books from the cart
							books -= booksRemoved;
							
							//Calculate costs of the books
							bookCosts = books * 5.0;
						}

						//Prints error message to user that there are no books in the cart
						else
						{
							System.out.println("Error: There are no books in the cart.");
						}
					}
					
					//If user selects bookmarks
					else if(selectedItem == 2)
					{
						//Error check to make sure bookmarks are actually in the cart
						if(bookMarkers > 0)
						{	
							//Prompt user to remove bookmarks
							System.out.println("How many bookmarks would you like to remove?");
							int bookMarkersRemoved = keyboard.nextInt();
							
							//If user accidentally makes the number removed less than 0 or the number entered is greater than 
							//what is in the cart, then an error message will be printed
							while(bookMarkersRemoved < 0 || bookMarkersRemoved > bookMarkers)
							{
								System.out.println("Error: Invalid Input.");
								System.out.println("How many bookmarks would you like to remove?");
								bookMarkersRemoved = keyboard.nextInt();
							}
						
							//Remove the bookmarks from the cart
							bookMarkers -= bookMarkersRemoved;
							
							//Determine how many bookmark packs there are
							int bookMarkPacks = bookMarkers / 6;
						
							//Determine how many individual bookmarks there are
							int individualBookMarks = bookMarkers % 6;
						
							//Determine costs of bookmarks
							bookMarkCosts = (bookMarkPacks * 5.0) + (individualBookMarks * 1.0);
						}

						//Prints error message to user that there are no bookmarks in the cart
						else
						{
							System.out.println("Error: There are no bookmarks in the cart.");
						}
					}
					
					//If user selects bookpaintings
					else
					{
						//Error check to make sure book paintings are actually in the cart
						if(bookPaintings > 0)
						{	
							//Prompt user to remove paintings
							System.out.println("How many book paintings would you like to remove?");
							int bookPaintingsRemoved = keyboard.nextInt();
							
							//If user accidentally makes the number removed less than 0 or the number entered is greater than 
							//what is in the cart, then an error message will be printed
							while(bookPaintingsRemoved < 0 || bookPaintingsRemoved > bookPaintings)
							{
								System.out.println("Error: Invalid Input.");
								System.out.println("How many book paintings would you like to remove?");
								bookPaintingsRemoved = keyboard.nextInt();
							}
							
							//Remove book paintings from the cart
							bookPaintings -= bookPaintingsRemoved;
						
							//Determine costs of book paintings
							bookPaintingCosts = bookPaintings * 100.0;
						}

						//Prints error message to user that there are no bookmarks in the cart
						else
						{
							System.out.println("Error: There are no book paintings in the cart.");
						}
					}
				}
			
				//If user wishes to check out
				else
				{
					isStillShopping = false;
				}
			}	
			
			//Proceed to check out
			//Checks if user is actually buying anything. If this is the case then the user will be given
			//a thank you message, and it will not count towards the number of customers who have used the system
			//since the user didn't buy anything
			
			if(books == 0 && bookMarkers == 0 && bookPaintings == 0)
			{
				System.out.println("Thank you for using the system. Please come again.");
			}
			
			//Proceed to rest of check out if there are items in the cart
			else
			{
				//Update the number of customers who have used the system
				numberOfCustomers++;
				
				//Display all items in the cart to user, if the user did not buy any of a specific item,
				// then that item will not appear on the receipt
				if(books > 0)
				{
					System.out.printf("Books: %d, $%.2f\n", books, bookCosts);
					
					//Prompt the user to check if he/she has a bookworm discount card
					System.out.println("Do you have a bookworm card? Enter 1 for yes or 2 for no.");
					int bookWormCard = keyboard.nextInt();
				
					//Error check to make sure the user entered either a 1 or a 2
					while(bookWormCard != 1 && bookWormCard != 2)
					{
						System.out.println("Error: Invalid input.");
						System.out.println("Do you have a bookworm card? Enter 1 for yes or 2 for no.");
						bookWormCard = keyboard.nextInt();
					}
					
					if(bookWormCard == 1)
					{
						bookCosts *=.75;
						System.out.println("You get a %25 discount on your books!");
						System.out.printf("Books: %d, $%.2f\n", books, bookCosts);
					}
				}
				if(bookMarkers > 0)
				{
					System.out.printf("Bookmarks: %d, $%.2f\n", bookMarkers, bookMarkCosts);
				}
				if(bookPaintings > 0)
				{	
					System.out.printf("Book Paintings: %d, $%.2f\n", bookPaintings, bookPaintingCosts);
				}
				
				//Calculate subtotal
				double subtotal = bookCosts + bookMarkCosts + bookPaintingCosts;
				
				//Formating of the receipt
				System.out.println("\n--------------------------------");
				
				//Display subtotal to user
				System.out.printf("Subtotal: $%.2f\n", subtotal);
				
				//Determine if the user got a %10 discount
				//If user is the third customer out of every 3 to come in, he/she gets the discount
				if((numberOfCustomers % 3) == 0)
				{
					double discount = (subtotal * .1);
					subtotal -= discount;
					System.out.println("Congratulations! You get a %10 discount!");
					System.out.printf("Discount: $%.2f\n", discount);
					System.out.printf("Discounted Subtotal: $%.2f\n", subtotal);
				}
				
				else
				{
					System.out.println("Sorry, you don't get a discount. Better luck next time.");
				}
				
				//Display tax to user
				double tax = subtotal * .07;
				System.out.printf("Tax: $%.2f\n", tax);
				
				//Display total to user
				double total = tax + subtotal;
				System.out.printf("Total: $%.2f\n", total);
				
				//Formating of the receipt
				System.out.println("\n--------------------------------");
				
				//Prompt user to enter payment
				System.out.println("Please enter payment. Exclude the \"$\" in your response.");
				double payment = keyboard.nextDouble();
				
				//If user did not enter enough money
				while(payment < total)
				{
					System.out.println("You did not enter enough. Please enter more.");
					payment = keyboard.nextDouble();
				}
				
				//If user pays too much, calculate the change
				if(payment > total)
				{
					System.out.printf("Change: $%.2f", payment - total);
				}
				
				//Thank the user
				System.out.println("\nThank you for shopping!");
			}
			//Ask user if anyone else is in line
			System.out.println("Are there customers waiting in line?");
			System.out.println("Enter 1 for yes or 2 for no.");
			stillShoppers = keyboard.nextInt();
		
			//Error check to make sure the user entered either a 1 or a 2
			while(stillShoppers != 1 && stillShoppers != 2)
			{
				System.out.println("Wrong input. Please try again.");
				System.out.println("Are there customers waiting in line?");
				System.out.println("Enter 1 for yes or 2 for no.");
				stillShoppers = keyboard.nextInt();
			}
			
		}
	}
}