import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * A binary search tree based implementation of a symbol table.
 * 
 * @author (your name), Sedgewick and Wayne, Acuna
 * @version (version)
 */

        
public class BaseBSTST<Key extends Comparable<Key>, Value> implements OrderedSymbolTable<Key, Value> {
    private Node root;

    private class Node
    {
        private final Key key;
        private Value val;
        private Node left, right;
        private int N;

        public Node(Key key, Value val, int N) {
            this.key = key; this.val = val; this.N = N;
        }
    }
    
    @Override
    public int size() {
        return size(root);
    }
    
    private int size(Node x) {
        if (x == null)
            return 0;
        else
            return x.N;
    }
    
    @Override
    public Value get(Key key) {
        return get(root, key);
    }
    
    private Value get(Node x, Key key) {
        // Return value associated with key in the subtree rooted at x;
        // return null if key not present in subtree rooted at x.
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else return x.val;
    }
    
    public Value getFast(Key key) {
        Node index = root;
        while(true) {
        	if(index == null) {
        		break;
        	}
        	int cmp = key.compareTo(index.key);
        	if(cmp==0) {
        		return index.val;
        	}
        	if(cmp<0) {
        		index = index.left;
        	}else {
        		index = index.right;
        	}
        }
        return null;
    }
    
    @Override
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }
    
    private Node put(Node x, Key key, Value val) {
        // Change key's value to val if key in subtree rooted at x.
        // Otherwise, add new node to subtree associating key with val.
        if (x == null) return new Node(key, val, 1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, val);
        else if (cmp > 0) x.right = put(x.right, key, val);
        else x.val = val;
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }
    
    public void putFast(Key key, Value value) {
		if(root == null) {
			root = new Node(key, value, 1);
			return;
		}
		Node index = root, next = root;
		boolean increment = true;
		if(contains(key)) {
			increment = false;
		}
    	while(true) {
    		int cmp = key.compareTo(index.key);
    		if(increment) {
    			index.N++;
    		}
    		if(cmp<0) {
    			next = index.left;	
        		if(next == null) {
        			index.left = new Node(key, value, 1);
        			return;
        		}
        		index = next;
    		}
    		if(cmp>0) {
    			next = index.right;	
        		if(next == null) {
        			index.right = new Node(key, value, 1);
        			return;
        		}
        		index = next;
    		}
    		if(cmp==0) {
    			index.val = value;
    			return;
    		}
    	}
    }
    
    @Override
    public Key min() {
      return min(root).key;
    }
    
    private Node min(Node x) {
        if (x.left == null)
            return x;
        return min(x.left);
    }
    
    @Override
    public Key max() {
      return max(root).key;
    }
    
    private Node max(Node x) {
        if (x.right == null) return x;
        return max(x.right);
    }
    
    @Override
    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null)
            return null;
        return x.key;
    }
    
    private Node floor(Node x, Key key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp < 0) return floor(x.left, key);
        Node t = floor(x.right, key);
        if (t != null) return t;
        else return x;
    }
    
    @Override
    public Key select(int k) {
        return select(root, k).key;
    }
    
    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k-t-1);
        else return x;
    }
    
    @Override
    public int rank(Key key) {
        return rank(key, root);
    }
    
    private int rank(Key key, Node x) {
        // Return number of keys less than x.key in the subtree rooted at x.
        if (x == null) return 0;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return rank(key, x.left);
        else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right);
        else return size(x.left);
    }
    
    @Override
    public void deleteMin() {
        root = deleteMin(root);
    }
    
    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }
    
    @Override
    public void delete(Key key) {
        root = delete(root, key);
    }
    
    private Node delete(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = delete(x.left, key);
        else if (cmp > 0) x.right = delete(x.right, key);
        else
        {
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public Iterable<Key> keys() {
        return keys(min(), max());
    }
    
    @Override
    public Iterable<Key> keys(Key lo, Key hi)
    {
        Queue<Key> queue = new LinkedList<>();
        keys(root, queue, lo, hi);
        return queue;
    }
    
    private void keys(Node x, Queue<Key> queue, Key lo, Key hi)
    {
        if (x == null) return;
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);
        if (cmplo < 0) keys(x.left, queue, lo, hi);
        if (cmplo <= 0 && cmphi >= 0) queue.add(x.key);
        if (cmphi > 0) keys(x.right, queue, lo, hi);
    }
    
    @Override
    public boolean contains(Key key) {
        Node index = root;
        while(true) {
        	if(index == null) {
        		break;
        	}
        	int cmp = key.compareTo(index.key);
        	if(cmp==0) {
        		return true;
        	}
        	if(cmp<0) {
        		index = index.left;
        	}else {
        		index = index.right;
        	}
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        if (root == null) return true;
        return false;
    }

    @Override
    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null)
            return null;
        return x.key;
    }
    
    private Node ceiling(Node x, Key key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp > 0) return ceiling(x.right, key);
        Node t = ceiling(x.left, key);
        if (t != null) return t;
        else return x;
    }
    
    @Override
    public void deleteMax() {
        root = deleteMax(root);
    }
    
    private Node deleteMax(Node x) {
        if (x.right == null) return x.left;
        x.right = deleteMax(x.right);
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public int size(Key lo, Key hi) {
    	if(root == null) {
    		return 0;
    	}
        return size(root, lo, hi);
    }
    
    private int size(Node x, Key lo, Key hi) {
    	if(x == null) return 0;
    	int cmp = x.key.compareTo(lo);
    	if(cmp<0) {
    		return size(x.right, lo, hi);
    	}
    	cmp = x.key.compareTo(hi);
    	if(cmp>0) {
    		return size(x.left, lo, hi);
    	}
    	return size(x.left, lo, hi) + size(x.right, lo, hi) + 1;	
    }
    
    public void balance() {
    	Vector<Node> myNodes = new Vector<Node>();
    	getNodesList(root, myNodes);
    	root = balance(myNodes, 0, myNodes.size()-1);
    }
    
    private void getNodesList(Node head, Vector<Node> myNodes) {
        if (head == null) 
            return; 
 
        getNodesList(head.left, myNodes); 
        myNodes.add(head); 
        getNodesList(head.right, myNodes);
		
	}

	private Node balance(Vector<Node> nodes, int lo, int hi) {
    	if(lo > hi) {
    		return null;
    	}
		
    	int mid = (hi+lo)/2;
    	Node node = nodes.get(mid); 
    	
    	node.left = balance(nodes, lo, mid - 1); 
    	node.right = balance(nodes, mid + 1, hi); 
  
		return node;
    }
    
    public void printLevel(Key key) {
    	Queue<Key> myQueue = new LinkedList<>();
    	int rank = rank(key);
    	for(int i =0;i<height(root);i++) {
    		printLevel(root,i,myQueue);
    	}
    	printLevel(root, rank, myQueue);
    	for(Key value : myQueue) {
    		System.out.print(value + " ");
    	}
    	System.out.println();
    }
    
    private int height(Node index)  
    { 
        if (index == null) 
            return 0; 

            int leftHeight = height(index.left); 
            int rightHeight = height(index.right); 
   
            
            if (leftHeight > rightHeight) 
                return ++leftHeight; 
            return ++rightHeight; 
    } 
    
    private void printLevel(Node root, int rank, Queue<Key> queue) {
    	if(root == null) {
    		return;
    	}
    	
    	if(rank == 1) {
    		queue.add(root.key);
    	}
    	
    	printLevel(root.left, rank-1, queue);
    	printLevel(root.right, rank-1, queue);
    	
    }

    /**
     * entry point for testing.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BaseBSTST<Integer, String> bst = new BaseBSTST();
        
        bst.put(10, "TEN");
        bst.put(3, "THREE");
        bst.put(1, "ONE");
        bst.put(5, "FIVE");
        bst.put(2, "TWO");
        bst.put(7, "SEVEN");
        bst.put(8, "Eight");
        
        System.out.println("rank: " + bst.rank(bst.max()));
        System.out.println("size: " + bst.size(0,1));
        
        System.out.println("Contains: " + bst.contains(1));
        System.out.println("isEmpty: " + bst.isEmpty());
        
        System.out.println("get: " + bst.get(55));
        System.out.println("getFast: " + bst.getFast(55));       
        
        // Test PUT
//        System.out.println("Size: " + bst.size());
//        bst.put(15, "FIFTEEN");
//        System.out.println("put_Size: " + bst.size());
//        bst.putFast(16, "SIXTEEN");
//        System.out.println("putFast_Size: " + bst.size());
        
        // test deleteMax
//        System.out.println("getMax: " + bst.max());
//        bst.deleteMax();
//        System.out.println("Delete_Size: " + bst.size());
//        System.out.println("Max_Contains: " + bst.contains(16));
        
        // Test floor/ceil
//        System.out.println("Floor: "+  bst.get(bst.floor(6)));
//        System.out.println("Ceil: "+  bst.get(bst.ceiling(6)));
        
        // Test size()
        System.out.println("Size_Test: "+  bst.size(2,3));
        
        System.out.println("Before balance:");
        bst.printLevel(10); //root
        
        System.out.println("After balance:");
        bst.balance();
        bst.printLevel(10); //root
    }
}