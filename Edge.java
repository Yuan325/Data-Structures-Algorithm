//import java.util.*;

// Edge between two nodes
public class Edge {
	
	String label;
	Node tail;
	Node head;
	boolean visited;
	int successRate; // if the edge had previously successful to reach Goal
	
	
	public Edge( Node tailNode, Node headNode, String theLabel ) {
		setLabel( theLabel );
		setTail( tailNode );
		setHead( headNode );
		visited = false;
		successRate = 0;

	}
	
	public void setSuccessRate(int x) {
		// 0 unknown
		// 1 success
		// -1 unsuccessful
		
		successRate = x;
	}
	
	public int getSuccessRate() {
		return successRate;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Node getTail() {
		return tail;
	}
	
	public Node getHead() {
		return head;
	}
	
	public void setLabel( String s ) {
		label = s;
	}
	
	public void setTail( Node n ) {
		tail = n;
	}
	
	public void setHead( Node n ) {
		head = n;
	}
	
	public void setVisited(boolean x) {
		visited = x;
	}
	
	public boolean getVisited() {
		return visited;
	}
	
	
}
