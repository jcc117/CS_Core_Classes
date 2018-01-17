/**
*A class that implements the ADT TernaryTreeInterface
*/
package cs445.a5;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

public class TernaryTree<T> implements TernaryTreeInterface<T>
{
	private TernaryNode<T> root;
	
	//Initializes an empty tree
	public TernaryTree()
	{
		root = null;
	}
	
	//Initializes a tree whos root node contains rootData
	public TernaryTree(T rootData)
	{
		root = new TernaryNode<>(rootData);
	}
	
	//Initializes a tree whose root node contains rootData and whose child subtrees are leftTree, middleTree, and rightTree
	public TernaryTree(T rootData, TernaryTree<T> leftTree, TernaryTree<T> middleTree, TernaryTree<T> rightTree)
	{
		privateSetTree(rootData, leftTree, middleTree, rightTree); 
	}
	
	/** Sets the ternary tree to a new one-node ternary tree with the given data
     *  @param rootData  The data for the new tree's root node
     */
	 public void setTree(T rootData)
	 {
		 root = new TernaryNode<>(rootData);
	 }
	 
	 /** Sets this ternary tree to a new ternary tree
     *  @param rootData  The data for the new tree's root node
     *  @param leftTree  The left subtree of the new tree
     *  @param middleTree  The middle subtree of the new tree
     *  @param rightTree  The right subtree of the new tree
     */
	 public void setTree(T rootData, TernaryTreeInterface<T> leftTree, TernaryTreeInterface<T> middleTree, TernaryTreeInterface<T> rightTree)
	 {
		 privateSetTree(rootData, (TernaryTree<T>)leftTree, (TernaryTree<T>)middleTree, (TernaryTree<T>)rightTree);
	 }
	 private void privateSetTree(T rootData, TernaryTree<T> leftTree, TernaryTree<T> middleTree, TernaryTree<T> rightTree)
	 {
		//Reset the root to the new data
		root = new TernaryNode<>(rootData);
		
		//Set leftTree
		if ((leftTree != null) && !leftTree.isEmpty())
            root.setLeftChild(leftTree.root);
		
		//Set rightTree
		//If rightTree is the same thing as the middle and left trees, make a copy and
		//set rightTree equal to the copy
        if ((rightTree != null) && !rightTree.isEmpty()) 
		{
            if ((rightTree != leftTree) && (rightTree != middleTree)) 
			{
                root.setRightChild(rightTree.root);
            } 
			else 
			{
                root.setRightChild(rightTree.root.copy());
            }
        }
		
		//Set middleTree
		//If middleTree is the same thing as the right and left trees, make a copy and
		//set middleTree equal to the copy
		if ((middleTree != null) && !middleTree.isEmpty()) 
		{
            if ((middleTree != leftTree) && (middleTree != rightTree)) 
			{
                root.setMiddleChild(middleTree.root);
            } 
			else 
			{
                root.setMiddleChild(middleTree.root.copy());
            }
        }
		
		
		if ((leftTree != null) && (leftTree != this))
            leftTree.clear();
		if ((rightTree != null) && (rightTree != this))
            rightTree.clear();
		if((middleTree != null) && (middleTree != this))
			middleTree.clear();
	 }
	 
	 /** Gets the data in the root node
     *  @return  the data from the root node
     *  @throws EmptyTreeException  if the tree is empty */
    public T getRootData()
	{
		if(!isEmpty())
			return root.getData();
		else
			throw new EmptyTreeException();
	}

    /** Gets the height of the tree (i.e., the maximum number of nodes passed
     *  through from root to leaf, inclusive)
     *  @return  the height of the tree */
    public int getHeight()
	{
		return root.getHeight();
	}

    /** Counts the total number of nodes in the tree
     *  @return  the number of nodes in the tree */
    public int getNumberOfNodes()
	{
		return root.getNumberOfNodes();
	}

    /** Determines whether the tree is empty (i.e., has no nodes)
     *  @return  true if the tree is empty, false otherwise */
    public boolean isEmpty()
	{
		return root == null;
	}

    /** Removes all data and nodes from the tree */
    public void clear()
	{
		root = null;
	}
	
	/** Creates an iterator to traverse the tree in preorder fashion
    *  @return  the iterator */
    public Iterator<T> getPreorderIterator()
	{
		return new PreorderIterator();
	}
	private class PreorderIterator implements Iterator<T>
	{
		private ArrayDeque<TernaryNode<T>> nodeStack;
		
		//Create stack and push root to the stack
		public PreorderIterator()
		{
			nodeStack = new ArrayDeque<>();
			nodeStack.push(root);
		}
		//Return true if the stack isn't empty, false otherwise
		public boolean hasNext()
		{
			return !nodeStack.isEmpty();
		}
		public T next()
		{
			TernaryNode<T> nextNode;
			//Pop an element off of the stack and add its children to it in reverse order (right to left)
			//Throw exception if there is no next element
			if(hasNext())
			{
				nextNode = nodeStack.pop();
				if(nextNode.getRightChild() != null)
					nodeStack.push(nextNode.getRightChild());
				if(nextNode.getMiddleChild() != null)
					nodeStack.push(nextNode.getMiddleChild());
				if(nextNode.getLeftChild() != null)
					nodeStack.push(nextNode.getLeftChild());
			}
			else
				throw new NoSuchElementException();
			return nextNode.getData();
		}
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

   /** Creates an iterator to traverse the tree in postorder fashion
    *  @return  the iterator */
    public Iterator<T> getPostorderIterator()
	{
		return new PostOrderIterator();
	}
	private class PostOrderIterator implements Iterator<T>
	{
		private ArrayDeque<TernaryNode<T>> nodeStack;
		private TernaryNode<T> currentNode;
		public PostOrderIterator()
		{
			nodeStack = new ArrayDeque<>();
			currentNode = root;
		}
		public boolean hasNext()
		{
			return !nodeStack.isEmpty() || (currentNode != null);
		}
		public T next()
		{
			TernaryNode<T> nextNode = null;
			//Find leftmost leaf
			while(currentNode != null)
			{
				nodeStack.push(currentNode);
				if(currentNode.getLeftChild() != null)
					currentNode = currentNode.getLeftChild();
				else if(currentNode.getMiddleChild() != null)
					currentNode = currentNode.getMiddleChild();
				else if(currentNode.getRightChild() != null)
					currentNode = currentNode.getRightChild();
				else
					currentNode = null;
			}
			//If empty, end of the tree is found
			if(!nodeStack.isEmpty())
			{
				TernaryNode<T> parent = null;
				nextNode = nodeStack.pop();
				//Set currentNode to its closest sibling or null
				//If null, it will iterate again to find its parent's sibling
				if(!nodeStack.isEmpty())
				{
					//Parent is nextNode's parent node 
					parent = nodeStack.peek();
					if(nextNode == parent.getLeftChild())
						currentNode = parent.getMiddleChild();
					else if(nextNode == parent.getMiddleChild())
						currentNode = parent.getRightChild();
					else
						currentNode = null;
				}
				else
					currentNode = null;
			}
			else
				throw new NoSuchElementException();
			
			return nextNode.getData();
		}
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

   /** Creates an iterator to traverse the tree in inorder fashion
    *	This Operation is not supported for TernaryTrees. This is because if
		a tree has 3 children, there is no way to distinguish where the parent
		node would go if there are being put in order. You cannot split 3
		evenly like one could do with a BinaryTree. If you have have nodes
		A, B, C, and D and the InorderIterator gives me BCAD, there is no
		way of telling which is the parent node. This is given the fact that
		the nodes are in a full TernaryTree.
    *  @return  the iterator */
    public Iterator<T> getInorderIterator()
	{
		throw new UnsupportedOperationException();
	}

   /** Creates an iterator to traverse the tree in level-order fashion
    *  @return  the iterator */
    public Iterator<T> getLevelOrderIterator()
	{
		return new LevelOrderIterator();
	}
	private class LevelOrderIterator implements Iterator<T>
	{
		private ArrayDeque<TernaryNode<T>> q;
		public LevelOrderIterator()
		{
			q = new ArrayDeque<TernaryNode<T>>();
			if(root != null)
				q.add(root);
		}
		public boolean hasNext()
		{
			return !q.isEmpty();
		}
		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			else
			{
				//Remove the node which will be returned
				TernaryNode<T> currentNode = q.remove();
				//Add currentNode's children to the queue in order (left to right)
				if(currentNode.getLeftChild() != null)
					q.add(currentNode.getLeftChild());
				if(currentNode.getMiddleChild() != null)
					q.add(currentNode.getMiddleChild());
				if(currentNode.getRightChild() != null)
					q.add(currentNode.getRightChild());
				return currentNode.getData();
			}
		}
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}