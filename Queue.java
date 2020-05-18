import java.util.*;

public class Queue<T> {
	Node<T> head;
	Node<T> tail;
	int maxInQueue;
	int currentSize;

	public Queue() {
		this.head = null;
		this.tail = null;
		this.maxInQueue = 0;
		this.currentSize = 0;
	}

	public boolean isEmpty() {
		return head == null;
	}

	public void enqueue(Node<T> node) {
		// addLast
		if (head == null) {
			head = node;
			tail = head;
		} else {
			tail.next = node;
			tail=node;
		}

		currentSize++;
		if (currentSize > maxInQueue) {
			maxInQueue = currentSize;
		}
	}

	public void dequeue() {
		if (head == null)
			throw new NoSuchElementException();
		// removeFirst
		if (head == tail) {
			tail = tail.next;
		}
		head = head.next;
		currentSize--;
	}

}