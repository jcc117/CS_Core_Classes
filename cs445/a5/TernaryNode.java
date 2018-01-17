/**
*A class that represents nodes in a ternary tree
*/
package cs445.a5;

class TernaryNode<T>
{
	private T data;
	private TernaryNode<T> leftChild;
	private TernaryNode<T> middleChild;
	private TernaryNode<T> rightChild;
	
	//Default Constructor
	public TernaryNode()
	{
		this(null);
	}
	
	//Set data with no children
	public TernaryNode(T newData)
	{
		this(newData, null, null, null);
	}
	
	//Set data and children
	public TernaryNode(T newData, TernaryNode<T> newLeftChild, TernaryNode<T> newMiddleChild, TernaryNode<T> newRightChild)
	{
		data = newData;
		leftChild = newLeftChild;
		middleChild = newMiddleChild;
		rightChild = newRightChild;
	}
	
	//Retrieves data
	public T getData()
	{
		return data;
	}
	
	//Sets new data
	public void setData(T newData)
	{
		data = newData;
	}
	
	//Retrieves the leftChild
	public TernaryNode<T> getLeftChild()
	{
		return leftChild;
	}
	
	//Retrieves the middleChild
	public TernaryNode<T> getMiddleChild()
	{
		return middleChild;
	}
	
	//Retrieves the rightChild
	public TernaryNode<T> getRightChild()
	{
		return rightChild;
	}
	
	//Sets a new leftChild
	public void setLeftChild(TernaryNode<T> newLeftChild)
	{
		leftChild = newLeftChild;
	}
	
	//Sets a new middleChild
	public void setMiddleChild(TernaryNode<T> newMiddleChild)
	{
		middleChild = newMiddleChild;
	}
	
	//Sets a new rightChild
	public void setRightChild(TernaryNode<T> newRightChild)
	{
		rightChild = newRightChild;
	}
	
	//Determines if there is a left child
	public boolean hasLeftChild()
	{
		return leftChild != null;
	}
	
	//Determines if there is a middle child
	public boolean hasMiddleChild()
	{
		return middleChild != null;
	}
	
	//Determiens if there is a right child
	public boolean hasRightChild()
	{
		return rightChild != null;
	}
	
	//Detects whether the node is a leaf or not
	public boolean isLeaf()
	{
		return (leftChild == null) && (rightChild == null) && (middleChild == null);
	}
	
	//Determines height of the tree
	public int getHeight()
	{
		return getHeight(this);
	}
	private int getHeight(TernaryNode<T> node)
	{
		int height = 0;
		//Compare heights of all the children, add 1 to the largest of the 3 to account for the root node
		if(node != null)
		{
			height = 1 + Math.max(getHeight(node.getLeftChild()), Math.max(getHeight(node.getMiddleChild()), getHeight(node.getRightChild())));
		}
		
		return height;
	}
	
	//Determines the number of nodes in a tree
	public int getNumberOfNodes()
	{
		int leftNum = 0;
		int middleNum = 0;
		int rightNum = 0;
		
		//Gets number of nodes for each child and adds them plus 1 to account for root node
		if(leftChild != null)
			leftNum = leftChild.getNumberOfNodes();
		if(middleChild != null)
			middleNum = middleChild.getNumberOfNodes();
		if(rightChild != null)
			rightNum = rightChild.getNumberOfNodes();
		return 1 + leftNum + middleNum + rightNum;
	}
	
	//Copy a node
	public TernaryNode<T> copy()
	{
		//Copy each child of the node to the new node
		//Remains null if the child is null
		TernaryNode<T> newRoot = new TernaryNode<>(data);
		if(leftChild != null)
			newRoot.setLeftChild(leftChild.copy());
		if(middleChild != null)
			newRoot.setMiddleChild(middleChild.copy());
		if(rightChild != null)
			newRoot.setRightChild(rightChild.copy());
		return newRoot;
	}
}