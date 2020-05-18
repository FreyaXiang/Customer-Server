public class Customer {
	String id;
	int arriveTime;

	public Customer(String id, int arriveTime) {
		this.id = id;
		this.arriveTime = arriveTime;
	}

	// method for converting time with standard format (hh:mm:ss) to seconds
	// assume 9am is 0
	// time before 9am < 0, time after 9am > 0  
	// assume customers come at normal time: from 7am to 7pm
	public static int convertToSeconds(int hour, int min, int sec) {
		if (hour < 1 || hour > 12) {
			throw new IllegalArgumentException("Hour must be in the range of 1 to 12");
		} else if (hour >= 1 && hour < 7) {
			return (hour + 12 - 9) * 3600 + min * 60 + sec;
		} else if (hour >= 7 && hour < 9) {
			return (-1) * ((9 - hour - 1) * 3600 + (60 - min - 1) * 60 + (60 - sec));
		} else {
			return (hour - 9) * 3600 + min * 60 + sec;
		}
	}

}