import java.io.*;
import java.util.*;

class BSTNode<T>
{
	T key;
	BSTNode<T> left, right;
	BSTNode( T key, BSTNode<T> left, BSTNode<T> right)
	{
		this.key = key;
		this.left = left;
		this.right = right;
	}
}

class Queue<T>
{
	LinkedList<BSTNode<T>> queue;
	Queue() {queue = new LinkedList<BSTNode<T>>(); }
	boolean empty() { return queue.size() == 0; }
	void enqueue(BSTNode<T> node ) { queue.addLast( node ); }
	BSTNode<T> dequeue() { return queue.removeFirst(); }
	//THROWS NO SUH ELEMENT EXCEPTION IF QUEUE EMPTY
}

class BSTreePP<T>
{
	private BSTNode<T> root;
	private int nodeCount;
	private boolean addAttemptWasDupe = false;
	
	//DEFAULT CONSTRUCTOR
	public BSTreePP()
	{
		nodeCount = 0;
		root = null;
	}
	
	//INPUT FROM FILE CONSTRUCTOR
	@SuppressWarnings("unchecked")
	public BSTreePP( String infileName ) throws Exception
	{
		nodeCount = 0;
		root = null;
		BufferedReader infile = new BufferedReader( new FileReader( infileName ) );
		while( infile.ready() )
			add( (T) infile.readLine() ); //this cast produces the warning (casting from file to T elem)
		infile.close();
	}
	
	//COPY CONSTRUCTOR
	public BSTreePP( BSTreePP<T> other )
	{
		nodeCount = 0;
		root = null;
		
		addNodesInPreOrder( other.root );
	}
	private void addNodesInPreOrder( BSTNode<T> otherBSTNode )
	{
		if( otherBSTNode == null ) return;
		this.add( otherBSTNode.key );
		this.addNodesInPreOrder( otherBSTNode.left );
		this.addNodesInPreOrder( otherBSTNode.right );
	}
	
	//DUPES BOUNCE OFF & RETURN FALSE ELSE INCR COUNT & RETURN TRUE
	@SuppressWarnings("unchecked")
	public boolean add( T key )
	{
		addAttemptWasDupe = false;
		root = addHelper(  this.root, key );
		if(!addAttemptWasDupe) ++nodeCount;
		return !addAttemptWasDupe;
	}
	
	@SuppressWarnings("unchecked")
	private BSTNode<T> addHelper( BSTNode<T> root, T key )
	{
		if(root == null) return new BSTNode<T>(key,null,null);
		int comp = ((Comparable)key).compareTo( root.key );
		if(comp == 0 )
		{
			addAttemptWasDupe = true;
			return root;
		}
		else if( comp < 0 )
			root.left = addHelper( root.left, key );
		else
			root.right = addHelper( root.right, key );
		
		return root;
	} //end of addHelper
	
	//SIZE
	public int size()
	{
		return nodeCount;
	}
	//COUNT NODES
	public int countNodes()
	{
		return countNodes( this.root );
	}
	private int countNodes( BSTNode<T> root )
	{
		if( root == null ) return 0;
		return 1 + countNodes( root.left ) + countNodes( root.right );
	}
	//COUNT LEVELS
	public int countLevels()
    {
		return countLevels( root ); 
    }
    private int countLevels( BSTNode root)
    {
		if (root==null) return 0;
		return 1 + Math.max( countLevels(root.left), countLevels(root.right) );
    }
	public int[] calcLevelCounts()
	{
		int levelCounts[] = new int[countLevels()];
		calcLevelCounts( root, levelCounts, 0 );
		return levelCounts;
	}
	private void calcLevelCounts( BSTNode root, int levelCounts[], int level )
	{
		if (root==null)return;
		++levelCounts[level];
		calcLevelCounts( root.left, levelCounts, level+1 );
		calcLevelCounts( root.right, levelCounts, level+1 );
	}
	public BSTreePP<T> makeBalancedCopyOf( )
	{
		ArrayList<T> keys = new ArrayList<T>();
		keys = arrayListFiller( keys, this.root );
		
		BSTreePP<T> balancedBST = new BSTreePP<T>();
		int lo = 0;
		int hi = ( keys.size() - 1 );
		addKeysInBalancedOrder(keys, lo, hi, balancedBST);
		
		return balancedBST;
	}
	private ArrayList<T> arrayListFiller(ArrayList<T> toFill, BSTNode<T> thisRoot)
	{
		if(thisRoot == null) return toFill;
		
		arrayListFiller( toFill, thisRoot.left );
		toFill.add( thisRoot.key );
		arrayListFiller( toFill, thisRoot.right );
		
		return toFill;
	}
	private void addKeysInBalancedOrder(ArrayList<T> keys, int lo, int hi, BSTreePP<T> balancedBST)
	{
		if(lo > hi) return;
		int mid = ((hi+lo)/2);
		balancedBST.add(keys.get(mid));
		addKeysInBalancedOrder(keys,mid+1,hi,balancedBST);
		addKeysInBalancedOrder(keys,lo,mid-1,balancedBST);
	}
	public void prettyPrint( )
	{
		int width = this.countNodes();
		int depth = this.countLevels();
		//T[][] prettyMatrix = new T[depth][width];
		if(depth == width)
			prettyPrintLine( this.root );
		else
			prettyPrintOther( this.root );
	
	}
	private void prettyPrintLine( BSTNode<T> root )
	{	
		
		int space = 0;
		
			ArrayList<T> list = new ArrayList<T>();
			list = arrayListFiller(list, root);
			while(space < list.size())
			{
				for(int i = 0; i < space; i++)
				{
					System.out.print(" ");
				}
				System.out.println(list.get(space));
				space++;
			}
		
	}	
	private void prettyPrintOther( BSTNode<T> root )
	{
		int space = 0, counter = 0; //counter used to keep track of what level we are on
		
		boolean newLevel = true;// checks if we move levels
		
		int[] levelCounts = this.calcLevelCounts();
		
		
		ArrayList<T> list = new ArrayList<T>();
		ArrayList<T> indexList = new ArrayList<T>();
		
		indexList = arrayListFiller(indexList, root);
		list = addInLevelOrder(list, root);
		
			for(int j = 0; j < list.size(); j++)
			{
				//ENTERS HERE IF WE MUST PRINT ON SAME LEVEL
				if(!newLevel)
				{
					space = (indexList.indexOf(list.get(j)) - (indexList.indexOf(list.get(j-1))) - 1 ); //very ugly but it works?
					
					for(int i = 0; i<space; i++)
					{
						System.out.print(" ");
					}
					System.out.print(list.get(j));
				}
				else //ENTERS HERE IF NEW LEVEL
				{
					space = indexList.indexOf(list.get(j));
					for(int i = 0; i<space; i++)
					{
						System.out.print(" ");
					}
				
					System.out.print(list.get(j));
				}
				//checks to see if there are more than one node on level and if there is newLevel is set to false
				if(levelCounts[counter] > 1)
				{
					newLevel = false;
					levelCounts[counter]--;
				}
				else
				{
					newLevel = true;
					System.out.println();
					counter++;
				}
		
			}
		} //END PRETTY PRINT
	
	private void printLevel(BSTNode<T> root, int level)
	{
		if (root == null) 
			return; 
		if (level == 1) 
			System.out.print(root.key); 
		else if (level > 1) 
		{ 
        printLevel(root.left, level-1); 
        printLevel(root.right, level-1); 
		} 
	}
	public ArrayList<T> addInLevelOrder(ArrayList<T> list, BSTNode<T> root)
	{	if (this.root == null) return list;
		Queue<T> q = new Queue<T>();
		q.enqueue( this.root ); // this. just for emphasis/clarity
		while ( !q.empty() )
		{	BSTNode<T> n = q.dequeue();
			list.add(n.key);
			if ( n.left  != null ) q.enqueue( n.left );
			if ( n.right != null ) q.enqueue( n.right );
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
}