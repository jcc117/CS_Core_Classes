package cs445.a3;

import java.util.List;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sudoku {
    static boolean isFullSolution(int[][] board) {
        // TODO: Complete this method
		//Flag value to indicate there are no 0's left in the array
		boolean fullSolution = true;
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[i].length; j++)
			{
				//Make sure the value on the board does not equal 0
				if(board[i][j] == 0)
				{
					fullSolution = false;
					break;
				}
			}
			if(!fullSolution)
				break;
		}
		//If a zero was found return false
		if(!fullSolution)
			return false;
		else
		{
			//If the board is not an actual solution, reject it and return false
			//Otherwise, return true
			if(reject(board))
				return false;
			else
				return true;
		}
    }

    static boolean reject(int[][] board) {
        // TODO: Complete this method
		//Test rows
		boolean foundRepeat = false;
		for(int i = 0; i < board.length; i++)
		{
			//Each individual row
			for(int j = 0; j < board[i].length; j++)
			{
				//Each item in each row
				for(int k = 0; k < board[i].length; k++)
				{
					//If two items in the same row are equal and those two items are not equal to each other,
					//the solution is rejected. Otherwise, it is not rejected
					if(board[i][j]%10 == board[i][k]%10 && j != k && board[i][j]%10 != 0 && board[i][k]%10 != 0)
					{
						foundRepeat = true;
						break;
					}
				}
				if(foundRepeat)
					break;
			}
			if(foundRepeat)
				break;
		}
		if(foundRepeat)
			return true;
		else
		{
			//Test columns
			//Hold rows constant through each loop
			for(int i = 0; i < board.length; i++)
			{
				//Increment test column
				for(int j = 0; j < board[i].length; j++)
				{
					//Increment column being compared with the previous one
					for(int k = 0; k < board[i].length; k++)
					{
						if(board[j][i]%10 == board[k][i]%10 && j != k && board[j][i]%10 != 0 && board[k][i] != 0)
						{
							foundRepeat = true;
							break;
						}
					}
					if(foundRepeat)
						break;
				}
				if(foundRepeat)
					break;
			}
			if(foundRepeat)
				return true;
			else
			{
				//Test each sector
				for(int i = 0; i < board.length; i++)
				{
					for(int j = 0; j < board[i].length; j++)
					{
						//Locate the sector in which the value lies
						int minRowVal = 0;
						int maxRowVal = 0;
						int minColVal = 0;
						int maxColVal = 0;
						//Sector 1: [1][1]-[3][3]
						if(i>= 0 && i<=2 && j>=0 && j<= 2)
						{
							minRowVal = 0;
							maxRowVal = 2;
							minColVal = 0;
							maxColVal = 2;
						}
						//Sector 2: [1][4]-[3][6]
						else if(i>= 0 && i<=2 && j>=3 && j<= 5)
						{
							minRowVal = 0;
							maxRowVal = 2;
							minColVal = 3;
							maxColVal = 5;
						}
						//Sector 3: [1][7]-[3][9]
						else if(i>= 0 && i<=2 && j>=6 && j<= 8)
						{
							minRowVal = 0;
							maxRowVal = 2;
							minColVal = 6;
							maxColVal = 8;
						}
						//Sector 4: [4][1]-[6][3]
						else if(i>= 3 && i<=5 && j>=0 && j<= 2)
						{
							minRowVal = 3;
							maxRowVal = 5;
							minColVal = 0;
							maxColVal = 2;
						}
						//Sector 5: [4][4]-[6][6]
						else if(i>= 3 && i<=5 && j>=3 && j<= 5)
						{
							minRowVal = 3;
							maxRowVal = 5;
							minColVal = 3;
							maxColVal = 5;
						}
						//Sector 6: [4][7]-[6][9]
						else if(i>= 3 && i<=5 && j>=6 && j<= 8)
						{
							minRowVal = 3;
							maxRowVal = 5;
							minColVal = 6;
							maxColVal = 8;
						}
						//Sector 7: [7][1]-[9][3]
						else if(i>= 6 && i<=8 && j>=0 && j<= 2)
						{
							minRowVal = 6;
							maxRowVal = 8;
							minColVal = 0;
							maxColVal = 2;
						}
						//Sector 8: [7][4]-[9][6]
						else if(i>= 6 && i<=8 && j>=3 && j<= 5)
						{
							minRowVal = 6;
							maxRowVal = 8;
							minColVal = 3;
							maxColVal = 5;
						}
						//Sector 9: [7][7]-[9][9]
						else if(i>= 6 && i<=8 && j>=6 && j<= 8)
						{
							minRowVal = 6;
							maxRowVal = 8;
							minColVal = 6;
							maxColVal = 8;
						}
						//Cycle through all values in the sector to make sure it is not equal to the value being looked at
						for(int k = minRowVal; k <= maxRowVal; k++)
						{
							for(int p = minColVal; p <= maxColVal; p++)
							{
								if(board[i][j]%10 == board[k][p]%10 && j != p && board[i][j]%10 != 0 && board[k][p]%10 != 0)
								{
									foundRepeat = true;
									break;
								}
							}
							if(foundRepeat)
								break;
						}
						if(foundRepeat)
							break;
					}
					if(foundRepeat)
						break;
				}
				if(foundRepeat)
					return true;
				else
					return false;
			}
		}
    }

    static int[][] extend(int[][] board) {
        // TODO: Complete this method
		boolean incremented = false;
		int[][] temp = new int[9][9];
		for(int i = 0; i < temp.length; i++)
		{
			for(int j = 0; j < temp[i].length; j++)
			{
				//Copy the value of the cell over to the new array
				//If the value is less than 10 but not 0, than it was the last value extended to, and should now be increased
				//by 10 so it will no longer be considered the most recent value.
				if(board[i][j] < 10 && board[i][j] != 0)
					temp[i][j] = board[i][j] + 10;
				//Copy the regular value over to the new array
				else if(board[i][j] != 0 || incremented)
					temp[i][j] = board[i][j];
				//Increment the first cell found which contains zero
				//This only occurs once and the copying continues until the end of the array
				//is reached due to the fact that there may be other default values placed there
				//by the puzzle template
				else if(!incremented)
				{
					temp[i][j] = 1;
					incremented = true;
				}
			}
		}
		//Return the board if it was extended
		//If it wasn't then the board is already full and return null
		if(incremented)
			return temp;
		else
			return null;
    }

    static int[][] next(int[][] board) {
        // TODO: Complete this method
		int[][] temp = new int[9][9];
		for(int i = 0; i < 9; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				//If the value is less than 10 and not 0, then it is the most recent value
				if(board[i][j] != 0 && board[i][j] < 10)
				{
					//If the value is 9, then it can not be extended any farther, and null will be returned
					if(board[i][j] == 9)
						return null;
					//Otherwise, increase the value by 1
					else
						temp[i][j] = board[i][j] + 1;
				}
				//Copy over other values
				else
					temp[i][j] = board[i][j];
			}
		}
        return temp;
    }

	//Note: For all test boards except those of the testNext method, each value that is not 0 should be 10
	//more than what it actually is. This is to make it easier to find which value was most recently added since
	//that is the only value less than 10 but not 0. However, because none of the methods except testNext actually need
	//to find the most recent value and all operations use the %10 operator, none of the boards appear this way. However,
	//in the real code, it will not appear this way. Regardless, it does not matter whether the values are incremented by 10 or
	//not for the other methods and they will work the same if they are or not.
    static void testIsFullSolution() {
        // TODO: Complete this method
		System.out.println("testIsFullSolution--------------------");
		//Test a board that is complete and has a valid solution
		System.out.println("This board should be a full solution");
		int[][] testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{6, 7, 2, 1, 9, 5, 3, 4, 8},
		{1, 9, 8, 3, 4, 2, 5, 6, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 2, 6, 8, 5, 3, 7, 9, 1},
		{7, 1, 3, 9, 2, 4, 8, 5, 6},
		{9, 6, 1, 5, 3, 7, 2, 8, 4},
		{2, 8, 7, 4, 1, 9, 6, 3, 5},
		{3, 4, 5, 2, 8, 6, 1, 7, 9}};
		printBoard(testBoard);
		if(isFullSolution(testBoard))
			System.out.println("This is a full solution");
		else
			System.out.println("This is not a full solution");
		//Test board with an empty value still in it
		System.out.println("This board should not be a full solution due to the fact that there is still a zero in the array");
		testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{6, 7, 2, 1, 9, 5, 3, 4, 8},
		{1, 9, 0, 3, 4, 2, 5, 6, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 2, 6, 8, 5, 3, 7, 9, 1},
		{7, 1, 3, 9, 2, 4, 8, 5, 6},
		{9, 6, 1, 5, 3, 7, 2, 8, 4},
		{2, 8, 7, 4, 1, 9, 6, 3, 5},
		{3, 4, 5, 2, 8, 6, 1, 7, 9}};
		printBoard(testBoard);
		if(isFullSolution(testBoard))
			System.out.println("This is a full solution");
		else
			System.out.println("This is not a full solution");
		//Test a board that is complete but does not have a valid solution
		System.out.println("This board should not be a full solution due to the fact that it should be rejected");
		testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{6, 7, 2, 1, 9, 5, 3, 4, 8},
		{1, 9, 8, 3, 4, 2, 5, 6, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 2, 6, 8, 5, 3, 7, 9, 1},
		{7, 1, 3, 9, 2, 4, 8, 5, 6},
		{9, 6, 1, 5, 3, 7, 2, 8, 3},
		{2, 8, 7, 4, 1, 9, 6, 3, 5},
		{3, 4, 5, 2, 8, 6, 1, 7, 9}};
		printBoard(testBoard);
		if(isFullSolution(testBoard))
			System.out.println("This is a full solution");
		else
			System.out.println("This is not a full solution");
		//Test a board that is not complete and has conflicting values in it
		System.out.println("This board should not be a full solution due to the fact that it is not full and has conflicting values");
		testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{6, 7, 2, 1, 9, 5, 3, 4, 8},
		{1, 9, 8, 0, 4, 2, 5, 6, 0},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 2, 0, 8, 5, 3, 7, 9, 1},
		{7, 1, 3, 9, 2, 4, 8, 5, 7},
		{9, 6, 1, 5, 3, 7, 2, 8, 0},
		{2, 8, 7, 4, 1, 9, 6, 3, 5},
		{3, 4, 5, 2, 8, 0, 1, 7, 9}};
		printBoard(testBoard);
		if(isFullSolution(testBoard))
			System.out.println("This is a full solution");
		else
			System.out.println("This is not a full solution");
    }

    static void testReject() {
        // TODO: Complete this method
		//Test functionality of board that should not be rejected and is complete
		System.out.println("testReject--------------------");
		System.out.println("This board should not be rejected: complete solution");
		int[][] testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{6, 7, 2, 1, 9, 5, 3, 4, 8},
		{1, 9, 8, 3, 4, 2, 5, 6, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 2, 6, 8, 5, 3, 7, 9, 1},
		{7, 1, 3, 9, 2, 4, 8, 5, 6},
		{9, 6, 1, 5, 3, 7, 2, 8, 4},
		{2, 8, 7, 4, 1, 9, 6, 3, 5},
		{3, 4, 5, 2, 8, 6, 1, 7, 9}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		//Test functionality of detecting items in the same column
		System.out.println("This board should be rejected: conflicting values in columns");
		testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{0, 0, 2, 1, 9, 5, 3, 4, 8},
		{1, 9, 0, 3, 4, 2, 5, 6, 0},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 2, 6, 8, 5, 3, 0, 0, 1},
		{7, 0, 1, 9, 2, 4, 8, 5, 6},
		{9, 6, 1, 5, 0, 7, 2, 8, 4},
		{2, 8, 0, 0, 1, 9, 6, 3, 5},
		{3, 4, 5, 2, 8, 6, 1, 0, 9}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		//Test functionality of detecting items in the same row
		System.out.println("This board should be rejected: conflicting values in rows");
		testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{0, 0, 2, 1, 9, 5, 3, 4, 8},
		{1, 9, 3, 3, 4, 2, 5, 6, 0},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 2, 6, 8, 5, 3, 0, 0, 1},
		{7, 0, 1, 9, 2, 4, 8, 5, 6},
		{9, 6, 0, 5, 0, 7, 2, 8, 4},
		{2, 8, 0, 0, 1, 9, 6, 3, 5},
		{3, 4, 5, 2, 8, 6, 1, 0, 9}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 1
		System.out.println("This board should be rejected: conflicting values in sector 1");
		testBoard = new int[][]{
		{0, 0, 1, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{1, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 4, 8, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 5, 2, 8, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 2
		System.out.println("This board should be rejected: conflicting values in sector 2");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 0, 3, 1, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 4, 8, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 5, 2, 8, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 3
		System.out.println("This board should be rejected: conflicting values in sector 3");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 5},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 4, 8, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 0},
		{0, 0, 5, 2, 8, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 4
		System.out.println("This board should be rejected: conflicting values in sector 4");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{0, 1, 4, 9, 2, 0, 8, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 5, 2, 8, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 5
		System.out.println("This board should be rejected: conflicting values in sector 5");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 8, 4, 0, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 5, 2, 0, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 6
		System.out.println("This board should be rejected: conflicting values in sector 6");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 0, 8, 5, 4},
		{9, 0, 0, 0, 0, 7, 0, 8, 0},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 5, 2, 8, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 7
		System.out.println("This board should be rejected: conflicting values in sector 7");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 0, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 4, 8, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 9, 2, 8, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 8
		System.out.println("This board should be rejected: conflicting values in sector 8");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 4, 8, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 5, 2, 9, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting items in sector 9
		System.out.println("This board should be rejected: conflicting values in sector 9");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 4, 8, 5, 0},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 5, 2, 8, 0, 1, 7, 6}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting an imcomplete board that should not be rejected
		System.out.println("This board should not be rejected: incomplete solution");
		testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{6, 7, 2, 0, 9, 5, 0, 4, 0},
		{1, 9, 0, 3, 4, 2, 5, 6, 7},
		{8, 5, 9, 7, 6, 1, 0, 2, 3},
		{4, 2, 6, 8, 5, 3, 7, 9, 1},
		{7, 0, 3, 0, 0, 4, 8, 5, 6},
		{9, 6, 1, 5, 0, 7, 2, 8, 4},
		{2, 8, 7, 4, 1, 9, 6, 3, 0},
		{3, 0, 5, 2, 8, 6, 1, 7, 9}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of values are next to each other but not in the same sector
		System.out.println("This board should not be rejected: testBoard[2][2] and testBoard[3][3] should not conflict");
		testBoard = new int[][]{
		{0, 0, 0, 6, 7, 8, 0, 0, 0},
		{6, 0, 0, 1, 0, 0, 0, 0, 0},
		{0, 9, 7, 3, 0, 2, 5, 0, 0},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 4, 8, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 3, 4, 0, 9, 6, 0, 5},
		{0, 0, 5, 2, 8, 0, 1, 7, 0}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
		
		//Test functionality of detecting multiple errors on the board
		System.out.println("This board should be rejected: combination of all three types of errors above");
		testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{6, 7, 5, 0, 9, 5, 0, 4, 0},
		{1, 0, 0, 3, 4, 2, 5, 6, 7},
		{8, 5, 9, 7, 6, 1, 0, 2, 3},
		{4, 2, 6, 8, 5, 3, 2, 9, 1},
		{7, 0, 3, 0, 0, 4, 8, 5, 6},
		{9, 6, 1, 5, 0, 7, 2, 8, 4},
		{2, 8, 7, 4, 1, 9, 6, 3, 1},
		{3, 0, 5, 2, 8, 6, 1, 7, 9}};
		printBoard(testBoard);
		if(reject(testBoard))
			System.out.println("Solution is rejected");
		else
			System.out.println("Solution is not rejected");
    }

    static void testExtend() {
        // TODO: Complete this method
		//Note: these boards may be rejected by the reject method
		//However, they are only meant for testing the extend method
		System.out.println("testExtend--------------------");
		//Test a board that can be extended
		System.out.println("This board should be able to be extended");
		int[][] testBoard = new int[][]{
		{5, 2, 8, 6, 7, 8, 0, 0, 0},
		{6, 7, 0, 1, 0, 0, 0, 0, 0},
		{1, 9, 0, 3, 0, 2, 5, 0, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 0, 6, 8, 0, 0, 7, 0, 1},
		{7, 1, 0, 9, 2, 4, 8, 5, 6},
		{9, 0, 0, 0, 0, 7, 0, 8, 4},
		{2, 0, 1, 4, 0, 9, 6, 0, 5},
		{3, 0, 5, 2, 8, 0, 1, 7, 0}};
		//Print the board for comparison
		printBoard(testBoard);
		testBoard = extend(testBoard);
		
		System.out.println("");
		//Test whether the board extended or not
		if(testBoard == null)
		{
			System.out.println("Board could not be extended");
		}
		else
		{
			System.out.println("Board could be extended");
			printBoard(testBoard);
			System.out.println("");
		}
		
		//Test a board that cannot be extended(a full board)
		System.out.println("This board cannot be extended: full solution");
		testBoard = new int[][]{
		{5, 3, 4, 6, 7, 8, 9, 1, 2},
		{6, 7, 2, 1, 9, 5, 3, 4, 8},
		{1, 9, 8, 3, 4, 2, 5, 6, 7},
		{8, 5, 9, 7, 6, 1, 4, 2, 3},
		{4, 2, 6, 8, 5, 3, 7, 9, 1},
		{7, 1, 3, 9, 2, 4, 8, 5, 6},
		{9, 6, 1, 5, 3, 7, 2, 8, 4},
		{2, 8, 7, 4, 1, 9, 6, 3, 5},
		{3, 4, 5, 2, 8, 6, 1, 7, 9}};
		//Print the board for comparison
		printBoard(testBoard);
		testBoard = extend(testBoard);
		
		System.out.println("");
		//Test whether the board extended or not
		if(testBoard == null)
		{
			System.out.println("Board could not be extended");
		}
		else
		{
			System.out.println("Board could be extended");
			printBoard(testBoard);
			System.out.println("");
		}
    }

    static void testNext() {
        // TODO: Complete this method
		//Note: On these boards, the next solution is not necessarily a valid solution
		//Test a board in which there is a next value
		System.out.println("testNext--------------------");
		System.out.println("The next value should be found");
		int[][] testBoard = new int[][]{
		{15, 0, 18, 16, 17, 18, 0, 0, 0},
		{16, 17, 0, 11, 0, 0, 0, 0, 0},
		{11, 19, 0, 13, 0, 12, 15, 0, 17},
		{18, 15, 19, 17, 16, 11, 14, 12, 13},
		{14, 0, 16, 18, 0, 0, 17, 0, 11},
		{17, 11, 0, 19, 12, 14, 18, 15, 16},
		{19, 0, 0, 0, 0, 17, 0, 18, 14},
		{12, 0, 11, 14, 0, 19, 16, 0, 15},
		{3, 0, 15, 12, 18, 0, 11, 17, 0}};
		
		printBoard(testBoard);
		System.out.println("");
		
		testBoard = next(testBoard);
		if(testBoard == null)
		{
			System.out.println("No next solution");
		}
		else
		{
			testBoard = extend(testBoard);
			printBoard(testBoard);
			System.out.println("Next solution found");
		}
		//Test a board in which the most recent value is 9
		System.out.println("The next value should not be found-most recent value is 9");
		testBoard = new int[][]{
		{15, 0, 18, 16, 17, 18, 0, 0, 0},
		{16, 17, 0, 11, 0, 0, 0, 0, 0},
		{11, 19, 0, 13, 0, 12, 15, 0, 17},
		{18, 15, 19, 17, 16, 11, 14, 12, 13},
		{14, 0, 16, 18, 0, 0, 17, 0, 11},
		{17, 11, 0, 19, 12, 14, 18, 15, 16},
		{19, 0, 0, 0, 0, 17, 0, 18, 14},
		{12, 0, 11, 14, 0, 19, 16, 0, 15},
		{13, 9, 15, 12, 18, 0, 11, 17, 0}};
		
		printBoard(testBoard);
		System.out.println("");
		
		testBoard = next(testBoard);
		if(testBoard == null)
		{
			System.out.println("No next solution");
		}
		else
		{
			testBoard = extend(testBoard);
			printBoard(testBoard);
			System.out.println("Next solution found");
		}
    }

    static void printBoard(int[][] board) {
        if (board == null) {
            System.out.println("No assignment");
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 6) {
                System.out.println("----+-----+----");
            }
            for (int j = 0; j < 9; j++) {
                if (j == 2 || j == 5) {
                    System.out.print(board[i][j]%10 + " | ");
                } else {
                    System.out.print(board[i][j]%10);
                }
            }
            System.out.print("\n");
        }
    }
	//All values that are not 0 will have 10 added to them
	//This will help distinguish them from the most recent value added, which is the only one
	//less than 10 that is not a 0. All operations performed on them, including that of printBoard,
	//will require the %10 operator on them for them to be compared and printed properly. This works
	//since and interger below ten x and x%10 are considered the same thing
    static int[][] readBoard(String filename) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(filename), Charset.defaultCharset());
        } catch (IOException e) {
            return null;
        }
        int[][] board = new int[9][9];
        int val = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    val = Integer.parseInt(Character.toString(lines.get(i).charAt(j)));
                } catch (Exception e) {
                    val = 0;
                }
				if(val != 0)
					board[i][j] = val + 10;
				else
					board[i][j] = val;
            }
        }
        return board;
    }

    static int[][] solve(int[][] board) {
        if (reject(board)) return null;
        if (isFullSolution(board)) return board;
        int[][] attempt = extend(board);
        while (attempt != null) {
            int[][] solution = solve(attempt);
            if (solution != null) return solution;
            attempt = next(attempt);
        }
        return null;
    }

    public static void main(String[] args) {
        if (args[0].equals("-t")) {
            testIsFullSolution();
            testReject();
			testExtend();
            testNext();
        } else {
            int[][] board = readBoard(args[0]);
            printBoard(solve(board));
        }
    }
}
