import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) {

		// data fields for time counting
		int currentTime = 0;
		int endTime = 60 * 60 * 8; // 5pm
		int serviceTime;

		// fields for employee
		int numServed = 0;
		int longestBreakLength = 0;
		int idleTime = 0;

		// create two queues
		Queue<Customer> customers = new Queue<>(); // customers who have not come
		Queue<Customer> queue = new Queue<>(); // customers who have come
												// the first position of queue is the one being served

		// file I/O
		try {
			// read customersFile.txt
			String customerFileName = args[0];

			// create the file object
			File customerFile = new File(customerFileName);

			// read the file
			Scanner input1 = new Scanner(customerFile);

			// read the first line (service time)
			String info = input1.nextLine();
			serviceTime = Integer.parseInt(info);

			// read the rest of the lines
			while (input1.hasNextLine()) {

				// skip blank lines
				if (input1.nextLine().contentEquals(" ")) {
					continue;
				}

				// get customer id
				info = input1.nextLine();
				String id = "";
				for (int i = 0; i < info.length(); i++) {
					if (info.charAt(i) >= 48 && info.charAt(i) <= 57) {
						id += info.charAt(i);
					}
				}

				// get customer arrive time and convert it to seconds
				info = input1.nextLine();
				String arriveTime1 = info.split(":", 2)[1];
				// get "clean" arrive time
				String arriveTime2 = "";
				for (int i = 0; i < arriveTime1.length(); i++) {
					if ((arriveTime1.charAt(i) >= 48 && arriveTime1.charAt(i) <= 57) || arriveTime1.charAt(i) == 58) {
						arriveTime2 += arriveTime1.charAt(i);
					}
				}

				String[] arriveTimeArr = arriveTime2.split(":");
				int hour = Integer.parseInt(arriveTimeArr[0]);
				int min = Integer.parseInt(arriveTimeArr[1]);
				int sec = Integer.parseInt(arriveTimeArr[2]);

				int arriveTimeSec = Customer.convertToSeconds(hour, min, sec);

				// create a customer object and store it into arrayList
				Customer c = new Customer(id, arriveTimeSec);
				Node<Customer> n = new Node<>();
				n.data = c;
				customers.enqueue(n);

			}

			// create an array to store customer id and waiting time
			String[][] idAndWaiting = new String[customers.currentSize][2];

			// push customers coming before 9am into the queue
			while (customers.head.data.arriveTime <= 0) {
				Node<Customer> n = new Node<>();
				n.data = customers.head.data;
				queue.enqueue(n);
				customers.dequeue();
			}

			// service begins at 9am
			int ptr = 0;
			
			while (!customers.isEmpty() || !queue.isEmpty()) { // when there is any customer left

				if (queue.isEmpty()) { // all customers <= currentTime have been served

					// break the loop if all the remaining customers come after 5pm
					if (customers.head.data.arriveTime >= endTime) {
						idleTime += endTime - currentTime;
						if (longestBreakLength < endTime - currentTime) {
							longestBreakLength = endTime - currentTime;
						}
						break;
					}

					Node<Customer> n = new Node<>();
					n.data = customers.head.data;
					queue.enqueue(n);
					customers.dequeue();
					idleTime += queue.head.data.arriveTime - currentTime;
					if (longestBreakLength < queue.head.data.arriveTime - currentTime) {
						longestBreakLength = queue.head.data.arriveTime - currentTime;
					}
					currentTime = queue.head.data.arriveTime;
				}

				// waiting time
				int wait = currentTime - queue.head.data.arriveTime;
				idAndWaiting[ptr][0] = queue.head.data.id;
				idAndWaiting[ptr][1] = Integer.toString(wait);
				ptr++;

				// serving
				currentTime += serviceTime;
				numServed++;

				// current time cannot exceed endTime
				if (currentTime >= endTime) {
					currentTime = endTime;
				}

				while (!customers.isEmpty() && customers.head.data.arriveTime <= currentTime) {
					Node<Customer> n = new Node<>();
					n.data = customers.head.data;
					queue.enqueue(n);
					customers.dequeue();
				}

				queue.dequeue();

				// break the loop if currentTime already reaches endTime
				if (currentTime == endTime) {
					break;
				}

			}

			// for customers come before 5pm but do not receive the service
			while (!queue.isEmpty()) {
				idAndWaiting[ptr][0] = queue.head.data.id;
				idAndWaiting[ptr][1] = Integer.toString(endTime - queue.head.data.arriveTime);
				queue.dequeue();
				ptr++;
			}

			// for customers come after 5pm: set the waiting time to 0
			while (!customers.isEmpty()) {
				idAndWaiting[ptr][0] = customers.head.data.id;
				idAndWaiting[ptr][1] = "0";
				customers.dequeue();
				ptr++;
			}

			// read queriesfile.txt and write into output.txt at the same time
			String queryFileName = args[1];
			String outputFileName = "output.txt";

			// create the file object
			File queryFile = new File(queryFileName);
			File outputFile = new File(outputFileName);

			Scanner input2 = new Scanner(queryFile);
			PrintWriter output = new PrintWriter(outputFile);

			while (input2.hasNextLine()) {
				String queryContent = input2.nextLine();

				switch (queryContent) {

				case "NUMBER-OF-CUSTOMERS-SERVED":
					output.println("NUMBER-OF-CUSTOMERS-SERVED: " + numServed);
					System.out.println("NUMBER-OF-CUSTOMERS-SERVED: " + numServed);
					break;

				case "LONGEST-BREAK-LENGTH":
					output.println("LONGEST-BREAK-LENGTH: " + longestBreakLength);
					System.out.println("LONGEST-BREAK-LENGTH: " + longestBreakLength);
					break;

				case "TOTAL-IDLE-TIME":
					output.println("TOTAL-IDLE-TIME: " + idleTime);
					System.out.println("TOTAL-IDLE-TIME: " + idleTime);
					break;

				case "MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME":
					int maxInQueue = queue.maxInQueue - 1;
					output.println("MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME: " + maxInQueue);
					System.out.println("MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME: " + maxInQueue);
					break;

				default:
					// loop through this line to find "clean" id
					String idToFound = "";
					for (int i = 0; i < queryContent.length(); i++) {
						if (queryContent.charAt(i) >= 48 && queryContent.charAt(i) <= 57) {
							idToFound += queryContent.charAt(i);
						}
					}

					// loop through idAndWaiting
					for (String[] a : idAndWaiting) {
						if (a[0].contentEquals(idToFound)) {
							output.println(queryContent + ": " + a[1]);
							System.out.println(queryContent + ": " + a[1]);
							break;
						}
					}
				}

			}

			input1.close();
			input2.close();
			output.close();

		}

		catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Error: args[0]/args[1] not found");
			ex.printStackTrace();
		}

		catch (IOException ex) {
			System.out.println("Error: Unable to open/read/write to file");
			ex.printStackTrace();
		}

	}
}