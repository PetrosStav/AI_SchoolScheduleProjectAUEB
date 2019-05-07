// Authors
// Stavropoulos Petros (AM : 3150230)
// Savvidis Konstantinos (AM : 3150229)
// Mpanakos Vasileios (AM : 3140125)

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

// This class contains the various algorithms that will be used in order to find a schedule that
// obeys the restrictions given, transforming an initialState to a finalState

public class SearchAlgorithm {
	
	// List of all the states in the frontier of the algorithm
	private ArrayList<State> states;
	// HashSet of all the States that we have already visited in order to use it as a closed set
	// and avoid checking the same States twice
	private HashSet<State> closedSet;
	
	// Default Constructor
	public SearchAlgorithm() {
		// Initialize states to null
		states = null;
		// Initialize closedSet to null
		closedSet = null;
	}
	
	// A* algorithm with closed set
	//
	// First parameter is the initialState that the algorithm with transform to the finalState
	// Second parameter is a boolean variable which indicates the choice between two methods of
	// the State generating children :
	// false --> method 1 - Every pair swapped with every other pair
	// true  --> method 2 (SBF) - First pair swapped with every other pair
	// Returns the finalState which obeys to all the restrictions
	public State AstarClosedSet(State initialState, boolean SBF){ // working!!! --- TODO PUT THE CLOSED SET WHILE GETTING THE CHILDREN SO THAT THEY ARENT IN THE STATES LIST 
		// Set Gn to 100 because it is needed for A* to work
		State.setGn(100);
		// Allocate a list of States with a big initial capacity in order to avoid resizes
		states = new ArrayList<State>(5000000);
		// Allocate a new hashSet object to the closedSet
		closedSet = new HashSet<State>();
		// Add the initialState to the frontier states list
		states.add(initialState);
		
		// Set integer i to 0  - keeps index of which (incremented number) State we are currently checking
		int i=0;
		// While the list of states has States
		while(states.size() > 0){
			// Remove the first State in the list, which has the best (smallest) score
			State currentState = states.remove(0);
			// Print to console info about the current State
			System.out.println("State "+(i++) +" testing:\nScore: " + currentState.getScore()+ "\nScoreWeak: " + currentState.getScoreWeak() + "\nDepth: " + currentState.getDepth()+ "\n\n");
			// Check if the current State is terminal and if it is return it
			if(currentState.isTerminal()) return currentState;
			// Check if the hashSet (closed set) contains the current State
			if(!closedSet.contains(currentState)){
				// If it doesn't then add it to the HashSet
				closedSet.add(currentState);
				// Choose according to the parameter which getchildren method will be used
				// and add all the generated children to the frontier states list
				if(SBF) {
					states.addAll(currentState.getChildrenSBF());
				}else {
					states.addAll(currentState.getChildren());
				}
				// Sort the states list
				Collections.sort(states);
			}
		}
		// If the algorithm has reached this point then it hasn't been able to find a finalState
		// so return null
		return null;
	}
	
	// Modified A* with closed Set
	// Modified A* uses a mix of A*, Hill Climb algorithm to avoid getting stuck in a group of same conditions
	// as well as an Upper Bound, after which the population of States in the frontier becomes fixed, so that
	// no more RAM will be used by the algorithm and it can continue working as a last resort with what is has
	//
	// First parameter is the initialState that the algorithm with transform to the finalState
	// Second parameter is a boolean variable which indicates the choice between two methods of
	// the State generating children :
	// false --> method 1 - Every pair swapped with every other pair
	// true  --> method 2 (SBF) - First pair swapped with every other pair
	// Third parameter is the UpperBound after which the algorithm maintains a fixed frontier size
	// Returns the finalState which obeys to all the restrictions
	public State AstarModifiedClosedSet(State initialState,boolean SBF,int UpperBound){
		// Set Gn to 100 because it is needed for A* to work
		State.setGn(100);
		// Allocate a list of States with a big initial capacity in order to avoid resizes
		states = new ArrayList<State>(5000000);
		// Allocate a new hashSet object to the closedSet
		closedSet = new HashSet<State>();
		// Add the initialState to the frontier states list
		states.add(initialState);
		
		// Set integer i to 0  - keeps index of which (incremented number) State we are currently checking
		// as well as checks if we passed the UpperBound
		int i=0;
		// Initialize integer variable prevScore to -1
		// prevScore defines the score of the previous State that had an absolute difference from that until the current State <=2
		int prevScore = -1;
		// Initialize integer variable currScore to -1
		// currScore defines the score of the currentState that the algorithm is checking
		int currScore = -1;
		// Initialize integer variable times to 0
		// times indicates how many times the prevScore had an absolute difference from the currScore <=2
		int times = 0;
		// While the list of states has States 
		while(states.size() > 0){
			// Declare currentState as a State reference
			State currentState;
			// In this section the algorithm does the HC mix
			//
			// it checks if a certain score has been appearing frequently and if it does then instead
			// of taking the first element from the States list, it starts taking leaps according to
			// how many times the score is nearly the same as the previous, with the final leap getting
			// the middle State in the list
			//
			// check the times variable and remove accordingly:
			if(times < 2) {
				// the first element the first 2 times of a similar score
				currentState = states.remove(0);
			}else if(times < 5){
				// the (times-1)*100th element the 2nd,3rd,4th time of a similar score
				currentState = states.remove(100*(times-1));
			}else if(times < 8){
				// the (times-3)*1000th element the 5th,6th,7th time of a similar score
				currentState = states.remove(1000*(times-3));
			}else {
				// the middle element of the list the 8th time of a similar score
				currentState = states.remove(states.size()/2);
				// reset the times to 0
				times = 0;
			}
			// Get the score of the current State and set it to currScore
			currScore = currentState.getScore();
			// If the absolute difference of the prevScore and the currScore is <= 2 then increase the times variables
			if(Math.abs(currScore - prevScore) <= 2) times++;
			else {
				// If not then set the currScore to the prevScore
				prevScore = currScore;
				// Set the times to 0
				times = 0;
			}
			// Print to console info about the current State
			System.out.println("State "+(i++) +" testing:\nScore: " + currentState.getScore()+ "\nScoreWeak: " + currentState.getScoreWeak() + "\nDepth: " + currentState.getDepth()+ "\n\n");
			// Check if the current State is terminal and if it is return it
			if(currentState.isTerminal()) return currentState;
			// Check if the hashSet (closed set) contains the current State
			if(!closedSet.contains(currentState)){
				// If it doesn't then add it to the HashSet
				closedSet.add(currentState);
				// Get the size of the frontier States list
				int sz = states.size();
				// Choose according to the parameter which getchildren method will be used
				// and add all the generated children to the frontier states list
				if(SBF) {
					states.addAll(currentState.getChildrenSBF());
				}else {
					states.addAll(currentState.getChildren());
				}
				// Sort the states list
				Collections.sort(states);
				// In this section the algorithm does the fixed frontier list size trick
				// Check if the number of the current State is over the UpperBound from the parameter 
				if(i>UpperBound) {
					// If it is then take the sublist of the frontier states list with the
					// size of the frontier list prior to the addition of the generated children
					// keeping this way a fixed sized for the frontier states list
					states = new ArrayList<State>(states.subList(0, sz));
				}
			}
		}
		// If the algorithm has reached this point then it hasn't been able to find a finalState
		// so return null
		return null;
	}
	
	// Beam Search with Closed Set
	//
	// First parameter is the initialState that the algorithm with transform to the finalState
	// Second parameter is the number of States that Beam Search will keep in it's frontier (the top n States)
	// after each state in the frontier has generated it's children and the list has been sorted
	// Third parameter is a boolean variable which indicates the choice between two methods of
	// the State generating children :
	// false --> method 1 - Every pair swapped with every other pair
	// true  --> method 2 (SBF) - First pair swapped with every other pair
	public State BeamSearch(State initialState,int n, boolean SBF) {
		// Set Gn to 0 because we don't want the States to have extra score
		// from the depth in the tree as in A*
		State.setGn(0);
		// Allocate a list of States with an initial capacity depending on
		// which method for generating children is used in order to avoid resizes
		if(SBF) {
			states = new ArrayList<State>(n*1000);
		}else {
			states = new ArrayList<State>(n*10000);
		}
		// Allocate a new hashSet object to the closedSet
		closedSet = new HashSet<State>();
		
		// Now the algorithm does the first step, so that beam search has enough
		// States to choose for the frontier according to n
		
		// Add the initialState to the frontier states list
		states.add(initialState);
		// Get all the children for the initialState and add them
		// to the frontier states list
		states.addAll(initialState.getChildren());
		// // Sort the states list
		Collections.sort(states);
		
		// Set integer i to 0  - keeps index of which (incremented number) State we are currently checking
		int i=0;
		// While the list of states has States
		while(states.size() > 0){
			// Take the sublist of size n of the frontier states list and set it as
			// the new frontier states list
			states = new ArrayList<>(states.subList(0, n));
			// For every State in the frontier state list
			for(int k=0;k<n;k++) {
				// Remove the first element of the list and set it as the currentState
				State currentState = states.remove(0);
				// Print to console info about the current State
				System.out.println("State "+(i++) +" testing:\nScore: " + currentState.getScore()+ "\nScoreWeak: " + currentState.getScoreWeak() + "\nDepth: " + currentState.getDepth()+ "\n\n");
				// Check if the current State is terminal and if it is return it
				if(currentState.isTerminal()) return currentState;
				// Check if the hashSet (closed set) contains the current State
				if(!closedSet.contains(currentState)){
					// If it doesn't then add it to the HashSet
					closedSet.add(currentState);
					// Choose according to the parameter which getchildren method will be used
					// and add all the generated children to the frontier states list
					if(SBF) {
						states.addAll(currentState.getChildrenSBF());
					}else {
						states.addAll(currentState.getChildren());
					}
				}
			}
			// // Sort the states list
			Collections.sort(states);
			
		}
		// If the algorithm has reached this point then it hasn't been able to find a finalState
		// so return null
		return null;
	}
	
}
