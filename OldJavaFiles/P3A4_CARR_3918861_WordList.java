//Jordan Carr
//This is the class file that contains methods for adding words to a list of words and sending them to the main file
//12/7/15
//12/8/15

import java.util.ArrayList;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class P3A4_CARR_3918861_WordList
{
	//Create array to store the uploaded words
	private ArrayList<String> wordStorage;
	
	public P3A4_CARR_3918861_WordList()
	{
		wordStorage = new ArrayList<String>();
	}

	//Adds a single word to the list of words
	public void addWord(String newWord)
	{
		wordStorage.add(newWord);
	}

	//Adds a list of words from a single text file to the list of words
	public void addWordsFromFile(Scanner openFile) throws IOException
	{
		//Loop through the file and assign a word to the array until the end has been reached
		while(openFile.hasNext())
		{
			//Assign a word to the array
			wordStorage.add(openFile.nextLine());
		}
	}

	//Returns a random word from the list of words
	public String getRandomWord()
	{
		//Generate random number to select a random word from the wordList array
		Random selection = new Random();
		int selection1 = selection.nextInt(wordStorage.size());
		
		//Return selected word
		String returnWord = wordStorage.get(selection1);
		return returnWord;
	}

	//Returns the number of words in the list of words
	public int numWords()
	{
		return wordStorage.size();
	}
}