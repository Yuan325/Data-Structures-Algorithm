import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;
import javax.xml.ws.AsyncHandler;

import org.omg.IOP.TaggedComponentHelper;

// Class DelivC does the work for deliverable DelivC of the Prog340

public class DelivC {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;

	// courses won't change once assigned
	// Each course have their individual index
	Node[] courses = new Node[21];
	// arrays to keep track of semester and number of conflict for each course based on index for course
	int[] semesters = new int[21];
	int[] courseConflict = new int[21];
	
	int totalConflict = 0;

	public DelivC( File in, Graph gr ) {
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

		// add all nodes into an array
		// this array won't be modified throughout the code (each course have their individual index)
		int count = 0;
		for (Node m: g.getNodeList()) {
			courses[count] = m;
			count++;
		}

		// run constrain satisfaction
		randomStart();
		solution();
		
		// print final schedule
		System.out.println("Final schedule: ");
		output.println("Final schedule: ");
		printSchedule();
		// print course semester number
		System.out.println("\n\t\t\tFall 2019: 20203\tSpring 2020: 20205\nSummer 2020: 20211\tFall 2020: 20213\tSpring 2021: 20215\nSummer 2021: 20221\tFall 2021: 20223\tSpring 2022: 20225");
		output.println("\n\t\t\tFall 2019: 20203\tSpring 2020: 20205\nSummer 2020: 20211\tFall 2020: 20213\tSpring 2021: 20215\nSummer 2021: 20221\tFall 2021: 20223\tSpring 2022: 20225");
		

		output.close();
	}

	// random start to assign random semester to each course
	// Reference source: 
	// 		https://www.geeksforgeeks.org/randomly-select-items-from-a-list-in-java/
	// 		https://www.geeksforgeeks.org/initializing-a-list-in-java/
	public void randomStart() {
		List<Integer> list = new ArrayList<>(Arrays.asList(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7));
		Random r = new Random();
		for (int course = 0; course < 21; course++) {
			int randomIndex = r.nextInt(list.size());
			semesters[course] = list.get(randomIndex);
			list.remove(randomIndex);
			courses[course].setDay();
		}
	}
	
	public void printSchedule() {
		String sem1 = "20203:\t", sem2= "20205:\t", sem3= "20211:\t", sem4= "20213:\t", sem5= "20215:\t", sem6= "20221:\t", sem7= "20223:\t";

		for (int course = 0; course < 21; course++) {
			if(semesters[course] == 1)
				sem1 += courses[course].getName() + "-" + courses[course].getDay() + "\t";
			if(semesters[course] == 2)
				sem2 += courses[course].getName() + "-" + courses[course].getDay() + "\t";
			if(semesters[course] == 3)
				sem3 += courses[course].getName() + "-" + courses[course].getDay() + "\t";
			if(semesters[course] == 4)
				sem4 += courses[course].getName() + "-" + courses[course].getDay() + "\t";
			if(semesters[course] == 5)
				sem5 += courses[course].getName() + "-" + courses[course].getDay() + "\t";
			if(semesters[course] == 6)
				sem6 += courses[course].getName() + "-" + courses[course].getDay() + "\t";
			if(semesters[course] == 7)
				sem7 += courses[course].getName() + "-" + courses[course].getDay() + "\t";
		}

		System.out.println(sem1);
		System.out.println(sem2);
		System.out.println(sem3);
		System.out.println(sem4);
		System.out.println(sem5);
		System.out.println(sem6);
		System.out.println(sem7);
		System.out.println();
		output.println(sem1);
		output.println(sem2);
		output.println(sem3);
		output.println(sem4);
		output.println(sem5);
		output.println(sem6);
		output.println(sem7);
		output.println();
		
		
	}

	// For each course, check semester and compare it to every other course in outgoing edges
	// Compare based on label (> and >=)
	// Count number of conflict for each course and total number of conflict
	public void countConflict() {
		totalConflict = 0;

		for (int course = 0; course<21; course++) {
			int conflict = 0;
			// check outgoing edge for conflict
			for (Edge m: courses[course].getOutgoingEdges()) {
				if (m.getLabel().equals(">")) {
					for (int i=0; i<21;i++) 
						if (m.getHead() == courses[i]) 
							if (semesters[course] <= semesters[i])
								conflict++;
				}
				if (m.getLabel().equals(">=")) {
					for (int i=0; i<21;i++)
						if(m.getHead() == courses[i])
							if (semesters[course]< semesters[i])
								conflict++;
				}
			}
			// check incoming edge for conflict
			for (Edge m: courses[course].getIncomingEdges()) {
				if (m.getLabel().equals(">")) {
					for (int i=0; i<21; i++)
						if (m.getTail() == courses[i])
							if (semesters[course]>=semesters[i])
								conflict ++;
				}
				if (m.getLabel().equals(">=")) {
					for (int i=0; i<21; i++)
						if (m.getTail() == courses[i])
							if (semesters[course]> semesters[i])
								conflict ++;
				}
			}
			
			chooseDays();
			// also check course days as conflict
			for (int x = 0; x < 21; x++) {
				if (semesters[course] == semesters[x] && course != x)
					if (courses[course].getDay().equals(courses[x].getDay()))
						conflict ++;
			}

			courseConflict[course] = conflict;
			totalConflict += conflict;
		}

	}

	// swap courses to improve the schedule (reduce conflict)
	public void improve() {
		chooseDays();
		int currentConflict = totalConflict;
		int maxConflict = 0;
		int maxConflictIndex = 0;
		// get the course with maximum number of conflict
		for (int course = 0; course < 21; course++) {
			if (courseConflict[course]>= maxConflict) {
				maxConflict = courseConflict[course];
				maxConflictIndex = course;
			}
		}
		
		// find another course to swap
		int swapIndex = 0;
		int swapConflict = 0;
		
		// pick a random semester to swap
		List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
		Random r = new Random();
		int randomIndex = r.nextInt(list.size());
		int randomSemester = list.get(randomIndex);
		int count = 0;
		
		// pick a course with the highest number of conflict from the random semester
		while (swapConflict == 0 || count <= 21) {
			for (int course = 0; course < 21; course++) {
				if (semesters[course] == randomSemester && courseConflict[course] >= swapConflict) {
					swapIndex = course;
					swapConflict = courseConflict[course];
					count ++;
				}
			}
			// if all 3 course in that semester have 0 conflict, pick another random semester
			if (count >=21 && swapConflict == 0) {
				randomSemester = r.nextInt(list.size());
			}
		}
		
		// swap the courses
		int temp = semesters[maxConflictIndex];
		semesters[maxConflictIndex] = semesters[swapIndex];
		semesters[swapIndex] = temp;
		chooseDays();
		countConflict();

		// if total conflict increases after swapping, undo the swap
		if (totalConflict > currentConflict) {
			temp = semesters[maxConflictIndex];
			semesters[maxConflictIndex] = semesters[swapIndex];
			semesters[swapIndex] = temp;
			chooseDays();
		}
		countConflict();
	}

	public void solution() {
		countConflict();
		int count = 0;
		// while total number of conflict is not 0, keep improve the schedule
		while (totalConflict != 0) {
			improve();
			printSchedule();
			count ++;
			// random start every 30 times
			if (count%30 == 0 && totalConflict>0) {
				randomStart();
			}
			countConflict();
		}
	}

	// improve the days
	public void chooseDays() {
		Node[] courseSem = new Node[3];
		int count;
		// find the 3 course with the same semester
		for (int sem = 1; sem <=7; sem++) {
			count = 0;
			for (int course = 0; course < 21; course ++) {
				if (semesters[course] == sem) {
					courseSem[count] = courses[course];
					count++;
				}
			}
			int tryCount = 0;
			// while any of the days is the same, change the day of the course with a higher flexibility (more days offered)
			while ((courseSem[0].getDay().equals(courseSem[1].getDay()) || courseSem[0].getDay().equals(courseSem[2].getDay()) || courseSem[1].getDay().equals(courseSem[2].getDay()))) {
				if (courseSem[0].getDay().equals(courseSem[1].getDay())) {
					if (courseSem[0].days.length > courseSem[1].days.length) {
						courseSem[0].changeDay();
					}
					else {
						courseSem[1].changeDay();
					}
				}
				if (courseSem[0].getDay().equals(courseSem[2].getDay())) {
					if (courseSem[0].days.length > courseSem[2].days.length) {
						courseSem[0].changeDay();
					}
					else {
						courseSem[2].changeDay();
					}
				}
				if (courseSem[1].getDay().equals(courseSem[2].getDay())) {
					if (courseSem[1].days.length > courseSem[2].days.length) {
						courseSem[1].changeDay();
					}
					else {
						courseSem[2].changeDay();
					}
				}
				// stop the loop after 5 times
				if (tryCount == 5) {
					break;
				}
				tryCount++;
			}
		}
	}
}

