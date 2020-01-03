import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;

import javax.security.auth.kerberos.KerberosKey;
import javax.xml.crypto.NodeSetData;

// Class DelivD does the work for deliverable DelivD of the Prog340

/////// ADD THE OUTPUT TO FILE
/////// Check the answer! (F11 and F14)

public class DelivD {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	
	int length;
	Node[] arr;
	String[][] dMatrix; // distance matrix
	String[][] pMatrix; // predecessor matrix
	boolean negCycle = false;
	
	
	public DelivD( File in, Graph gr ) {
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
		
		// instantiating variables
		length = g.getNodeList().size() + 1;
		arr = new Node[length];
		dMatrix = new String[length][length];
		pMatrix = new String[length][length];
		
		// start Deliv D algorithm
		createMatrix();
		floydWarshall();
		
		
		output.close();
		
	}
	
	// Create distance and predecessor matrix by placing initial information (iteration 0)
	public void createMatrix() {		
		// matrix[i][j] = matrix[row][column]
		dMatrix[0][0] = "D";
		pMatrix[0][0] = "P";
		
		for (int i = 1; i<length;i++) {
			arr[i] = g.getNodeList().get(i-1);
			dMatrix[0][i] = g.getNodeList().get(i-1).getName();
			pMatrix[0][i] = g.getNodeList().get(i-1).getName(); 
			dMatrix[i][0] = g.getNodeList().get(i-1).getName();
			pMatrix[i][0] = g.getNodeList().get(i-1).getName();
		}
		
		for (int i=1; i<length;i++) {
			for (int j=1; j<length; j++) {
				if (i==j) {
					dMatrix[i][j] = "0";
					pMatrix[i][j] = "~"; 
				}
				for (Edge m : arr[i].getOutgoingEdges()) {
					if (m.getHead() == arr[j]) {
						dMatrix[i][j] = m.getLabel();
						pMatrix[i][j] = m.getTail().getName();
					}
				}
				if (dMatrix[i][j] == null) {
					dMatrix[i][j]= Integer.toString(Integer.MAX_VALUE);
					pMatrix[i][j]= "~"; 
				}
			}
		}
	}
	
	// printing both distance and predecessor matrix
	public void print() {
		String matrix = "";
		for (int i = 0; i <length; i++) {
			for (int j = 0; j< length; j++) {
				if (dMatrix[i][j].equals("2147483647")) {
					matrix += "~\t";
				}
				else {
					matrix += dMatrix[i][j] + "\t"; 
				}
			}
			matrix +="\n";
		}
		matrix +="\n";
		for (int i = 0; i <length; i++) {
			for (int j = 0; j<length; j++) {
				matrix += pMatrix[i][j] + "\t"; 
			}
			matrix +="\n";
		}
		System.out.println(matrix);
		output.println(matrix);
	}
	
	// running floyd warshall algorithm on matrix
	public void floydWarshall() {
		if (length < 10)
			print();
		for (int k = 1; k<length;k++) {
			for (int i=1; i<length; i++) {
				for (int j=1;j<length;j++) {
					if (k!=i || k != j) {
						if (!(dMatrix[i][k].equals("2147483647")) && !(dMatrix[k][j].equals("2147483647"))) {
							String ikj = Integer.toString(Integer.parseInt(dMatrix[i][k])+ Integer.parseInt(dMatrix[k][j]));
							if ((Integer.parseInt(dMatrix[i][k])+ Integer.parseInt(dMatrix[k][j])) < Integer.parseInt(dMatrix[i][j])) {
								dMatrix[i][j] = ikj;
								pMatrix[i][j]= pMatrix[k][j]; 
							}
							if (i==j && Integer.parseInt(dMatrix[i][j]) < 0)
								negCycle = true;
						}
					}
					
				}
			}
			// print all iteration if less than 10 nodes
			if (length < 10) {
				System.out.println("---------------------------------------------------------");
				output.println("---------------------------------------------------------");
				print();
			}
		}
		if (length>=10) {
			print();
		}
		if (negCycle == true) {
			System.out.println("\nThere are negative weight cycle in this graph.");
			output.println("\nThere are negative weight cycle in this graph.");
		}
		
	}
	
	
}

