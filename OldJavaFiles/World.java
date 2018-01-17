// Jordan Carr
// World File
// 3/15/16
import java.util.ArrayList;
public class World
{
	private int[][][][][] worldArray;
	private ArrayList<int[]> visitedRooms;
	public World()
	{
		//Fill array with values
		worldArray = new int[10][10][10][10][10];
		for(int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j++)
			{
				for(int k = 0; k < 10; k++)
				{
					for(int l = 0; l < 10; l++)
					{
						for(int m = 0; m < 10; m++)
						{
							worldArray[i][j][k][l][m] = (i + j + k + l + m) % 10;
						}
					}
				}
			}
		}
		//Create arraylist for holding rooms that have already been visited
		visitedRooms = new ArrayList<int[]>();
	}
	
	public int visit(int[] coordinates)
	{
		boolean found = false;
		if(!visitedRooms.isEmpty())
		{
			//Loop through the arraylist to ensure the room has not yet been visited
			for(int i = 0; i < visitedRooms.size(); i++)
			{
				boolean correctValue = false;
				for(int j = 0; j < coordinates.length; j++)
				{
					if(visitedRooms.get(i)[j] == coordinates[j])
					{
						correctValue = true;
					}
					else
					{
						correctValue = false;
						break;
					}
				
				}
				
				if(correctValue)
				{
					found = true;
					break;
				}
			}
		}
		//Return 11 to signify the room has been painted white
		if(found)
		{
			return 11;
		}
		//Determine the color of the newly visited room
		else
		{
			//Calculate the color of the room
			int color = (coordinates[0] + coordinates [1] + coordinates[2] + coordinates[3] + coordinates[4]) % 10;
			//Copy and add the array to the arraylist
			int[] newArray = new int[5];
			for(int i = 0; i < coordinates.length; i ++)
			{
				newArray[i] = coordinates[i];
			}
			visitedRooms.add(newArray);
			//Return the color
			return color;
		}
	}
	
	//Return a color based on what number the coordinate has
	public String getColor(int color)
	{
		if(color == 11)
		{
			return "White";
		}
		
		else if(color == 1)
		{
			return "Lime";
		}
		
		else if(color == 2)
		{
			return "Cerulean";
		}
		
		else if(color == 3)
		{
			return "Goldenrod";
		}
		
		else
		{
			return "Black";
		}
	}
}