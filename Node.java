import java.util.*;

// A node of a graph for the Spring 2018 ICS 340 program

public class Node implements Cloneable{

	String name;
	String val;  // The value of the Node
	String abbrev;  // The abbreviation for the Node
	ArrayList<Edge> outgoingEdges;  
	ArrayList<Edge> incomingEdges;
	Boolean visited;
	Boolean visitedOld;
	int start;
	int end;
	int successRate; // if the node had previously successful to reach Goal
	
	// variables for course pre-requisite
	char[] days;
	int dayIndex;
	String day;
	
	public Node( String theAbbrev ) {
		setAbbrev( theAbbrev );
		val = null;
		name = null;
		outgoingEdges = new ArrayList<Edge>();
		incomingEdges = new ArrayList<Edge>();
		visited = false;
		visitedOld = false;
		start = 0;
		end = 0;
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
	
	public String getAbbrev() {
		return abbrev;
	}
	
	public String getName() {
		return name;
	}
	
	public String getVal() {
		return val;
	}
	
	public ArrayList<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}
	
	public ArrayList<Edge> getIncomingEdges() {
		return incomingEdges;
	}
	
	public void setAbbrev( String theAbbrev ) {
		abbrev = theAbbrev;
	}
	
	public void setName( String theName ) {
		name = theName;
	}
	
	public void setVal( String theVal ) {
		val = theVal;
	}
	
	public void addOutgoingEdge( Edge e ) {
		outgoingEdges.add( e );
	}
	
	public void addIncomingEdge( Edge e ) {
		incomingEdges.add( e );
	}
	
	public void setVisited(boolean e) {
		visited = e;
	}
	
	public boolean getVisited() {
		return visited;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setVisitedOld(boolean x) {
		visitedOld = x;
	}
	
	public boolean getVisitedOld() {
		return visitedOld;
	}
	
	// change the day of course via days array (change to the next day)
	public void changeDay() {
		// if it is the last day, change back to the first day
		if (dayIndex == days.length - 1) {
			day = Character.toString(days[0]);
			dayIndex = 0;
		}
		else {
			dayIndex++;
			day = Character.toString(days[dayIndex]);
		}
	}
	
	public String getDay() {
		return day;
	}
	
	// set first day to course
	public void setDay() {
		days = new char[val.length()];
		for (int i = 0; i < val.length(); i++) {
			days[i] = val.charAt(i);
		}
		dayIndex = 0;
		day = Character.toString(days[0]);
	}
	
	
}
