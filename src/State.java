// Authors
// Stavropoulos Petros (AM : 3150230)
// Savvidis Konstantinos (AM : 3150229)
// Mpanakos Vasileios (AM : 3140125)

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

// This class represents a State, which contains the school's schedule along with all the required information
// and methods to determine if the schedule is accepted by the school, following the restrictions given.
// Furthermore, this is the element of all the Search algorithms (A*, Beam Search) and given it's score we
// can use the algorithms to find a terminal State, meaning that the schedule of that State will be accepted
// by the school

public class State implements Comparable<State>{
	
	// Seed value for Random if timeSeed is disabled
	private static final int RandSeed = 1;
	
	// Constant to calculate g(n) for A* 
	// Default values : 100 for a* , 0 for beam search
	private static int Gn;  
	
	// List of all lessons
	private static ArrayList<Lesson> lessonList;
	// List of all teachers
	private static ArrayList<Teacher> teacherList;
	// List of all lessons that a teacher can teach
	private static ArrayList<Teacher>[] lessonAvailableTeachersList; // real ids are the index + 1
	
	// The schedule for the gymnasium
	private PairLT[][][] scheduleTable;
	// List of the lessons for every class
	private ArrayList<PairLT>[] classPairsList;
	
	// Score of the State
	private int score;
	// Score only of the conflicts of weak restrictions
	private int scoreWeak;
	// Depth in tree, used for calculating g(n) for A*
	private int depth;
	
	// Default Constructor - Empty Constructor
	public State(){
		// Initialize all values to values that have no meaning for the State
		score = -1;
		scoreWeak = -1;
		depth = -1;
		Gn = -1;
		scheduleTable = null;
		classPairsList = null;	
	}
	
	// Initialization constructor
	@SuppressWarnings("unchecked")
	public State(ArrayList<Lesson> listL, ArrayList<Teacher> listT, boolean timeSeed) {
		
		// Allocate a new 3d array for the schedule
		scheduleTable = new PairLT[9][5][7];
		// Set the lesson list
		lessonList = listL;
		// Set the teachers list
		teacherList = listT;
		// This is the initial State, so it is the root of the tree, so depth is 0
		depth = 0;
		// Initialize Gn to 0
		Gn = 0;
		
		// Allocate the list of each lesson's available teachers
		lessonAvailableTeachersList = (ArrayList<Teacher>[])new ArrayList[lessonList.size()];
		// For every lesson in the lesson list
		for(int i=0;i < lessonList.size();i++){
			// Allocate a new ArrayList for the available teachers
			lessonAvailableTeachersList[i] = new ArrayList<Teacher>();
			// For each teacher in the teachers list
			for(Teacher t : teacherList){
				// For every lesson that the teacher can teach
				for(int lessid : t.getTeacherLessonIds()){
					// If the lesson Id of the lesson the teacher can teach is the same as the current lesson
					// then add the teacher to the lesson's available teachers
					if(lessid == i+1) lessonAvailableTeachersList[i].add(t);
				}
			}
		}
		// Initialize the initial State using randomization
		// if timeSeed is false then Random is using the State variable RandSeed for the seed
		// if timeSeed is true then Random is using the current time for the seed
		initiallize(timeSeed);
		// Evaluate the initial State's score
		evaluate();
	}
	
	// Constructor for each children of the initila State - Copy constructor
	@SuppressWarnings("unchecked")
	public State(PairLT[][][] table,int depth){
		// Set the depth to the depth given
		this.depth = depth;
		// Allocate the 3d array for the schedule for the school
		scheduleTable = new PairLT[9][5][7];
		// Allocate the array for the lists of lessons for each class
		classPairsList = (ArrayList<PairLT>[])new ArrayList[9];
		// Set score to -1 so that we know that it isn't evaluated yet
		score = -1;
		// For every class in the school
		for(int c = 0 ; c < 9 ; c++){
			// Allocate a new list for the current class
			classPairsList[c] = new ArrayList<>();
			// For every day in the week
			for(int d = 0 ; d < 5 ; d++){
				// For every school hour in the day
				for(int h = 0 ; h < 7 ; h++){
					// If there is a lesson in the schedule given
					if(table[c][d][h]!=null){
						// Allocate a new pair using the given pair's lesson and teacher
						PairLT temp = new PairLT(table[c][d][h].getLesson(),table[c][d][h].getTeacher());
						// Set the new pair to the schedule
						scheduleTable[c][d][h] = temp;
						// Add the new pair to the class list
						classPairsList[c].add(temp);
					}else{
						// If there isn't a lesson then put null in the schedule (No lesson)
						scheduleTable[c][d][h] = null;
					}
				}
			}
			// Sort the current class list of lessons using a new comparator
			// which compares the lesson ids
			Collections.sort(classPairsList[c],new Comparator<PairLT>() {
				// Override the compare function in the comparator
				@Override
				public int compare(PairLT arg0, PairLT arg1) {
					// Get each lesson's id
					int a = arg0.getLesson().getLessonId();
					int b = arg1.getLesson().getLessonId();
					// Declare the compare result
					int cmp;
					// Compare the lessons' ids
					if(a>b) cmp = 1;
					else if (a<b) cmp = -1;
					else cmp = 0;
					// Return the compare result
					return cmp;
				}
				
			});
		}
		
	}
	
	// Initialize the initial State using randomization
	@SuppressWarnings("unchecked")
	public void initiallize(boolean timeSeed){

		// Initialize list of pairs for each class
		
		// Allocate the array for the lists of lessons for each class
		classPairsList = (ArrayList<PairLT>[])new ArrayList[9];
		
		// Declare a random object r
		Random r;
		
		// Check timeSeed to configure the seed of the random object
		if(timeSeed) {
			// If true then set the seed to the current time
			r = new Random(System.currentTimeMillis());
		}else {
			// If false then use the variale RandSeed
			r = new Random(RandSeed); 
		}
		
		// For A gymnasium
		
		// For the first 3 classes
		for(int i=0;i<3;i++){
			// Allocate the list of pairs for the current class
			classPairsList[i] = new ArrayList<>();
			// Allocate the list of pairs that are for A gymnasium
			ArrayList<PairLT> entriesA = new ArrayList<>();
			// For every lesson in lessonList
			for(Lesson l : lessonList){
				// If the lesson is for A gymnasium
				if(l.getLessonClass()=='A'){
					// Get the number of teachers that can teach it
					int teach = lessonAvailableTeachersList[l.getLessonId()-1].size();
					// Select a random teacher from them using his index in the lessonAvailableTeachersList
					int tidx = r.nextInt(teach);
					// For all the hours that the lesson must be taught in a week
					for(int j=0;j<l.getHoursPerWeek();j++){
						// Create a pair for the lesson with the random teacher
						PairLT p = new PairLT(l, lessonAvailableTeachersList[l.getLessonId()-1].get(tidx));
						// Add the pair to the list for pairs for A gymnasium
						entriesA.add(p);
						// Add the pair to the list for the current class
						classPairsList[i].add(p);
					}
				}
			}
			// Set the row and column for the schedule table to 0
			int row = 0;
			int col = 0;
			// While there are pairs in the list for the A gymnasium
			while(entriesA.size()>0){
				// If column is five then the first day of the week and move to the next school hour
				if(col==5){
					// Set column to 0
					col = 0;
					// Increment row by 1
					row++;
				}
				// Get the current size of the entriesA list
				int idx = entriesA.size()-1;
				// If it has more than 1 elements then choose randomly one
				// or else take the last element
				int item = (idx>0)?r.nextInt(idx):0;
				// Put the pair in the school schedule and remove it from the entriesA list
				scheduleTable[i][col][row] = entriesA.remove(item);
				// Move to the next column (day)
				col++;
				
			}
			
		}
		// For B gymnasium
		
		// For the next 3 classes
		for(int i=3;i<6;i++){
			// Allocate the list of pairs for the current class
			classPairsList[i] = new ArrayList<>();
			// Allocate the list of pairs that are for B gymnasium
			ArrayList<PairLT> entriesB = new ArrayList<>();
			// For every lesson in lessonList
			for(Lesson l : lessonList){
				// If the lesson is for B gymnasium
				if(l.getLessonClass()=='B'){
					// Get the number of teachers that can teach it
					int teach = lessonAvailableTeachersList[l.getLessonId()-1].size();
					// Select a random teacher from them using his index in the lessonAvailableTeachersList
					int tidx = r.nextInt(teach);
					// For all the hours that the lesson must be taught in a week
					for(int j=0;j<l.getHoursPerWeek();j++){
						// Create a pair for the lesson with the random teacher
						PairLT p = new PairLT(l, lessonAvailableTeachersList[l.getLessonId()-1].get(tidx));
						// Add the pair to the list for pairs for B gymnasium
						entriesB.add(p);
						// Add the pair to the list for the current class
						classPairsList[i].add(p);
					}
				}
			}
			// Set the row and column for the schedule table to 0
			int row = 0;
			int col = 0;
			// While there are pairs in the list for the B gymnasium
			while(entriesB.size()>0){
				// If column is five then the first day of the week and move to the next school hour
				if(col==5){
					// Set column to 0
					col = 0;
					// Increment row by 1
					row++;
				}
				// Get the current size of the entriesB list
				int idx = entriesB.size()-1;
				// If it has more than 1 elements then choose randomly one
				// or else take the last element
				int item = (idx>0)?r.nextInt(idx):0;
				// Put the pair in the school schedule and remove it from the entriesB list
				scheduleTable[i][col][row] = entriesB.remove(item);
				// Move to the next column (day)
				col++;
				
			}
		}
		// For C gymnasium
		
		// For the last 3 classes
		for(int i=6;i<9;i++){
			// Allocate the list of pairs for the current class
			classPairsList[i] = new ArrayList<>();
			// Allocate the list of pairs that are for C gymnasium
			ArrayList<PairLT> entriesC = new ArrayList<>();
			// For every lesson in lessonList
			for(Lesson l : lessonList){
				// If the lesson is for C gymnasium
				if(l.getLessonClass()=='C'){
					// Get the number of teachers that can teach it
					int teach = lessonAvailableTeachersList[l.getLessonId()-1].size();
					// Select a random teacher from them using his index in the lessonAvailableTeachersList
					int tidx = r.nextInt(teach);
					// For all the hours that the lesson must be taught in a week
					for(int j=0;j<l.getHoursPerWeek();j++){
						// Create a pair for the lesson with the random teacher
						PairLT p = new PairLT(l, lessonAvailableTeachersList[l.getLessonId()-1].get(tidx));
						// Add the pair to the list for pairs for C gymnasium
						entriesC.add(p);
						// Add the pair to the list for the current class
						classPairsList[i].add(p);
					}
				}
			}
			// Set the row and column for the schedule table to 0
			int row = 0;
			int col = 0;
			// While there are pairs in the list for the C gymnasium
			while(entriesC.size()>0){
				// If column is five then the first day of the week and move to the next school hour
				if(col==5){
					// Set column to 0
					col = 0;
					// Increment row by 1
					row++;
				}
				// Get the current size of the entriesC list
				int idx = entriesC.size()-1;
				// If it has more than 1 elements then choose randomly one
				// or else take the last element
				int item = (idx>0)?r.nextInt(idx):0;
				// Put the pair in the school schedule and remove it from the entriesC list
				scheduleTable[i][col][row] = entriesC.remove(item);
				// Move to the next column (day)
				col++;
			}
		}
	}
	
	
	public ArrayList<State> getChildren()
	{
		// Find all the children of the current State using method 1, meaning that we exchange each pair with every other pair that is after it,
		// making a child State for every exchange we do, as well as exchange each teacher of every pair with every other available teacher for
		// that lesson, keeping in mind that the same teacher must teach a specific lesson in a class and not multiple teachers
		
		// Allocate a List of State for the children of the current State
		// Set initial capacity to avoid continuous expanding
		ArrayList<State> children = new ArrayList<>(10000); 
		
		// In this part we exchange teachers and get all the possible children via this exchange
		
		// For every class in the school
		for(int cl = 0 ; cl < 9 ; cl++){
			// Get the number of the pairs of the specific class in the week
			int sz = classPairsList[cl].size();
			// For every pair of the specific class in the week
			for(int i=0;i<sz;i++){
				// Get a reference to the pair
				PairLT currP = classPairsList[cl].get(i);
				// Get a reference to the pair's lesson
				Lesson currL = currP.getLesson();
				// For every teacher that can teach this lesson
				for(Teacher t : lessonAvailableTeachersList[currL.getLessonId()-1]){
					// If the teacher is not the current teacher
					if(t.getTeacherId()!=currP.getTeacher().getTeacherId()){
						// Create a child State using the copy constructor with depth a level below than the current State
						State child = new State(scheduleTable,this.depth+1);
						// Set an integer variable j to the pair that the loop is (i)
						// in order for the child to check the next pairs in it's classPairsList
						// and not to change the position of the pair we are in the parent State
						int j = i;
						// While there are next with the same lesson as the current, change the teacher accordingly to 't'
						// This works because of the way classPairsList is designed, as it is sorted by lesson Id
						while(child.classPairsList[cl].get(j).getLesson().getLessonId()==currL.getLessonId()){
							// Set pair's teacher to 't' in the child State
							child.classPairsList[cl].get(j).setTeacher(t);
							// Go to the next pair
							j++;
						}
						// Evaluate the score of the child State
						child.evaluate();
						// Add the child State to the children of the current State
						children.add(child);
					}
				}
				// Check if we are in the last pair
				if(i+1<sz){
					// While the next pair has the same lesson as the current move to the next pair
					// This works because of the way classPairsList is designed, as it is sorted by lesson Id
					// Here we skip all the pairs of the lesson that we created children to go the next lesson
					// and repeat the process
					while(classPairsList[cl].get(i+1).getLesson().getLessonId()==currL.getLessonId()){
						// Move to the next pair
						i++;
						// If we are in the end of the list break the loop
						if(i==sz) break;
					}
				}
			}
		}
		
		// In this part we exchange each pair in the schedule with all the other pairs in the same class ( method 1 )
		
		// For each class in the school
		for(int c=0;c<9;c++){
			// For each day in the week
			for(int d=0;d<5;d++){
				// For each school hour in the day
				for(int h=0;h<7;h++){
					// Set temph to the next hour from the current
					int temph = h+1;
					// For each day from the current to the last day in the week
					for(int ds = d;ds<5;ds++){
						// For each hour from temph (the next from the current) to the last in the day
						for(int hs=temph;hs<7;hs++){
							// If the pair is the same then skip to the next hour
							if(scheduleTable[c][d][h] == scheduleTable[c][ds][hs]) continue;
							// Else create a child State using the clone constructor, with depth one level below
							// the current State's depth
							State child = new State(scheduleTable,depth+1);
							// Get a reference to the first pair in the child's schedule
							PairLT tempP = child.scheduleTable[c][d][h];
							// Set the first pair with the second pair
							child.scheduleTable[c][d][h] = child.scheduleTable[c][ds][hs];
							// Set the second pair to the first pair using the reference
							// and completing the pair swap
							child.scheduleTable[c][ds][hs] = tempP;
							// Evaluate the score of the child State
							child.evaluate();
							// Add the child State to the children of the current State
							children.add(child);
						}
						// Set temph to 0, so that the next day will start from the first school hour
						temph = 0;
					}
				}
			}
		}
		// Return the list with all the children
		return children;
	}
	
	public ArrayList<State> getChildrenSBF()
	{
		// SBF : Small Branching Factor
		
		// Find all the children of the current State using method 2, meaning that we exchange only the first pair with every other pair that
		// is after it, making a child State for every exchange we do, as well as exchange each teacher of every pair with every other available 
		// teacher for that lesson, keeping in mind that the same teacher must teach a specific lesson in a class and not multiple teachers
		
		// Method 2 creates less children for each State, so it has a smaller branching factor than method 1 
		
		// Allocate a List of State for the children of the current State
		// Set initial capacity to avoid continuous expanding		
		ArrayList<State> children = new ArrayList<>(1000);
		
		// In this part we exchange teachers and get all the possible children via this exchange
		
		// For every class in the school
		for(int cl = 0 ; cl < 9 ; cl++){
			// Get the number of the pairs of the specific class in the week
			int sz = classPairsList[cl].size();
			// For every pair of the specific class in the week
			for(int i=0;i<sz;i++){
				// Get a reference to the pair
				PairLT currP = classPairsList[cl].get(i);
				// Get a reference to the pair's lesson
				Lesson currL = currP.getLesson();
				// For every teacher that can teach this lesson
				for(Teacher t : lessonAvailableTeachersList[currL.getLessonId()-1]){
					// If the teacher is not the current teacher
					if(t.getTeacherId()!=currP.getTeacher().getTeacherId()){
						// Create a child State using the copy constructor with depth a level below than the current State
						State child = new State(scheduleTable,this.depth+1);
						// Set an integer variable j to the pair that the loop is (i)
						// in order for the child to check the next pairs in it's classPairsList
						// and not to change the position of the pair we are in the parent State
						int j = i;
						// While there are next with the same lesson as the current, change the teacher accordingly to 't'
						// This works because of the way classPairsList is designed, as it is sorted by lesson Id
						while(child.classPairsList[cl].get(j).getLesson().getLessonId()==currL.getLessonId()){
							// Set pair's teacher to 't' in the child State
							child.classPairsList[cl].get(j).setTeacher(t);
							// Go to the next pair
							j++;
						}
						// Evaluate the score of the child State
						child.evaluate();
						// Add the child State to the children of the current State
						children.add(child);
					}
				}
				// Check if we are in the last pair
				if(i+1<sz){
					// While the next pair has the same lesson as the current move to the next pair
					// This works because of the way classPairsList is designed, as it is sorted by lesson Id
					// Here we skip all the pairs of the lesson that we created children to go the next lesson
					// and repeat the process
					while(classPairsList[cl].get(i+1).getLesson().getLessonId()==currL.getLessonId()){
						// Move to the next pair
						i++;
						// If we are in the end of the list break the loop
						if(i==sz) break;
					}
				}
			}
		}
		
		// In this part we exchange each pair in the schedule with all the other pairs in the same class ( method 1 )
		
		// For each class in the school
		for(int c=0;c<9;c++) {
			// For each day in the week
			for(int d=0;d<5;d++) {
				// For each school hour in the day
				for(int h=0;h<7;h++) {
					// If the pair is the same then skip to the next hour
					if(scheduleTable[c][0][0] == scheduleTable[c][d][h]) continue;
					// Else create a child State using the clone constructor, with depth one level below
					// the current State's depth
					State child = new State(scheduleTable,depth+1);
					// Get a reference to the first pair in the child's schedule
					PairLT tempP = child.scheduleTable[c][d][h];
					// Set the first pair with the second pair
					child.scheduleTable[c][d][h] = child.scheduleTable[c][0][0];
					// Set the second pair to the first pair using the reference
					// and completing the pair swap
					child.scheduleTable[c][0][0] = tempP;
					// Evaluate the score of the child State
					child.evaluate();
					// Add the child State to the children of the current State
					children.add(child);
				}
			}
		}
		// Return the list with all the children
		return children;
	}
	
	
	public boolean isTerminal()
	{
		// Checks if the current State is a terminal State
		
		// Check that the score for only the strong restrictions is 0, meaning that are no strong restrictions
		// If it is 0 then it is a terminal State
		if(score - depth*Gn - scoreWeak == 0) return true;
		// If it is not 0 then it isn't a terminal State
		return false;
	}
	
	
	private void evaluate()
	{	
		// Evaluates the score of the current State
		
		// Create an integer array to put the conflicts from restrictions 2,3 and 8
		int[] restrictions238 = new int[3];
		
		// Fill the array with the corresponding conflicts
		restriction2_3_8(restrictions238);
		
		// Create and fill integer variables for each restriction from 2,3 and 8
		int res2 = restrictions238[0];
		int res3 = restrictions238[1];
		int res8 = restrictions238[2];
		
		// Calculate the score for the State using the conflicts from each strong restriction (1,2,3,4 and 5)
		// The conflicts are multiplied by a weight (1000 in this case)
		
//		score = 1600*restriction1() + 2500*res2 + 2200*res3 + 1200*restriction4() + 1000*restriction5();
		score = 1000*restriction1() + 1000*res2 + 1000*res3 + 1000*restriction4() + 1000*restriction5();
		
		// Calculate the weak score using the conflicts from the weak restrictions (6,7 and 8)
		// The weight for the weak restrictions is 1, so that even in an extreme case the sum of the
		// conflicts of the weak restrictions will not be greater than 1 strong restriction (<1000)
		scoreWeak = restriction6() + restriction7() + res8;
		
		// Add the weak score to the score of the State
		score += scoreWeak;
		
		// Add the depth multiplied by the constant value Gn, which represents g(n) if the used algorithm is A*
		score += depth * Gn;
		
	}
	
	
	private int restriction1()
	{
		// Checks if there is no same teacher in any class on a specific hour
		
		// Initialize conflicts found to 0
		int conflicts = 0;
		
		// For every day in the week
		for(int d = 0;d < 5;d++) {
			// For every school hour in the day
			for(int h = 0;h < 7 ; h++) {
				// Initialize a boolean array with default values false
				// We use this array so that when a conflict from a teacher has been found we don't calculate
				// multiple times the conflict for the same teacher
				boolean[] cFound = new boolean[9];
				// For every class in the school
				for(int c = 0;c < 9;c++) {
					// If a conflict has already been found for the specific teacher or there isn't a lesson continue
					if(scheduleTable[c][d][h]==null || cFound[c]) continue;
					// Set currID to the current teacher's Id that we check
					int currTID = scheduleTable[c][d][h].getTeacher().getTeacherId();
					// For every class after the current class
					for(int c2 = c+1;c2 < 9;c2++) {
						// If there isn't a lesson continue
						if(scheduleTable[c2][d][h]==null) continue;
						// Chech if the teacher's Id is the same in the other class teacher
						if(currTID == scheduleTable[c2][d][h].getTeacher().getTeacherId()) {
							// If the teacher Id is in the class then the teacher teaches in these classes at the same time
							// so increase the conflicts by 1
							conflicts++;
							// We have found a conflict for the teacher
							cFound[c2] = true;
						}
					}
				}
			}
		}
		// Return all the conflicts that have been found 
		return conflicts;
	}
	
	private void restriction2_3_8(int[] restrictions) {
		
		// Strong restrictions 2,3 : Checks if there is a teacher that exceeds his maxhours per day/week
		// Weak restriction 8 -- Checks if the hours each teacher teaches in a week is uniformly distributed
		
		// For efficiency all these restrictions are calculated in this function
		
		// Initialize each conflict variable to 0
		int conflict2 = 0;
		int conflict3 = 0;
		int conflict8 = 0;
		
		// Allocate a 2d array that holds the hours each teacher teaches for every day in the week
		// and one last column with the sum of those, meaning the weekly hours for each teacher
		int DaysTeachersTable[][] = new int[6][teacherList.size()];
		
		// For every class in the school
		for(int c = 0;c < 9;c++) {
			// For every day in the week
			for(int d=0;d<5;d++) {
				// For every school hour in the day
				for(int h=0;h<7;h++) {
					// If there is a pair
					if(scheduleTable[c][d][h]!=null) {
						// Increase the pair's teacher total hours at that day by 1
						DaysTeachersTable[d][scheduleTable[c][d][h].getTeacher().getTeacherId()-1]++;
					}
				}
			}
		}
		
		// Set integer variable which is used to find the sum of all the teachers' hours per week to 0
		int sumTeachersHours = 0;
		
		// For all the teachers
		for(int t=0;t<teacherList.size();t++) {
			// Initialize sum to 0
			int sum = 0;
			// For every day in the week
			for(int d=0;d<5;d++) {
				// Get the hours from the 2d array for the specific teacher on the day that we are
				int hours = DaysTeachersTable[d][t];
				// Add the hours to the sum
				sum+= hours;
				// If the hours are greater than the max hours per day of the teacher
				// then increase the conflict variable for the restriction2
				if(hours>teacherList.get(t).getMaxHoursPerDay()) conflict2++;
			}
			// Store the sum in the last column of the 2d array, as the weekly hours
			// of the teacher
			DaysTeachersTable[5][t] = sum;
			// Increase the sum of all the teachers' weekly hours by the sum
			sumTeachersHours += sum;
			// If the weekly hours of the teacher are greater than his max hours
			// per week then increase the conflict variable for the restriction3
			if(sum > teacherList.get(t).getMaxHoursPerWeek()) conflict3++;
		}
		
		// Find the hours that the teachers should teach per week in order to be uniformly distributed
		// used for the restriction8
		// Because the mean may be a real number, we use two integers, one for the floor and one for
		// the ceiling. For example:
		// 3.4 would give: N1 = 3 , N2 = 4
		int N1 = (int)Math.floor(sumTeachersHours / (double)teacherList.size());
		int N2 = (int)Math.ceil(sumTeachersHours / (double)teacherList.size());
		
		// For every teacher
		for(int t=0;t<teacherList.size();t++) {
			// Get his weekly hours from the 2d array
			int weekHours = DaysTeachersTable[5][t];
			// If the weekly hours are less than the lower bound (N1)
			if(weekHours<N1) {
				// Get the teacher's max hours per week
				int maxHours = teacherList.get(t).getMaxHoursPerWeek();
				// If a teacher is at his max hours then don't calculate it as a violation
				if(weekHours != maxHours) {
					// If the lower bound is less than the teacher's maxHours/week then the increase the conflict for
					// the restriction8 only until it reaches his max hours and not until the lower bound
					if(maxHours<N1) {
						conflict8 += maxHours - weekHours;
					}else {
						// Else increase it using the difference from the lower bound
						conflict8 += N1 - weekHours;
					}
				}
			}
			// Else if the weekly hours are greater than the upper bound
			else if(weekHours>N2) {
				// Increase the conflict for the restriction8 using the difference from the upper bound
				conflict8 += weekHours - N2;
			}
		}
		// Assign each conflict variable to it's corresponding restriction in the array
		// passed as a parameter
		restrictions[0] = conflict2;
		restrictions[1] = conflict3;
		restrictions[2] = conflict8;
	
	}
	
	private int restriction4() {
		
		// Checks if there are teachers that teach for over 2 hours continuously
		
		// Initialize the conflicts variable to 0
		int conflicts = 0;
		
		// Allocate a 2d array that holds each teachers continuous teaching hours for every day of the week
		int continuousHoursTable[][] = new int[5][teacherList.size()];
		
		// The algorithm checks for conflicts only downwards, as if there was was a teacher that teaches another lesson
		// before the specific hour then we will have already checked it
		
		// For every day in the week
		for(int d=0;d<5;d++) {
			// For every school hour in the day
			for(int h=0;h<7;h++) {
				// For every class in the school
				for(int c=0;c<9;c++) {
					// If there isn't a pair then continue to the next class
					if(scheduleTable[c][d][h] == null) continue;
					// Get the teacher id of the current pair
					int tID = scheduleTable[c][d][h].getTeacher().getTeacherId()-1;
					// Set continuous hours for the teacher to 1, as he is teaching at least this lesson 
					continuousHoursTable[d][tID] = 1;
					// For every hour in the day after the current hour
					for(int h2=h+1;h2<7;h2++) {
						// Initialize a boolean variable that indicates if we have found another lesson that
						// the teacher teaches to false
						boolean found = false;
						// For every class in the school
						for(int c2=0;c2<9;c2++) {
							// If there is a pair and the teacher that teaches the lesson is the same as the current
							if(scheduleTable[c2][d][h2]!=null && tID == scheduleTable[c2][d][h2].getTeacher().getTeacherId()-1) {
								// Increase the continuous hours for the teacher by 1
								continuousHoursTable[d][tID]++;
								// Indicate that we have found another lesson that the teacher teaches
								found = true;
								// Break the loop
								break;
							}
						}
						// Check if we found that there is another lesson that the teacher teaches
						if(!found) {
							// If we didn't found one then break the loop to go to the next class 
							break;
						}else if(continuousHoursTable[d][tID]>2) {
							// If we have found and the continuous hours are more than 2 then
							// increase the conflicts variable by 1
							conflicts++;
							//break the loop to go to the next class
							break;
						}
					}
				}
			}
		}
		
		// Return the conflicts variable for the restriction4
		return conflicts;
	}
	
	private int restriction5() {
		
		// Checks if there null pairs (no lesson) between lessons
		
		// Initialize the conflicts variable to 0
		int conflicts = 0;
		
		// For every class in the school
		for(int c = 0;c < 9;c++) {
			// For every day in the week
			for(int d=0;d<5;d++) {
				// For every hour in the day
				for(int h=0;h<7;h++) {
					// If there isn't a pair (there is no lesson)
					if(scheduleTable[c][d][h]==null) {
						// If the null-pair is not at the end and the next pair is
						// not a null pair, then there is a null-pair between lessons
						// so increase the conflicts variable by 1
						if(h+1<7 && scheduleTable[c][d][h+1]!=null) conflicts++;
					}
				}
			}
		}
		
		// Return the conflicts variable for the restriction5
		return conflicts;
	}
	
	private int restriction6() {
		
		// Weak Restriction : Checks if the hours of each day are uniformly distributed in the week for each class
		
		// Initialize the conflicts variable to 0
		int conflicts = 0;
		
		// For every class in the school
		for(int c = 0 ; c < 9 ; c++) {
			
			// Get the number of all the hours in the class
			int hours = classPairsList[c].size();
			// To be uniformly distributed there must be approximately N1 or N2 hours ( N1==N2 if hours are a multiple of 5)
			int N1 = (int) Math.floor(hours/5.0);
			int N2 = (int) Math.ceil(hours/5.0);
			
			// Check how much the hours differ from N1,N2
			// For every day in the week
			for(int d = 0;d < 5 ; d++) {
				// Set the sum of the hours for the day to 0
				int sum = 0;
				// For every school hour in the day
				for(int h=0;h<7;h++) {
					// If there is a pair then there is a lesson,
					// so increase hours' sum by 1
					if(scheduleTable[c][d][h]!=null) sum++;
				}
				// If the hours' sum is less than the lower bound then
				// increase the conflicts variable by the difference of
				// the hours and N1
				if(sum < N1) conflicts += N1-sum;
				// If the hours' sum is more than the upper bound then
				// increase the conflicts variale by the diffrerence of
				// the hours and N2
				else if(sum > N2) conflicts += sum-N2;
				
			}
			
			
			
			
		}
		
		// Return the conflicts variable for restriction5
		return conflicts;
	}
	
	private int restriction7() {

		// Weak Restriction : Check if each lesson's hours are uniformly distributed in the week for each class
		
		// Initialize conflicts variable to 0
		int conflicts = 0;
		
		// Allocate a 2d array that has the hours of each lesson for every day in the week
		// The array has every lesson from A,B,C Gymnasium
		int[][] lessonsDaysTable = new int[lessonList.size()][5]; 
		
		// Iterate through c=0,3,6 meaning classes A1,B1,C1 to fill the whole lessonDaysTable one time
		for (int c = 0; c <= 6; c += 3) { 

			// Fill the array
			
			// For every day in the week
			for (int d = 0; d < 5; d++) {
				// For every hour in the day
				for (int h = 0; h < 7; h++) {
					// If there is a pair
					if (scheduleTable[c][d][h] != null) {
						// Get the lesson id of the pair
						int lid = scheduleTable[c][d][h].getLesson().getLessonId();
						// Increase that lesson's hours by one at the current day
						lessonsDaysTable[lid - 1][d]++;
					}
				}
			}
		}
		// Check for uniformity
		
		// For every lesson
		for (int i = 0; i < lessonList.size(); i++) {
			
			// Get the reference to the lesson using the lesson id
			Lesson l = lessonList.get(i);
			// Get the lesson's hours per week
			int hours = l.getHoursPerWeek();
			// Calculate lower and upper bounds N1 and N2 which are used
			// to find how many hours per day for uniformity
			int N1 = (int) Math.floor(hours / 5.0);
			int N2 = (int) Math.ceil(hours / 5.0);

			// Check how much the lesson's hours differ from N1,N2
			// For every day in the week
			for (int d = 0; d < 5; d++) {
				// Get the sum of the lesson's hours from the array on
				// the current day
				int sum = lessonsDaysTable[i][d];
				// If the sum if less than N1 then add to the conflicts
				// variable the difference of sum and N1
				if (sum < N1)
					conflicts += N1 - sum;
				else if (sum > N2)
					// If the sum if greater than N2 then add to the conflicts
					// variable the difference of sum and N2
					conflicts += sum - N2;

			}

		}
		
		// Allocate a 2d array that has the hours of each lesson for every day in the week
		// The array has every lesson from A,B,C Gymnasium
		lessonsDaysTable = new int[lessonList.size()][5];

		// Iterate through c=1,4,7 meaning classes A2,B2,C2 to fill the whole lessonDaysTable one time
		for (int c = 1; c <= 7; c += 3) { 

			// Fill the array
			
			// For every day in the week
			for (int d = 0; d < 5; d++) {
				// For every hour in the day
				for (int h = 0; h < 7; h++) {
					// If there is a pair
					if (scheduleTable[c][d][h] != null) {
						// Get the lesson id of the pair
						int lid = scheduleTable[c][d][h].getLesson().getLessonId();
						// Increase that lesson's hours by one at the current day
						lessonsDaysTable[lid - 1][d]++;
					}
				}
			}
		}
		// Check for uniformity

		// For every lesson
		for (int i = 0; i < lessonList.size(); i++) {

			// Get the reference to the lesson using the lesson id
			Lesson l = lessonList.get(i);
			// Get the lesson's hours per week
			int hours = l.getHoursPerWeek();
			// Calculate lower and upper bounds N1 and N2 which are used
			// to find how many hours per day for uniformity
			int N1 = (int) Math.floor(hours / 5.0);
			int N2 = (int) Math.ceil(hours / 5.0);

			// Check how much the lesson's hours differ from N1,N2
			// For every day in the week
			for (int d = 0; d < 5; d++) {
				// Get the sum of the lesson's hours from the array on
				// the current day
				int sum = lessonsDaysTable[i][d];
				// If the sum if less than N1 then add to the conflicts
				// variable the difference of sum and N1
				if (sum < N1)
					conflicts += N1 - sum;
				else if (sum > N2)
					// If the sum if greater than N2 then add to the conflicts
					// variable the difference of sum and N2
					conflicts += sum - N2;

			}

		}

		// Allocate a 2d array that has the hours of each lesson for every day in the week
		// The array has every lesson from A,B,C Gymnasium
		lessonsDaysTable = new int[lessonList.size()][5];

		// Iterate through c=2,5,8 meaning classes A3,B3,C3 to fill the whole lessonDaysTable one time
		for (int c = 2; c <= 8; c += 3) { 

			// Fill the array
			
			// For every day in the week
			for (int d = 0; d < 5; d++) {
				// For every hour in the day
				for (int h = 0; h < 7; h++) {
					// If there is a pair
					if (scheduleTable[c][d][h] != null) {
						// Get the lesson id of the pair
						int lid = scheduleTable[c][d][h].getLesson().getLessonId();
						// Increase that lesson's hours by one at the current day
						lessonsDaysTable[lid - 1][d]++;
					}
				}
			}
		}
		// Check for uniformity

		// For every lesson
		for (int i = 0; i < lessonList.size(); i++) {

			// Get the reference to the lesson using the lesson id
			Lesson l = lessonList.get(i);
			// Get the lesson's hours per week
			int hours = l.getHoursPerWeek();
			// Calculate lower and upper bounds N1 and N2 which are used
			// to find how many hours per day for uniformity
			int N1 = (int) Math.floor(hours / 5.0);
			int N2 = (int) Math.ceil(hours / 5.0);

			// Check how much the lesson's hours differ from N1,N2
			// For every day in the week
			for (int d = 0; d < 5; d++) {
				// Get the sum of the lesson's hours from the array on
				// the current day
				int sum = lessonsDaysTable[i][d];
				// If the sum if less than N1 then add to the conflicts
				// variable the difference of sum and N1
				if (sum < N1)
					conflicts += N1 - sum;
				else if (sum > N2)
					// If the sum if greater than N2 then add to the conflicts
					// variable the difference of sum and N2
					conflicts += sum - N2;

			}

		}

		// Return the conflicts variable for restriction7
		return conflicts;
	}
	
	// Getter for State's score
	public int getScore()
	{
		return this.score;
	}
	
	// Setter for State's score
	public void setScore(int score)
	{
		this.score = score;
	}
	
	// Getter for State's depth
	public int getDepth() {
		return depth;
	}

	// Setter for State's depth
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void print()
	{
		// Print the schedule to the console
		
		char classes[] = {'A','B','C'};
		
		// For every class in the school
		for(int c=0;c<9;c++){
			
			// Print class to console
			System.out.println(classes[c/3]+""+(c%3+1));
			
			// For every school hour in the day
			for(int h=0;h<7;h++){
				// For every day in the week
				for(int d=0;d<5;d++){
					// If there is a pair
					if(scheduleTable[c][d][h]!=null){
						// Print the pair's lesson name and teacher name
						System.out.print(scheduleTable[c][d][h].getLesson().getLessonName() +"-"+ scheduleTable[c][d][h].getTeacher().getTeacherName() + " | ");
					}else{
						// Print that there is no lesson
						System.out.print("KENO | ");
					}
				}
				// Print a new line to go to the next hour
				System.out.println();
			}
			
			// Print two new lines for the next class 
			
			System.out.println();
			System.out.println();
			
		}
	}
	
	public void writeToFile(String filename){
		// This method writes the schedule to a csv file
		
		// Initialize the FileWriter to null
		FileWriter writer = null;
		try {
			// Set the FileWriter to the file using the filename from the parameter
			writer = new FileWriter(filename);
			
			// Allocate a static array that holds all the classes' name
			String[] classes = {"A1","A2","A3","B1","B2","B3","C1","C2","C3"};
			
			// Write the separator used which is a comma (used by excel)
			writer.write("sep=,\n");
			
			// For every class in the school
			for(int i=0;i<9;i++){
				
				// Write the class name
				writer.write(classes[i]);
				
				// Write the labels for all the days of the week
				writer.write("\nDeutera,Trith,Tetarth,Pempth,Paraskeyh\n");
				
				// For every school hour in the day
				for(int n=0;n<7;n++){
					// For every day
					for(int m=0;m<5;m++){
						// If there is a pair
						if(scheduleTable[i][m][n]!=null){
							// Write the lesson's and teacher's name with a comma in the end (csv)
							writer.write(scheduleTable[i][m][n].getLesson().getLessonName() + "| " + scheduleTable[i][m][n].getTeacher().getTeacherName() + ",");
						}else{
							// If there isn't a pair
							// Write that there is no lesson at that hour with a comma in the end (csv)
							writer.write("KENO,");
						}
					}
					// Write a new line for the next day
					writer.write("\n");
				}
				
				// Write two new lines for the next class
				writer.write("\n");
				writer.write("\n");
				
			}
			
			
		} catch (IOException e) {
			// If there was an IOException print the Stack Trace as an error message
			e.printStackTrace();
		} finally{
			try {
				// Close the writer
				writer.close();
			} catch (IOException e) {
				// If there was an IOException print the Stack Trace as an error message
				e.printStackTrace();
			}
		}
		
	}
	
	// Getter for State's scheduleTable
	public PairLT[][][] getScheduleTable() {
		return scheduleTable;
	}

	// Getter for State's classPairsList
	public ArrayList<PairLT>[] getClassPairsList() {
		return classPairsList;
	}

	// Getter for State's score from the weak restrictions
	public int getScoreWeak() {
		return scoreWeak;
	}

	// Getter for State class Gn variable
	public static int getGn() {
		return Gn;
	}

	// Setter for State class Gn variable
	public static void setGn(int gn) {
		Gn = gn;
	}

	

	@Override
	// Calculate the hashCode of the State to use at the closedSet
	public int hashCode() {
		// Set a prime number (31)
		final int prime = 31;
		// Initialize the result to 1
		int result = 1;
		// Calculate the result using the prime number and the hashCode from the scheduleTable
		result = prime * result + Arrays.deepHashCode(scheduleTable);
		// Return the hashCode
		return result;
	}

	@Override
	// Determine if two States are the same
	public boolean equals(Object obj) {
		// If the other object has the same reference
		if (this == obj)
			// Then it is the same
			return true;
		// If the other object is null 
		if (obj == null)
			// Then it can't be the same
			return false;
		// If the other object is from an other class than the State class
		if (getClass() != obj.getClass())
			// Then it can't be the same
			return false;
		// We know that the object is the same class, so we explicitly cast it to a State
		State other = (State) obj;
		// Check every element of the scheduleTable in the two States
		if (!Arrays.deepEquals(scheduleTable, other.scheduleTable))
			// If they are not equal return false
			return false;
		// If they are equal return true
		return true;
	}

	@Override
	// Compare a State to another State using the State's score
	// and return the comparison result
	public int compareTo(State s) {
		return Double.compare(this.score, s.score);
	}
}
