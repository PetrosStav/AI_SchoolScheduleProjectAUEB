// Authors
// Stavropoulos Petros (AM : 3150230)
// Savvidis Konstantinos (AM : 3150229)
// Mpanakos Vasileios (AM : 3140125)

// This class represents a middle school lesson, which is the basis to construct pairs of lessons-teachers
// to use in the schedule of the school

public class Lesson {
	// lessonId is the unique identifier of a lesson
	private int lessonId;
	// lessonName is the name/title of a lesson
	private String lessonName;
	// lessonClass is the class(A,B,C) in which each lesson is taught
	private char lessonClass;
	// hoursPerWeek are the amount of hours each lesson is taught in each class department(A1,A2,A3,B1,B2,B3,C1,C2,C3)
	private int hoursPerWeek;
	
	// Constructor for lesson using parameters
	public Lesson(int lessonId,String lessonName, char lessonClass,int hoursPerWeek) {
		this.lessonId = lessonId;
		this.lessonName = lessonName;
		this.lessonClass = lessonClass;
		this.hoursPerWeek = hoursPerWeek;
	}
	
	// Getter for the lessonId
	public int getLessonId() {
		return lessonId;
	}
	
	// Setter for the lessonId
	public void setLessonId(int lessonId) {
		this.lessonId = lessonId;
	}
	
	// Getter for the lessonName
	public String getLessonName() {
		return lessonName;
	}
	
	// Setter for the lessonName
	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}
	
	// Getter for the lessonClass
	public char getLessonClass() {
		return lessonClass;
	}
	
	// Setter for the lessonClass
	public void setLessonClass(char lessonClass) {
		this.lessonClass = lessonClass;
	}
	
	// Getter for the hoursPerWeek
	public int getHoursPerWeek() {
		return hoursPerWeek;
	}
	
	// Setter for the hoursPerWeek
	public void setHoursPerWeek(int hoursPerWeek) {
		this.hoursPerWeek = hoursPerWeek;
	}

	@Override
	// Calculate the Lesson's hashcode
	public int hashCode() {
		// Set a prime number (31)
		final int prime = 31;
		// Initialize the result to 1
		int result = 1;
		// Calculate the result using the prime, the previous result and the lessonId
		result = prime * result + lessonId;
		// Return the result
		return result;
	}

	@Override
	// Determine if two Teacher objects are the same
	public boolean equals(Object obj) {
		// If the other object has the same reference
		if (this == obj)
			// Then it is the same
			return true;
		// If the other object is null 
		if (obj == null)
			// Then it can't be the same
			return false;
		// If the other object is from an other class than the Lesson class
		if (getClass() != obj.getClass())
			// Then it can't be the same
			return false;
		// We know that the object is the same class, so we explicitly cast it to a Lesson
		Lesson other = (Lesson) obj;
		// Check the lessonId of the two Lessons
		if (lessonId != other.lessonId)
			// The lessonIds aren't the same
			return false;
		// The lessonIds are the same
		return true;
	}

	@Override
	// Override the toString method so that we can print the Lesson object to the console
		// for debugging purposes
	public String toString() {
		return "Lesson [lessonId=" + lessonId + ", lessonName=" + lessonName + ", lessonClass=" + lessonClass
				+ ", hoursPerWeek=" + hoursPerWeek + "]";
	}
	
	
}
