import java.io.*;
import java.util.Stack;

// Class DelivB does the work for deliverable DelivB of the Prog340

public class DelivB{

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;

	Node startNode;
	Node endNode;

	// to set discovery and finishing time for nodes
	int count = 1;
	
	int bestPath = 0;
	int currentDistance = 0 ;
	Stack<Node> stack = new Stack<>();

	// for output string formatting
	String format = "%-20s %-13s %-10s \n";

	boolean reachedGoal = false;
	boolean searchDFS_output;

	@SuppressWarnings("unchecked")
	public DelivB( File in, Graph gr ) {
		inputFile = in;
		g = gr;


		// Get output file name.
		String inputFileName = inputFile.toString();
		String baseFileName = inputFileName.substring( 0, inputFileName.length()-4 ); // Strip off ".txt"
		String outputFileName = baseFileName.concat( "_out.txt" );
		outputFile = new File( outputFileName );
		if ( outputFile.exists() ) {    // For retests
			outputFile.delete();
		}

		try {
			output = new PrintWriter(outputFile);			
		}
		catch (Exception x ) { 
			System.err.format("Exception: %s%n", x);
			System.exit(0);
		}

		// search for start node and end node
		if (g.getNodeList().size()>0) {
			for (Node m : g.getNodeList()) {
				if (m.getVal().equalsIgnoreCase("S")) {
					startNode = m;
				}
				if (m.getVal().equalsIgnoreCase("G")) {
					endNode = m;
				}
			}
		}

		// start first depth first search
		depthFirstSearchCall(startNode);

		// print start and end time
		System.out.println("Node\t\tStart Time\tEnd Time\n");
		output.println("Node\t\tStart Time\tEnd Time\n");
		outputDiscovery();

		System.out.println();
		output.println();

		// get the current best path before running the seccond DFS
		currentDistance();
		bestPath = currentDistance + 1;

		// set successRate for current node and edge
		for (int i = 0; i < stack.size()-1; i++) {
			stack.get(i).setVisitedOld(true);
			stack.get(i).setSuccessRate(1);
			for (Edge m: stack.get(i).getOutgoingEdges())
				if (m.getHead() == stack.get(i+1))
					m.setSuccessRate(1);
		}

		// 2nd DFS for backtracking
		depthFirstSearchcall2();

		output.close();
	}

	// initial depth first search call
	public void depthFirstSearchCall(Node node) {
		int distance = 0;
		Edge currentEdge = null;

		// getting the initial distance (first distance of the outgoing edge)
		for (Edge m: node.getOutgoingEdges()) {
			if(m.getHead().getVisited() == false)
				if (m.getVisited() == false && Integer.parseInt(m.getLabel()) != 0) {
					distance = Integer.parseInt(m.getLabel());
					break;
				}
		}

		Node nextNode = null;
		// set start time
		if(node.getVisited() == false) {
			node.setStart(count);
			if (reachedGoal == false) {
				stack.push(node);
				if (node == endNode)
					reachedGoal = true;
			}
			count ++;
			node.setVisited(true);
		}
		// get next node to visit
		for (Edge m: node.getOutgoingEdges()) {
			if (m.getHead().getVisited() == false) {
				if(Integer.parseInt(m.getLabel())<=distance) {
					distance = Integer.parseInt(m.getLabel());
					nextNode = m.getHead();
					currentEdge = m;
				}
			}
		}
		// recursive
		// if next node is null (disconnected graph), look for an unvisited node and explore that
		if (count < g.getNodeList().size()*2+1) {
			if(nextNode != null) {
				currentEdge.setVisited(true);
				depthFirstSearchCall(nextNode);

			}
			else {
				for (int i = stack.size(); i>0; i--) {
					Node s = stack.peek();
					for (Edge m: s.getOutgoingEdges()) {
						if(m.getHead().getVisited() == false) {
							distance = Integer.parseInt(m.getLabel());
							nextNode = m.getHead();
							currentEdge = m;
						}
						if(nextNode != null)
							break;
					}
					if(nextNode != null) {
						break;
					} else{
						// when pop, also set end time
						if (reachedGoal == false) {
							for (Edge m: stack.get(stack.size()-2).getOutgoingEdges()) {
								if (m.getHead() == stack.peek())
									m.setSuccessRate(-1);
							}
							s.setSuccessRate(-1);
							stack.pop();
							s.setEnd(count);
							count++;
						}
					}
				}

				if (nextNode != null) {
					currentEdge.setVisited(true);
					depthFirstSearchCall(nextNode);
				}
			}
			//find next available unvisited node
		}

		// set end time for each node
		// if end time is already added, then ignore it
		if(node.getVisited() == true) {
			if (node.getEnd() == 0) {
				node.setEnd(count);
				count++;
			}
		}

	}
	
	
	// 2nd depth first search
	public void depthFirstSearchcall2() {
		removeVisited();
		Node s = stack.peek();
		// if the top of stack is endNode, change boolean to true, and start the backtracking
		if(s == endNode) {
			bestPath = currentDistance;
			reachedGoal = true;
		}
		// if not yet reach endNode, keep popping out until reaches end node
		if(reachedGoal == false) {
			for(int i = stack.size()-1; i>0; i--){
				if (stack.get(i) != endNode) {
					stack.pop();
				}
				else {
					reachedGoal = true;
					// let s be the top of the stack
					s = stack.peek();
					break;
				}
			}
		}

		// if all of edge S is visited, then pop it and assign new S;
		boolean y = true;
		if (s != endNode && s != startNode) {
			for (Edge m: s.getOutgoingEdges()) {
				if (m.getVisited() == false) {
					y = false;
					break;
				}
			} 
			if (y == true) {
				stack.pop();
				s = stack.peek();
			}
		}
		// if s is start node, we want to keep it for now (in case there are only 2 element in the stack)
		if (s != startNode)
			stack.pop();
		
		if (s == startNode) {
			for (Edge m: s.getOutgoingEdges())
				if (m.getVisited() == false) {
					y = false;
				}
		}

		// if s is startNode, print the last path
		if(s == startNode && y == true) {
			currentDistance = 0;
			for(Edge n: s.getOutgoingEdges())
				if(n.getHead().equals(endNode)) {
					currentDistance = Integer.parseInt(n.getLabel());
					if (currentDistance < bestPath) {
						bestPath = currentDistance;
						System.out.print("Path ");
						output.print("Path ");
						System.out.println(s.getAbbrev()+ "-" + endNode.getAbbrev()+" = " + bestPath);
						output.println(s.getAbbrev()+ "-" + endNode.getAbbrev()+" = " + bestPath);
					}
				}
		}
		// else if s is endNode, print the first longest path
		else if(s == endNode) {
			currentDistance();
			bestPath = currentDistance;
			currentDistance = 0;
			printPath();
			if (stack.peek() != startNode)
				stack.pop();
			depthFirstSearchcall2();
		}
		
		else {
			// if the searchDFS function return true
			// check if it is the best path
			// if yes, replace the best path and run dfs2 again
			if (searchDFS(s) == true) {
				setSuccessRate();
				currentDistance();
				// compare current distance with best path
				// if it is true, print the path
				if (currentDistance <= bestPath) {
					bestPath = currentDistance;
					currentDistance = 0;
					printPath();
					stack.pop();
					depthFirstSearchcall2();

				}
				else {
					stack.pop();
					depthFirstSearchcall2();
				}
			}
			// else, don't replace path and continue running dfs2
			else {
				boolean p = true;
				for (Edge m: s.getOutgoingEdges())
					if (m.getVisited() == false) {
						p = false;
					}
				if (p == false)
					stack.push(s);

				depthFirstSearchcall2();
			}

		}
	}

	// boolean to check if alternate path will get to goal
	public boolean searchDFS(Node s) {
		int distance = 0;
		Node nextNode = null;
		Edge currentEdge = null;

		// if it reaches the end node, return true
		if (s == endNode) {
			searchDFS_output = true;
			return searchDFS_output;
		}
		// if it is not the end node, continue to run
		else {

			// get current distance without path to goal
			currentDistanceWithoutGoal(s);

			// find initial distance for unvisited edge and edge.getHead()
			for (Edge m: s.getOutgoingEdges()) {
				if(m.getHead().getVisited() == false && m.getSuccessRate() != -1 && m.getHead().getSuccessRate() != -1)
					if (m.getVisited() == false && Integer.parseInt(m.getLabel()) != 0) {
						distance = Integer.parseInt(m.getLabel());
						break;
					}
			}

			// find the shortest distance among unvisited edge and edge.getHead()
			for (Edge m: s.getOutgoingEdges()) {
				if(m.getHead().getVisited() == false && m.getSuccessRate() != -1 && m.getHead().getSuccessRate() != -1)
					if (m.getVisited() == false && Integer.parseInt(m.getLabel()) != 0 && Integer.parseInt(m.getLabel()) < distance) {
						distance = Integer.parseInt(m.getLabel());
					}
			}

			// get the next node and edge
			for (Edge m: s.getOutgoingEdges()) {
				// make sure all condition fulfilled before moving to the next Node
				if(m.getHead().getVisited() == false && m.getVisited() == false && Integer.parseInt(m.getLabel()) <= distance && m.getSuccessRate() != -1 && m.getHead().getSuccessRate() != -1 && Integer.parseInt(m.getLabel()) != 0 ) {
					currentDistance  += Integer.parseInt(m.getLabel());
					if (currentDistance < bestPath) {
						nextNode = m.getHead();
						currentEdge = m;
						m.setVisited(true);
						break;
					}
					else {
						currentDistance -= Integer.parseInt(m.getLabel());
						m.setVisited(true);
					}
				}
				else {
					m.setVisited(true);
				}
			}

			// if nextNode is a next unvisited node for a node, go there and explore it
			if (nextNode != null && s.getVisited() == false) {
				currentEdge.setVisited(true);
				stack.push(s);
				// if the next node is a node that is previously visited, remove all edge visited (we want to explore the node again)
				if (nextNode.getVisitedOld() == true) {
					removeEdgeVisited(nextNode);
				}
				searchDFS(nextNode);
				// if the path does not go to goal, pop those nodes out from the stack
				if (searchDFS_output != true) {
					stack.pop();
				}
			}
			else {
				searchDFS_output = false;
				return searchDFS_output;
			}
			return searchDFS_output;
		}
	}


	// output for start and end time
	public void outputDiscovery(){
		removeVisited();
		for (int i = 1; i <= g.getNodeList().size()*2; i++) {
			for (Node n : g.getNodeList()) {
				if(n.getVisited() == false)
					if (n.getStart() == i) {
						System.out.printf(format, n.getName(), n.getStart(), n.getEnd());
						output.printf(format, n.getName(), n.getStart(), n.getEnd());
						n.setVisited(true);
					}
			}
		}

	}

	// make all nodes as unvisited
	public void removeVisited() {
		for (Node n: g.getNodeList())
			n.setVisited(false);
	}

	// remove all visited edge
	public void removeEdgeVisited(Node n) {
		for (Edge m: n.getOutgoingEdges())
			m.setVisited(false);
	}


	// for checking purposes; to check what is in the stack
	public void printStack() {
		for (int i = 0; i<stack.size();i++) {
			System.out.print(stack.get(i).getAbbrev() + "-");
		}
		System.out.println();
	}

	// calculate current distance
	public void currentDistance() {
		currentDistance = 0;
		for (int i = 0; i<stack.size()-1;i++) {
			for (Edge m: stack.get(i).getOutgoingEdges()) {
				if(m.getHead().equals(stack.get(i+1))) {
					currentDistance += Integer.parseInt(m.getLabel());
					m.setVisited(true);
				}
			}
		}

		for (Edge m: stack.peek().getOutgoingEdges()) {
			if(m.getHead().equals(endNode)) {
				currentDistance += Integer.parseInt(m.getLabel());
				m.setVisited(true);
				break;
			}
		}
	}

	// get current distance without including goal (for searchDFS)
	public void currentDistanceWithoutGoal(Node s) {
		currentDistance = 0;
		for (int i = 0; i<stack.size()-1;i++) {
			stack.get(i).setVisited(true);
			for (Edge m: stack.get(i).getOutgoingEdges()) {
				if(m.getHead().equals(stack.get(i+1))) {
					currentDistance += Integer.parseInt(m.getLabel());
					m.getHead().setVisited(true);
					m.setVisited(true);
				}
			}
		}
		if (stack.size() == 2) {
			stack.get(0).setVisited(true);
			stack.get(1).setVisited(true);
			for (Edge m: stack.get(0).getOutgoingEdges()) {
				if (m.getHead() == stack.get(1)) {
					currentDistance += Integer.parseInt(m.getLabel());
					m.setVisited(true);
				}
			}
		}
		if (stack.size() == 1) {
			stack.get(0).setVisited(true);
			for (Edge m: stack.get(0).getOutgoingEdges()) {
				if (m.getHead() == s) {
					m.setVisited(true);
				}
			}
		}
	}

	// print path
	public void printPath() {
		System.out.print("Path ");
		output.print("Path ");
		String outputAbbrev = "";
		for (int i = 0; i<stack.size();i++) {
			outputAbbrev += stack.get(i).getAbbrev() + "-";
		}
		System.out.println(outputAbbrev+ endNode.getAbbrev()+" = " + bestPath);
		output.println(outputAbbrev+ endNode.getAbbrev()+" = " + bestPath);
	}
	
	// set the success rate for all node and edge that are in the current stack as 1
	public void setSuccessRate() {
		for (int i = 0; i < stack.size()-1; i++) {
			stack.get(i).setSuccessRate(1);
			for (Edge m: stack.get(i).getOutgoingEdges())
				if (m.getHead() == stack.get(i+1))
					m.setSuccessRate(1);
		}
	}

}

