//Jordan Carr
//Main class file, stores main method for program and action listeners for all of the buttons
//Created 4/3/16
//Updated 4/11/16

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class Assig4
{
	private static final int _HEIGHT = 300;
	private static final int _WIDTH = 500;
	public static JButton _logInButton;
	public static JButton _castButton;
	public static JFrame _logInWindow;
	public static JPanel _mainPanel;
	public static JFrame _ballotWindow;
	public static ArrayList<Ballot> _ballots;
	public static ArrayList<String> _input;
	public static ArrayList<ArrayList> _optionButtons;
	public static ArrayList<Voter> _voters;
	public static String _currentVoterID;
	
	public static void main(String[] args) throws IOException
	{
		//Open file and make sure it exists
		File newFile = new File(args[0]);
		if(!newFile.exists())
		{
			System.out.println("Error: File not found.");
			System.exit(0);
		}
		Scanner inputFile = new Scanner(newFile);
		
		//Read in number of categories for the ballot
		String x = inputFile.nextLine();
		int numCategories = Integer.parseInt(x);
		_ballots = new ArrayList<Ballot>(numCategories);
		_input = new ArrayList<String>(numCategories);
		
		int counter = 0;
		
		//Read in ballot file
		while(inputFile.hasNext())
		{
			//Read in values
			_input.add(inputFile.nextLine());
			System.out.println(_input.get(counter));
			
			//Create a new Ballot object from the data
			Ballot newBallot = new Ballot();
			newBallot.setParameters(_input.get(counter));
			
			//Add category label
			JLabel categoryLabel = new JLabel(newBallot.getCategory());
			newBallot.add(categoryLabel);
			
			counter++;

			_ballots.add(newBallot);
			
		}
		//Close the file
		inputFile.close();
		
		//Open voters file
		File voterFile = new File("voters.txt");
		
		//Make sure voters.txt exists
		if(!voterFile.exists())
		{
			System.out.println("Error: Could not fine \"voters.txt\"");
			System.exit(0);
		}
		
		inputFile = new Scanner(voterFile);
		_voters = new ArrayList<Voter>();
		
		//Read voters file
		while(inputFile.hasNext())
		{
			String tempString = inputFile.nextLine();
			String[] voterInfo = tempString.split(":");
			boolean ifVoted = Boolean.parseBoolean(voterInfo[2]);
			Voter newVoter = new Voter(voterInfo[0], voterInfo[1], ifVoted);
			_voters.add(newVoter);
		}
		inputFile.close();
		System.out.println("Read in voter data.");
		
		//Create ballot window and add newly made panels
		_ballotWindow = new JFrame("E-Voter, Version 1.0");
		_ballotWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_ballotWindow.setSize(_WIDTH, _HEIGHT);
		_ballotWindow.setLayout(new GridLayout());
		
		//Add ballot panels to window
		for(int i = 0; i < _ballots.size(); i++)
		{
			_ballotWindow.add(_ballots.get(i));
		}
		
		//Add Log In button
		_mainPanel = new JPanel();
		_logInButton = new JButton("Log In");
		ActionListener logInListener = new LButtonListener();
		_logInButton.addActionListener(logInListener);
		_mainPanel.add(_logInButton);
		
		//Add Casting button
		_castButton = new JButton("Cast Vote");
		ActionListener castListener = new CastListener();
		_castButton.addActionListener(castListener);
		_castButton.setEnabled(false);
		_mainPanel.add(_castButton);
		_ballotWindow.add(_mainPanel);
		
		//Add ballot option buttons to an ArrayList of ArrayLists of JButtons
		//This will allow the program to easily cycle through them to enable them after
		//the user logs in
		//Create an ActionListener for all of the voting buttons to share
		ActionListener voteButton = new VoteButtonListener();
		_optionButtons = new ArrayList<ArrayList>();
		for(int i = 0; i < _ballots.size(); i++)
		{
			//Set the layout for each panel
			_ballots.get(i).setLayout(new GridLayout(_ballots.get(i).getAnswers().length + 1, 1));
			
			//Cycle through each panel and create buttons for all of the voting options
			ArrayList<JToggleButton> buttons = new ArrayList<JToggleButton>();
			for(int j = 0; j < _ballots.get(i).getAnswers().length; j++)
			{
				//Create the button
				JToggleButton newJToggleButton = new JToggleButton(_ballots.get(i).getAnswers()[j]);
				newJToggleButton.addActionListener(voteButton);
				newJToggleButton.setForeground(Color.black);
				newJToggleButton.setEnabled(false);
				
				//Add the toggle button to the arrayList of toggle buttons, each array corresponding
				//to one JPanel/ballot
				buttons.add(newJToggleButton);
				
				//Add the button to its corresponding Ballot
				_ballots.get(i).add(newJToggleButton);
			}
			
			//Add the ArrayList of Buttons to the _optionButtons ArrayList
			_optionButtons.add(buttons);
		}
		_ballotWindow.setVisible(true);
		
	}
	
	//Return option button arrayList
	public static ArrayList<ArrayList> getOptionButtons()
	{
		return _optionButtons;
	}
	
	//Return cast button
	public static JButton getCastButton()
	{
		return _castButton;
	}
	
	//Return voter arrayList
	public static ArrayList<Voter> getVoters()
	{
		return _voters;
	}
	
	//Return log in button
	public static JButton getLogInButton()
	{
		return _logInButton;
	}
	
	//Set the current voter ID
	public static void setCurrentVoterID(String id)
	{
		_currentVoterID = id;
	}
	
	//Return the current voter ID
	public static String getCurrentVoterID()	
	{
		return _currentVoterID;
	}
	
	//Return the ballots
	public static ArrayList<Ballot> getBallots()
	{
		return _ballots;
	}
			
}

class LButtonListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			System.out.println("Log In button pressed.");
			//Ask the user for their ID number
			boolean iDFound = false;
			while(!iDFound)
			{
				//Ensure the voter Id exists
				String voterID = JOptionPane.showInputDialog("Please enter your ID to log in.");
				int index = 0;
				for(int k = 0; k < Assig4.getVoters().size(); k++)
				{
					System.out.println(Assig4.getVoters().get(k).getVoterID());
					if(voterID.equals(Assig4.getVoters().get(k).getVoterID()))
					{
						iDFound = true;
						index = k;
						break;
					}
				}
			
				//If id exists
				if(iDFound)
				{
					//Make sure the voter didn't already vote
					System.out.println("ID found.");
					System.out.println(Assig4.getVoters().get(index).getVoted());
					boolean voted = Assig4.getVoters().get(index).getVoted();
					if(voted)
					{
						System.out.println("Voter already voted.");
						JOptionPane.showMessageDialog(null, Assig4.getVoters().get(index).getName() + ", you already voted");
						iDFound = false;
					}
				
					//Voter is eligible to vote
					else
					{	
						//Set the current voter ID
						Assig4.setCurrentVoterID(voterID);
					
						//Disable the log in button
						Object temp1 = (JButton)e.getSource();
		
						//Enable the choice buttons and cast vote button
						((JButton)temp1).setEnabled(false);
						for(int i = 0; i < Assig4.getOptionButtons().size(); i++)
						{
							for(int j = 0; j < Assig4.getOptionButtons().get(i).size(); j++)
							{
								Object temp2 = (JToggleButton)Assig4.getOptionButtons().get(i).get(j);
								((JToggleButton)temp2).setEnabled(true);
							}
						}
						Assig4.getCastButton().setEnabled(true);
						System.out.println("Enable Option buttons and vote casting button");
					}
				}
			
				//If Id does not exist
				else
				{
					JOptionPane.showMessageDialog(null, "Error: ID not found");
				}
			
			}
		}
		//Catch exception thrown if user hits cancel on the window
		catch(NullPointerException ex)
		{
			System.out.println("Exit button/cancel pressed");
		}
	}
}

class VoteButtonListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("Voting Button pressed.");
		//Change color of selected button to indicate selection(red) or deselection(black)
		JToggleButton selection = (JToggleButton)e.getSource();
		
		//Black foreground indicates the button is not selected
		if(selection.getForeground().equals(Color.black))
		{
			//Find which Ballot the button is in
			int foundButtonIndex = 0;
			for(int i = 0; i < Assig4.getBallots().size(); i++)
			{
				//Found button flag
				boolean buttonFound = false;
				for(int j = 0; j < Assig4.getBallots().get(i).getAnswers().length; j++)
				{
					//For debugging purposes
					System.out.println(Assig4.getBallots().get(i).getAnswers()[j]);
					
					if(selection.getText().equals(Assig4.getBallots().get(i).getAnswers()[j]))
					{
						//Set the selection of the ballot
						Assig4.getBallots().get(i).setSelection(selection.getText());
						foundButtonIndex = i;
						buttonFound = true;
						break;
					}
				}
				if(buttonFound)
				{
					//For debugging purposes
					System.out.println(Assig4.getBallots().get(i).getSelection());
					break;
				}
			}
			
			//Deselect all of the buttons in the given category
			for(int i = 0; i < Assig4.getBallots().get(foundButtonIndex).getAnswers().length; i++)
			{
				((JToggleButton)Assig4.getOptionButtons().get(foundButtonIndex).get(i)).setForeground(Color.black);
				if(((JToggleButton)Assig4.getOptionButtons().get(foundButtonIndex).get(i)).isSelected())
				{
					((JToggleButton)Assig4.getOptionButtons().get(foundButtonIndex).get(i)).setSelected(!((JToggleButton)Assig4.getOptionButtons().get(foundButtonIndex).get(i)).isSelected());
				}
			}
			
			//Reselect the selected button
			selection.setSelected(!selection.isSelected());
			selection.setForeground(Color.red);
			
			//For debugging purposes
			System.out.println(selection.getText());
			
		}
		else
		{
			selection.setForeground(Color.black);
			//Find which panel the button resides in
			for(int i = 0; i < Assig4.getBallots().size(); i++)
			{
				boolean buttonFound = false;
				for(int j = 0; j < Assig4.getBallots().get(i).getAnswers().length; j++)
				{
					System.out.println(Assig4.getBallots().get(i).getAnswers()[j]);
					if(selection.getText().equals(Assig4.getBallots().get(i).getAnswers()[j]))
					{
						//Deselect the choice from the found panel
						Assig4.getBallots().get(i).setDeselection();
						buttonFound = true;
						break;
					}
				}
				if(buttonFound)
				{
					break;
				}
			}
		}
		
	}
}

class CastListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		//Ask the user to confirm his/her choices
		System.out.println("Cast Button pressed.");
		int done = JOptionPane.showConfirmDialog(null, "Are you sure about your choices?", "Vote Confirmation", JOptionPane.YES_NO_OPTION);
	
		//1 = no, 0 = yes
		System.out.println(done);
		//Check user's input to make sure he/she is done casting votes
		if(done == 0)
		{	
			//Update the ballot files
			for(int i = 0; i < Assig4.getBallots().size(); i++)
			{
				try
				{
					Assig4.getBallots().get(i).updateBallotFile();
				}
				catch(IOException ex)
				{
					System.out.println("Error");
				}
			}
			
			//Reset the ballots so another person can log in to vote
			for(int i = 0; i < Assig4.getOptionButtons().size(); i++)
			{
				//Reset selections to null in each category
				Assig4.getBallots().get(i).setDeselection();
				
				for(int j = 0; j < Assig4.getOptionButtons().get(i).size(); j++)
				{
					Object temp = (JToggleButton)Assig4.getOptionButtons().get(i).get(j);
					//Make sure the button needs to be reset
					if(((JToggleButton)temp).getForeground().equals(Color.red) && ((JToggleButton)temp).isSelected() == true)
					{
						((JToggleButton)temp).setForeground(Color.black);
						((JToggleButton)temp).setSelected(!((JToggleButton)temp).isSelected());
					}
					((JToggleButton)temp).setEnabled(false);
				}
			}
			//Disable cast button and enable log in button
			Object temp2 = (JButton)e.getSource();
			((JButton)temp2).setEnabled(false);
			Assig4.getLogInButton().setEnabled(true);
			
			//Change the voting status of the voter who just voted
			//Find the voter in the array of voters
			for(int k = 0; k < Assig4.getVoters().size(); k++)
			{
				//For debugging purposes
				System.out.println(Assig4.getVoters().get(k).getVoterID());
				
				//If voter is found, change the vote status to true
				if(Assig4.getCurrentVoterID().equals(Assig4.getVoters().get(k).getVoterID()))
				{
					Assig4.getVoters().get(k).setVoteStatus();
					break;
				}
			}
			
			System.out.println("Voter is done voting");
			
			//Update the voter file
			try
			{
				PrintWriter newFile = new PrintWriter("temptemp2.txt");
				for(int i = 0; i < Assig4.getVoters().size(); i++)
				{
					//Write the data to the temporary file
					newFile.println(Assig4.getVoters().get(i).getVoterID()
					+ ":" + Assig4.getVoters().get(i).getName()
					+ ":" + Assig4.getVoters().get(i).getVoted()); 
				}
				
				newFile.close();
				
				//Copy and override the data from the temporary file to the voters file
				File tempFile = new File("temptemp2.txt");
				Scanner inputFile = new Scanner(tempFile);
				PrintWriter updatedVoterFile = new PrintWriter("voters.txt");
				while(inputFile.hasNext())
				{
					updatedVoterFile.println(inputFile.nextLine());
				}
				
				//Close temporay file and updated voter file
				inputFile.close();
				updatedVoterFile.close();
				
				System.out.println("Voter file updated");
			}
			catch(IOException ex)
			{
				System.out.println("Error");
			}
		}
	}
}