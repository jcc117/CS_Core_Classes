//Jordan Carr
//Assignment 1
//Created 9/14/16
//Modified 9/24/16

package cs445.a1;

public class Profile implements ProfileInterface
{
	/*Stores information on the user's name and a small bio about himself/herself*/
	private String userName;
	private String userBio;
	private Set<ProfileInterface> profileSet = new Set<ProfileInterface>();
	
	/*If the user passes no information, userName and userBio will be set to
	empty Strings*/
	public Profile()
	{
		userName = "";
		userBio = "";
	}
	
	/*If the user passes information on his/her name to the constructor, that information
	will be set to its given variables. If the values passed in are null, they will be 
	filled in with empty Strings*/
	public Profile(String name, String about)
	{
		if(name != null)
		{
			userName = name;
		}
		else
		{
			userName = "";
		}
		
		if(about != null)
		{
			userBio = about;
		}
		else
		{
			userBio = "";
		}
	}
	
	/*Sets a new name for the user. If null argument is passed, an exception is thrown. If not,
	new name is set*/
	public void setName(String newName) throws java.lang.IllegalArgumentException
	{
		if(newName != null)
		{
			userName = newName;
		}
		else
		{
			throw new java.lang.IllegalArgumentException();
		}
	}
	
	/*Returns the name of the user*/
	public String getName()
	{
		return userName;
	}
	
	/*Sets the user bio. If null argument is passed, an exception is thrown. If not,
	new user bio is set*/
	public void setAbout(String newAbout) throws java.lang.IllegalArgumentException
	{
		if(newAbout != null)
		{
			userBio = newAbout;
		}
		else
		{
			throw new java.lang.IllegalArgumentException();
		}
	}
	
	/*Returns the user bio*/
	public String getAbout()
	{
		return userBio;
	}
	
	/*Adds a new profile to follow to the profileSet. If successful, value of true is returned.
	If not, false is returned.*/
	public boolean follow(ProfileInterface other)
	{
		try
		{
			if(other.equals(this))
			{
				return false;
			}
			boolean added = profileSet.add(other);
			return added;
		}
		catch(SetFullException e)
		{
			return false;
		}
	}
	
	/*Removes a profile to unfollow from the profileSet. If successful, a value of true is returned.
	If not, false is returned.*/
	public boolean unfollow(ProfileInterface other)
	{
		boolean removed = profileSet.remove(other);
		return removed;
	}
	
	/*Adds another profile to profileSet. If the user is already following the profile, than false is returned.
	If not, it is added to profileSet.*/
	public ProfileInterface[] following(int howMany)
	{
		Object[] followListTemp = profileSet.toArray();
		ProfileInterface[] followList = new ProfileInterface[followListTemp.length];
		for(int i = 0; followList.length > i; i++)
		{
			followList[i] = (ProfileInterface)followListTemp[i];
		}
		ProfileInterface[] returnList = null;
		//Checks to make sure howMany is within the legal bounds for an array size
		if(howMany > 0)
		{
			/*If howMany equals the current size of the array, then the array will be returned
			completely unmodified*/
			if(followList.length <= howMany)
			{
				returnList = followList;
			}
			/*In the case that howMany is smaller than the followList length, the data will be copied
			over into a new array of that size. The data will be copied over in the order it is in the array.*/
			else
			{
				returnList = new ProfileInterface[howMany];
				for(int j = 0; howMany > j; j++)
				{
					returnList[j] = followList[j];
				}
			}
		}
		return returnList;
	}
	
	/*Reccomends a profile for the user to follow. Returns a profile followed by a profile the user is following. Does not
	reccommed to follow someone the user is already following or to follow oneself. Returns the profile to suggest or null
	if one is not found.*/
	public ProfileInterface recommend()
	{
		ProfileInterface recommendation = null;
		//Ensure the user is following someone
		if(profileSet.isEmpty())
		{
			return recommendation;
		}
		else
		{
			ProfileInterface[] followList = following(profileSet.getCurrentSize());
			boolean foundRec = false;
			//Cycle through all profiles that user follows
			for(int i = 0; followList.length > i; i++)
			{	
				//Cycle through all followed profiles of a followed profile
				//Note-the argument of 4 is temporary here until another way of getting the entire array is figured out
				ProfileInterface[] followedList = followList[i].following(4);
				for(int j = 0; followedList.length > j; j ++)
				{
					if(!profileSet.contains(followedList[j]) && followedList[j] != null && !followedList[j].equals(this))
					{
						recommendation = followedList[j];
						foundRec = true;
						break;
					}
				}
				if(foundRec)
				{
					break;
				}
			}
		}
		return recommendation;
	}
			
}