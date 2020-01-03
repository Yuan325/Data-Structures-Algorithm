import java.io.*;
import java.util.*;

// Class DelivA does the work for deliverable DelivA of the Prog340

public class DelivA {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivA( File in, Graph gr ) {
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
		
		if (g.getNodeList().size() > 0) {
			for ( Edge m : g.getEdgeList()) {
				String t = "Edge from " + m.getTail().getName() + " to " + m.getHead().getName() + " labeled " + m.getLabel() + ".";
				System.out.println(t);
				output.println(t);
			}
		}
		output.close();
	}
	

}