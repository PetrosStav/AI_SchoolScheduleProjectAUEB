// Authors
// Stavropoulos Petros (AM : 3150230)
// Savvidis Konstantinos (AM : 3150229)
// Mpanakos Vasileios (AM : 3140125)

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		
		// Allocate a list for the lessons
		ArrayList<Lesson> lessons = new ArrayList<>();
		// Allocate a list for the teachers
		ArrayList<Teacher> teachers = new ArrayList<>();
		// Initialize a buffered reader for the lessons to null
		BufferedReader readerL = null;
		// Initialize a buffered reader for the teachers to null
		BufferedReader readerT = null;
		try {
			// Open lessons.txt file with the buffered reader
			readerL = new BufferedReader(new FileReader("lessons.txt"));
			// Open (teachersStrict|teachersModerate|teachersLoose).txt file with the buffered reader
			// teachersStrict.txt   --> teachers have little hours per day and per week, according to the lessons that
			//							they can teach, thus making it more difficult to find a correct schedule
			// teachersModerate.txt --> teachers have normal hours per day and per week, according to the lessons that
			//							they can teach, thus making finding a correct schedule of moderate difficulty
			// teachersLoose.txt	--> teachers have many hours per day and per week, according to the lessons that
			//							they can teach, thus making it more easy to find find a correct schedule
			readerT = new BufferedReader(new FileReader("teachersLoose.txt"));
			
			// Read the Lessons
			
			// Declare a String reference
			String line;
			// While the reader hasn't reached the end of the file
			// and it reads the label [LESSON]
			while((line = readerL.readLine())!=null && line.equals("[LESSON]")){
				// Read the first line after the label,parse the integer and set it to the lessonId
				int lessonId = Integer.parseInt(readerL.readLine());
				// Read the next line and set it to the lessonName
				String lessonName = readerL.readLine();
				// Read the next line and set the first character of the line to the lessonClass
				char lessonClass = readerL.readLine().charAt(0);
				// Read the next line,parse the integer and set it to the huorsPerWeek 
				int hoursPerWeek = Integer.parseInt(readerL.readLine());
				// Add the lesson to the lessons list
				lessons.add(new Lesson(lessonId, lessonName, lessonClass, hoursPerWeek));
			}
			
			// Read the Teachers
			
			// While the reader hasn't reached the end of the file
			// and it reads the label [TEACHER]
			while((line = readerT.readLine())!=null && line.equals("[TEACHER]")){
				// Read the first line after the label,parse the integer and set it to the teacherId
				int teacherId = Integer.parseInt(readerT.readLine());
				// Read the next line and set it to the lessonName
				String teacherName = readerT.readLine();
				// Read the next line, split it using ',' as a separator and get the String array
				String[] lessonIds = readerT.readLine().split(",");
				// Allocate an integer array of the same length as the String array above
				int[] teacherLessonIds = new int[lessonIds.length];
				// For every element of the String array
				for(int i=0;i<teacherLessonIds.length;i++){
					// Parse the element as an integer and set it to the
					// according index in the teacherLessonIds array
					teacherLessonIds[i] = Integer.parseInt(lessonIds[i]);
				}
				// Read the next line, parse the integer and set it to the maxHoursPerDay
				int maxHoursPerDay = Integer.parseInt(readerT.readLine());
				// Read the next line, parse the integer and set it to the maxHoursPerWeek
				int maxHoursPerWeek = Integer.parseInt(readerT.readLine());
				// Add the teacher to the teachers list
				teachers.add(new Teacher(teacherId, teacherName, teacherLessonIds, maxHoursPerDay, maxHoursPerWeek));
			}
		// Catch all the exceptions and print the stack traces accordingly
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}finally{
			
			try {
				// If the readerL is not null
				if(readerL!=null) {
					// Close the reader for the lessons file
					readerL.close();
				}
				// If the readerT is not null
				if(readerT!=null) {
					// Close the reader for the teachers file
					readerT.close();
				}
			// Catch any IO Exception and print the stack trace
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			
		}
		
		// Create the initial State using the lesson and teacher list that we filled above
		// choosing true or false for the timeSeed
		// timeSeed --> true  : use time as a seed for a pseudo random run
		// timeSeed --> false : use a specific seed (RandSeed) for a more controlled run (for debugging purposes)
		State initialState = new State(lessons,teachers,true);		
		
		// Write the initialState to a csv file in order to see from what condition of the schedule
		// we started and compare it to the finalState's condition
		initialState.writeToFile("schedule_init.csv");
				
		// Initialize the finalState to null
		State finalState = null;
		
		// Allocate the SearchAlgorithm object that will be used to perform the according search algorithm to the initialState
		SearchAlgorithm algo = new SearchAlgorithm();
		
		// Set the integer value that will determine which search algorithm will be used 
		// Choices : 1 - A*
		//			 2 - Modified A*
		//			 3 - Beam Search
		int choice = 3; // change this
		
		// If we have the first choice - A*
		if(choice == 1) {
			// Find the finalState using the A* algorithm with closed set
			// The second parameter controls the method that the State generates it's children
			// false --> method 1 - Every pair swapped with every other pair
			// true  --> method 2 (SBF) - First pair swapped with every other pair
			finalState = algo.AstarClosedSet(initialState,true);
		}
		// If we have the second choice - Modified A*
		else if(choice == 2) {
			// Find the finalState using the Modified A* algorithm with closed set
			// The second parameter controls the method that the State generates it's children
			// false --> method 1 - Every pair swapped with every other pair
			// true  --> method 2 (SBF) - First pair swapped with every other pair
			// The third parameter controls the Upper Bound, after which the algorithm will
			// keep a fixed population of States in it's frontier
			finalState = algo.AstarModifiedClosedSet(initialState,false,50);
		}
		// If we have the third choice - Beam Search
		else {
			// Find the finalState using the Beam Search algorithm with closed set
			// The second parameter controls the number of States that Beam Search will
			// keep in it's frontier (the top n States)
			// The third parameter controls the method that the State generates it's children
			// false --> method 1 - Every pair swapped with every other pair
			// true  --> method 2 (SBF) - First pair swapped with every other pair
			// Because Beam Search depends much on the randomness of the initialState
			// and with a small frontier it finishes fast enough, we give it some
			// tries to restart and try again with a new randomized initialState
			int tries = 5;
			while(finalState==null && tries > 0) {
			finalState = algo.BeamSearch(initialState, 5, false );
			if(finalState==null) {
				System.out.println("BeamSearch didn't find a solution.\nRestarting...");
				tries--;
				}
			}
		}
		
		// Check if a finalState has been found
		if(finalState!=null) {
			// If it has been found then write it to a csv file
			finalState.writeToFile("schedule.csv");
			// Print a success message to console
			System.out.println("Schedule has been written to file.");
		}else {
			// Print a failure message to console
			System.out.println("The algorithm didn't find a shedule.");
		}
		
		
	}
}
