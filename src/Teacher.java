// Authors
// Stavropoulos Petros (AM : 3150230)
// Savvidis Konstantinos (AM : 3150229)
// Mpanakos Vasileios (AM : 3140125)

import java.util.Arrays;

//This class represents a middle school teacher, which is the basis to construct pairs of lessons-teachers
//to use in the schedule of the school

public class Teacher {
	
	// teacherId is the unique identifier of a teacher
	private int teacherId;
	// teacherName is the name of the teacher
	private String teacherName;
	// teacherLessonIds is an array with the unique identifiers of the lessons each teacher can teach
	private int[] teacherLessonIds;
	// maxHoursPerDay are the maximum number of hours each teacher is capable of teaching in a day
	private int maxHoursPerDay;
	// maxHoursPerWeek are the maximum number of hours each teacher is capable of teaching in a week
	private int maxHoursPerWeek;
	
	// Constructor of Teacher using parameters
	public Teacher(int teacherId, String teacherName, int[] teacherLessonIds, int maxHoursPerDay, int maxHoursPerWeek) {
		this.teacherId = teacherId;
		this.teacherName = teacherName;
		this.teacherLessonIds = teacherLessonIds;
		this.maxHoursPerDay = maxHoursPerDay;
		this.maxHoursPerWeek = maxHoursPerWeek;
	}

	// Getter for teacherId
	public int getTeacherId() {
		return teacherId;
	}

	// Setter for teacherId
	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	// Getter for teacherName
	public String getTeacherName() {
		return teacherName;
	}

	// Setter for teacherName
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	// Getter for teacherLessonIds
	public int[] getTeacherLessonIds() {
		return teacherLessonIds;
	}

	// Setter for teacherLessonIds
	public void setTeacherLessonIds(int[] teacherLessonIds) {
		this.teacherLessonIds = teacherLessonIds;
	}

	// Getter for maxHoursPerDay
	public int getMaxHoursPerDay() {
		return maxHoursPerDay;
	}

	// Setter for maxHoursPerDay
	public void setMaxHoursPerDay(int maxHoursPerDay) {
		this.maxHoursPerDay = maxHoursPerDay;
	}

	// Getter for maxHoursPerWeek
	public int getMaxHoursPerWeek() {
		return maxHoursPerWeek;
	}

	// Setter for maxHoursPerWeek
	public void setMaxHoursPerWeek(int maxHoursPerWeek) {
		this.maxHoursPerWeek = maxHoursPerWeek;
	}
	
	@Override
	// Calculate the Teacher's hashcode
	public int hashCode() {
		// Set a prime number (31)
		final int prime = 31;
		// Initialize the result to 1
		int result = 1;
		// Calculate the result using the prime, the previous result and the teacherId
		result = prime * result + teacherId;
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
		// If the other object is from an other class than the Teacher class
		if (getClass() != obj.getClass())
			// Then it can't be the same
			return false;
		// We know that the object is the same class, so we explicitly cast it to a Teacher
		Teacher other = (Teacher) obj;
		// Check the teacherId of the two Teachers
		if (teacherId != other.teacherId)
			// The teacherIds aren't the same
			return false;
		// The teacherIds are the same
		return true;
	}

	@Override
	// Override the toString method so that we can print the Teacher object to the console
	// for debugging purposes
	public String toString() {
		return "Teacher [teacherId=" + teacherId + ", teacherName=" + teacherName + ", teacherLessonIds="
				+ Arrays.toString(teacherLessonIds) + ", maxHoursPerDay=" + maxHoursPerDay + ", maxHoursPerWeek="
				+ maxHoursPerWeek + "]";
	}
	
	
}
