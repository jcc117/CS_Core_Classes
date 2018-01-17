// Jordan Carr
// Quiz File
// Created 3/17/16
// Updated 3/17/16

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Quiz
{
	public static void main(String[] args) throws IOException
	{
		//Check if the user entered a file name at the command line
		if(args.length == 0)
		{
			System.out.println("Error: No file input.");
			System.exit(0);
		}
		
		Scanner keyboard = new Scanner(System.in);
		File myFile = new File(args[0]);
		
		//Check to make sure entered file exists
		if(!myFile.exists())
		{
			System.out.println("Error: No file found.");
			System.exit(0);
		}
		
		Scanner inputFile = new Scanner(myFile);
		ArrayList<Question> questionArray = new ArrayList<Question>();
		
		//Read questions into the file
		while(inputFile.hasNext())
		{
			//Read question
			String question = inputFile.nextLine();
			
			//Read number of answers
			String numAnswersString = inputFile.nextLine();
			int numAnswers = Integer.parseInt(numAnswersString);
			
			//Read answers
			String[] answerArray = new String[numAnswers];
			for(int i = 0; i < answerArray.length; i++)
			{
				answerArray[i] = inputFile.nextLine();
			}
			
			//Read correct answer number
			String correctAnswerString = inputFile.nextLine();
			int correctAnswer = Integer.parseInt(correctAnswerString);
			
			//Read number of times tried
			String timesTriedString = inputFile.nextLine();
			int timesTried = Integer.parseInt(timesTriedString);
			
			//Read number of times correct
			String timesCorrectString = inputFile.nextLine();
			int timesCorrect = Integer.parseInt(timesCorrectString);
			
			//Create question object and put it into an arraylist of questions
			Question newQuestion = new Question(question, numAnswers, answerArray, correctAnswer, timesTried, timesCorrect);
			questionArray.add(newQuestion);
		}
		
		//Close file
		inputFile.close();
		
		//Welcome message
		System.out.println("Welcome to the Quiz Program! Good Luck!\n");
		
		//Create array to hold answer numbers inputed by the user
		int[] inputAnswers = new int[questionArray.size()];
	
		//Display quiz and ask questions
		for(int i = 0; i < questionArray.size(); i++)
		{
			//Display question
			System.out.println(questionArray.get(i).getQuestion());
			
			//Display answers
			for(int j = 0; j < questionArray.get(i).getNumAnswers(); j++)
			{
				System.out.println("(" + j + ")\t" + questionArray.get(i).getAnswer(j));
			}
			System.out.println("");
			
			//Prompt user to answer
			System.out.print("Your answer? (Enter a number) >");
			
			//Error check to make sure input is valid
			boolean goodInput = false;
			while(!goodInput)
			{
				//Check to make sure variable is an int
				if(keyboard.hasNextInt())
				{
					//Check to make sure variable is within the correct range of answers for the question
					inputAnswers[i] = keyboard.nextInt();
					if(inputAnswers[i] >= 0 && inputAnswers[i] < questionArray.get(i).getNumAnswers())
					{
						goodInput = true;
					}
					else
					{
						System.out.print("Invalid Input. Please try again. >");
					}
				}
				//If input is not an int
				else if(!keyboard.hasNextInt())
				{
					keyboard.nextLine();
					System.out.print("Invalid Input. Please try again. >");
					keyboard.nextLine();
				}
			}
			System.out.println("");
		}
		
		//Display questions with answers and if correct or not
		double rightAnswers = 0.0;
		double wrongAnswers = 0.0;
		for(int k = 0; k < questionArray.size(); k++)
		{
			System.out.println(questionArray.get(k).getQuestion());
			boolean rightOrWrong = questionArray.get(k).determineCorrect(inputAnswers[k]);
			//If right
			if(rightOrWrong)
			{
				System.out.println("Correct!");
				System.out.println("Guessed Answer: " + questionArray.get(k).getAnswer(inputAnswers[k]));
				System.out.println("Correct Answer: " + questionArray.get(k).getCorrectAnswer() + "\n");
				rightAnswers++;
				questionArray.get(k).addTimesCorrect();
				questionArray.get(k).addTimesTried();
			}
			//If wrong
			else
			{
				System.out.println("Incorrect! Remember the answer for next time.");
				System.out.println("Guessed Answer: " + questionArray.get(k).getAnswer(inputAnswers[k]));
				System.out.println("Correct Answer: " + questionArray.get(k).getCorrectAnswer() + "\n");
				wrongAnswers++;
				questionArray.get(k).addTimesTried();
			}
		}
		
		//Display statistics about the quesitons
		System.out.println("");
		System.out.println("Various other statistics:");
		System.out.printf("Your score: %.2f%% \n", rightAnswers/(rightAnswers + wrongAnswers) * 100.0);
		System.out.println("Questions Answered Correctly: " + rightAnswers);
		System.out.println("Total Number of Questions: " + questionArray.size());
		System.out.println("");
		
		//Display statistics about the individual questions
		for(int i = 0; i < questionArray.size(); i++)
		{
			System.out.println(questionArray.get(i).getQuestion());
			System.out.println("\tTimes Correct: " + questionArray.get(i).getTimesCorrect());
			System.out.println("\tTimes Tried: " + questionArray.get(i).getTimesTried());
			System.out.printf("\tPercent Correct: %.2f%% \n", questionArray.get(i).getCorrectPercentage());
			System.out.println("");
		}
		
		//Find and display the easiest and hardest questions based percent of the time they were answered correctly
		int hardestQuestionIndex = 0;
		int easiestQuestionIndex = 0;
		
		//Find hardest question
		for(int i = 1; i < questionArray.size(); i++)
		{
			if(questionArray.get(i).getCorrectPercentage() < questionArray.get(hardestQuestionIndex).getCorrectPercentage())
			{
				hardestQuestionIndex = i;
			}
		}
		
		//Find the easiest question
		for(int i = 1; i < questionArray.size(); i++)
		{
			if(questionArray.get(i).getCorrectPercentage() > questionArray.get(easiestQuestionIndex).getCorrectPercentage())
			{
				easiestQuestionIndex = i;
			}
		}
		
		//Display hardest question
		System.out.println("Hardest Question: " + questionArray.get(hardestQuestionIndex).getQuestion());
		System.out.println("\tTimes Tried: " + questionArray.get(hardestQuestionIndex).getTimesTried());
		System.out.println("\tTiems Correct: " + questionArray.get(hardestQuestionIndex).getTimesCorrect());
		System.out.printf("\tPercent Correct: %.2f%% \n", questionArray.get(hardestQuestionIndex).getCorrectPercentage());
		
		//Display easiest question
		System.out.println("\nEasiestQuestion: " + questionArray.get(easiestQuestionIndex).getQuestion());
		System.out.println("\tTimes Tried: " + questionArray.get(easiestQuestionIndex).getTimesTried());
		System.out.println("\tTiems Correct: " + questionArray.get(easiestQuestionIndex).getTimesCorrect());
		System.out.printf("\tPercent Correct: %.2f%% \n", questionArray.get(easiestQuestionIndex).getCorrectPercentage());
		
		//Save and overwrite the file to update timesTried and timesCorrect information
		//To do this the entire file is rewritten
		PrintWriter outputFile = new PrintWriter(args[0]);
		for(int j = 0; j < questionArray.size(); j++)
		{
			outputFile.println(questionArray.get(j).getQuestion());
			outputFile.println(questionArray.get(j).getNumAnswers());
			for(int k = 0; k < questionArray.get(j).getNumAnswers(); k++)
			{
				outputFile.println(questionArray.get(j).getAnswer(k));
			}
			outputFile.println(questionArray.get(j).getCorrectAnswerNumber());
			outputFile.println(questionArray.get(j).getTimesTried());
			outputFile.println(questionArray.get(j).getTimesCorrect());
		}
		outputFile.close();
	}
}