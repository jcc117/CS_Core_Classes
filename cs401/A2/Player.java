// Jordan Carr
// Player Class
// Created 2/7/16
// Updated 2/20/16

import java.io.*;

public class Player
{
	private String name;
	private int totalHands;
	private int handsWon;
	private double money;
	
	//This constructor will create a brand new player's object
	//Hands won, hands, and money are set to default values
	public Player(String playerName)
	{
		name = playerName;
		totalHands = 0;
		handsWon = 0;
		money = 100.0;
	}
	
	//This constructor will take values from a saved file and create a new object with them
	//Hands won, hands, and money are taken from the file via the BlackJack class
	public Player(String savedName, int savedHands, int savedHandsWon, double savedMoney) throws IOException
	{
		name = savedName;
		totalHands = savedHands;
		handsWon = savedHandsWon;
		money = savedMoney;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getTotalHands()
	{
		return totalHands;
	}
	
	public int getHandsWon()
	{
		return handsWon;
	}
	
	public double getMoney()
	{
		return money;
	}
	
	public void addHandsWon()
	{
		handsWon++;
	}
	
	public void addTotalHands()
	{
		totalHands++;
	}
	
	public void fixBettings(double bettings)
	{
		money += bettings;
	}
}