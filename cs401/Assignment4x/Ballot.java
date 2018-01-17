//Jordan Carr
//Ballot Class, stores data about a given ballot including ballot number, answers, categories, and the selection the user has made
//Created 4/6/16
//Updated 4/10/16

import javax.swing.*;
import java.awt.event.*;
import java.util.Scanner;
import java.io.*;

public class Ballot extends JPanel
{
	public String[] _answers;
	public String _category;
	public String _ballotNumber;
	public String _selection;
	public int[] _answerTallies;
	public int[] _buttonLocations;
	
	//Set the variables of the Ballot
	public void setParameters(String unsplit)
	{
		//Split the String into its indvidual components
		String[] firstSplit = unsplit.split(":");
		_ballotNumber = firstSplit[0];
		_category = firstSplit[1];
		String[] temp = firstSplit[2].split(",");
		_answers = new String[temp.length];
		for(int i = 0; i < temp.length; i++)
		{
			_answers[i] = temp[i];
			
			//For debugging purposes
			System.out.println(_answers[i]);
		}
		_answerTallies = new int[_answers.length];
	}
	
	//Return answers array
	public String[] getAnswers()
	{
		return _answers;
	}
	
	//Return the name of the ballot/name of the category the user is voting in
	public String getCategory()
	{
		return _category;
	}
	
	//Set the answer that the user has selected
	public void setSelection(String selection)
	{
		_selection = selection;
	}
	
	//Deselect an answer
	public void setDeselection()
	{
		_selection = null;
	}
	
	//Return the answer the user has selected
	public String getSelection()
	{
		return _selection;
	}
	
	//Update the file corresponding to the ballot results
	public void updateBallotFile() throws IOException
	{
		if(_selection != null)
		{
			PrintWriter newFile = new PrintWriter("temptemp.txt");
		
			//Write data to a temporary formatted ballot file
			for(int i = 0; i < _answers.length; i++)
			{
				if(_selection.equals(_answers[i]))
				{
					_answerTallies[i]++;
				}
				newFile.println(_answers[i] + ":" + _answerTallies[i]);
			}
			
			//Close temporary file
			newFile.close();
			
			//Overwrite the ballot file by copying over it with the temporary file
			
			File tempFile = new File("temptemp.txt");
			Scanner inputFile = new Scanner(tempFile);
			PrintWriter updatedFile = new PrintWriter(_ballotNumber + ".txt");
			
			//Copy/Overwrite the temporary file over the official ballot file
			while(inputFile.hasNext())
			{
				updatedFile.println(inputFile.nextLine());
			}
			
			//Close the files
			inputFile.close();
			updatedFile.close();
			
			System.out.println("Ballot " + _ballotNumber + " updated");
		}
		else
		{
			System.out.println("Ballot " + _ballotNumber + " did not need updating");
		}
	}
}