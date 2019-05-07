// Authors
// Stavropoulos Petros (AM : 3150230)
// Savvidis Konstantinos (AM : 3150229)
// Mpanakos Vasileios (AM : 3140125)

// This class represents a pair of a lesson and a teacher that teaches that lesson
// and is the element of each hour in the schedule of the school

public class PairLT {
	
	// lesson is the Pair's lesson that is being taught
	private Lesson lesson;
	// teacher is the Pair's teacher that is teaching the above lesson
	private Teacher teacher;
	
	// Constructor using parameters
	public PairLT(Lesson lesson, Teacher teacher) {
		this.lesson = lesson;
		this.teacher = teacher;
	}

	// Getter for lesson
	public Lesson getLesson() {
		return lesson;
	}

	// Setter for lesson
	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}

	// Getter for teacher
	public Teacher getTeacher() {
		return teacher;
	}

	// Setter for teacher
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
	

	@Override
	// Calculate the PairLT's hashcode
	public int hashCode() {
		// Set a prime number (31)
		final int prime = 31;
		// Initialize the result to 1
		int result = 1;
		// Calculate the result using the previous result, the prime and the lesson's hashcode
		result = prime * result + ((lesson == null) ? 0 : lesson.hashCode());
		// Calculate the result using the previous result,the prime and the teacher's hashcode
		result = prime * result + ((teacher == null) ? 0 : teacher.hashCode());
		// Return the result
		return result;
	}

	@Override
	// Determine if two PairLT objects are the same
	public boolean equals(Object obj) {
		// If the other object has the same reference
		if (this == obj)
			// Then it is the same
			return true;
		// If the other object is null 
		if (obj == null)
			// Then it can't be the same
			return false;
		// If the other object is from an other class than the PairLT class
		if (getClass() != obj.getClass())
			// Then it can't be the same
			return false;
		// We know that the object is the same class, so we explicitly cast it to a PairLT
		PairLT other = (PairLT) obj;
		// Check if only one of the two PairLTs has a null lesson
		// if it does then they can't be the same
		if (lesson == null) {
			if (other.lesson != null)
				return false;
		}else if (!lesson.equals(other.lesson))
			// Check if the first PairLT's lesson is different from the other's lesson
			// and return false if it does
			return false;
		// Check if only one of the two PairLTs has a null teacher
		// if it does then they can't be the same
		if (teacher == null) {
			if (other.teacher != null)
				return false;
		} else if (!teacher.equals(other.teacher))
			// Check if the first PairLT's teacher is different from the other's teacher
			// and return false if it does
			return false;
		// If all the checks are passed then it is the same
		// so return true
		return true;
	}

	@Override
	// Override the toString method so that we can print the PairLT object to the console
		// for debugging purposes
	public String toString() {
		return "PairLT [lesson=" + lesson + ", teacher=" + teacher + "]";
	}
	
	
	
}
