// Jordan Carr
// Question File
// 3/17/16
// 3/17/16

public class Question
{
	private String _question;
	private int _numAnswers;
	private String[] _answers;
	private int _correctAnswer;
	private int _timesTried;
	private int _timesCorrect;
	
	public Question(String question, int numAnswers, String[] answers, int correctAnswer, int timesTried, int timesCorrect)
	{
		_question = question;
		_numAnswers = numAnswers;
		
		_answers = new String[_numAnswers];
		for(int i = 0; i < numAnswers; i++)
		{
			_answers[i] = answers[i];
		}
		
		_correctAnswer = correctAnswer;
		_timesTried = timesTried;
		_timesCorrect = timesCorrect;
	}
	
	//Returns question
	public String getQuestion()
	{
		return _question;
	}
	
	//Returns answer
	public String getAnswer(int i)
	{
		return _answers[i];
	}
	
	//Returns number of answers
	public int getNumAnswers()
	{
		return _numAnswers;
	}
	
	//Returns times tried
	public int getTimesTried()
	{
		return _timesTried;
	}
	
	//Returns times answer was correct
	public int getTimesCorrect()
	{
		return _timesCorrect;
	}
	
	//Returns the correct answer
	public String getCorrectAnswer()
	{
		return _answers[_correctAnswer];
	}
	
	//Returns the index of the correct answer in the answer array
	public int getCorrectAnswerNumber()
	{
		return _correctAnswer;
	}
	
	//Returns percentage of time question was answered correctly
	public double getCorrectPercentage()
	{
		return (((double)_timesCorrect)/((double)_timesTried)) * 100.0;
	}
	
	//Determines if answer given by user was correct or not
	public boolean determineCorrect(int inputAnswer)
	{
		//If correct
		if((_correctAnswer) == inputAnswer)
		{
			return true;
		}
		//If incorrect
		else
		{
			return false;
		}
	}
	
	//Add 1 to times correct
	public void addTimesCorrect()
	{
		_timesCorrect++;
	}
	
	//Add 1 to times tried
	public void addTimesTried()
	{
		_timesTried++;
	}
}