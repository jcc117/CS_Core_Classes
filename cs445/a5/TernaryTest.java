package cs445.a5;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TernaryTest
{
	public static void main(String[] args)
	{
		Integer name1 = 1;
	Integer name2 = 2;
	Integer name3 = 3;
	Integer name4 = 4;
	Integer name5 = 5;
	Integer name6 = 6;
	Integer name7 = 7;
	Integer name8 = 8;
	Integer name9 = 9;
	Integer name10 = 0;
	Integer name11 = -1;
	Integer name12 = -2;
	Integer name13 = -3;
	Integer name14 = -4;
	
	TernaryTree<Integer> tree1 = new TernaryTree<Integer>(name7);
	TernaryTree<Integer> tree2 = new TernaryTree<Integer>(name11);
	TernaryTree<Integer> tree3 = new TernaryTree<Integer>(name14);
	TernaryTree<Integer> tree4 = new TernaryTree<Integer>(name6, tree1, null, null);
	TernaryTree<Integer> tree5 = new TernaryTree<Integer>(name9, tree2, null, null);
	TernaryTree<Integer> tree6 = new TernaryTree<Integer>(name13, tree3, null, null);
	TernaryTree<Integer> tree7 = new TernaryTree<Integer>(name5);
	TernaryTree<Integer> tree8 = new TernaryTree<Integer>(name2, tree7, tree4, null);
	TernaryTree<Integer> tree9 = new TernaryTree<Integer>(name8);
	TernaryTree<Integer> tree10 = new TernaryTree<Integer>(name10);
	TernaryTree<Integer> tree11 = new TernaryTree<Integer>(name3, tree9, tree5, tree10);
	TernaryTree<Integer> tree12 = new TernaryTree<Integer>(name12, null, tree6, null);
	TernaryTree<Integer> tree13 = new TernaryTree<Integer>(name4, null, null, tree12);
	TernaryTree<Integer> tree14 = new TernaryTree<Integer>(name1, tree8, tree11, tree13);
	
	TernaryTree<String> tree0 = new TernaryTree<String>();
	try
	{
		tree0.getRootData();
	}
	catch(EmptyTreeException e)
	{
		System.out.println("tree0 is empty");
	}
		Iterator<Integer> preOrder = tree14.getPreorderIterator();
		try
		{
			boolean x = true;
			while(x)
			{
				System.out.print(preOrder.next());
			}
		}
		catch(NoSuchElementException e)
		{
			System.out.println("\nEnd Found");
		}
		
		Iterator<Integer> postOrder = tree14.getPostorderIterator();
		try
		{
			boolean x = true;
			while(x)
			{
				System.out.print(postOrder.next());
			}
		}
		catch(NoSuchElementException e)
		{
			System.out.println("\nEnd Found");
		}
		
		Iterator<Integer> levelOrder = tree14.getLevelOrderIterator();
		try
		{
			boolean x = true;
			while(x)
			{
				System.out.print(levelOrder.next());
			}
		}
		catch(NoSuchElementException e)
		{
			System.out.println("\nEnd Found");
		}
		
		System.out.println("Root: " + tree13.getRootData());
		System.out.println("Height: " + tree14.getHeight());
		System.out.println("Number of Nodes: " + tree14.getNumberOfNodes());
	}
}