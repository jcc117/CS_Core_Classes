//Jordan Carr
//Voter Class, stores data about a given voter including voter status, voter ID, and name
//Created 4/6/16
//Updated 4/10/16

public class Voter
{
	private String _voterID;
	private boolean _voted;
	private String _name;
	
	//Set all variables for the individual voter
	public Voter(String voterID, String name, boolean voted)
	{
		_voterID = voterID;
		_voted = voted;
		_name = name;
	}
	
	//Return voter ID
	public String getVoterID()
	{
		return _voterID;
	}
	
	//Return voter's name
	public String getName()
	{
		return _name;
	}
	
	//Return status if the voter already voted or not
	public boolean getVoted()
	{
		return _voted;
	}
	
	//Change the voter's status as to whether he/she voted yet
	public void setVoteStatus()
	{
		_voted = true;
	}
}