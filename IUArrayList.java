import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Array-based implementation of IndexedUnsortedList.
 * An Iterator with working remove() method is implemented, but
 * ListIterator is unsupported. 
 * 
 * @author 
 *
 * @param <T> type to store
 */
public class IUArrayList<T> implements IndexedUnsortedList<T> {
	private static final int DEFAULT_CAPACITY = 10;
	private static final int NOT_FOUND = -1;
	
	private T[] array;
	private int rear;
	private int modCount;
	
	/** Creates an empty list with default initial capacity */
	public IUArrayList() {
		this(DEFAULT_CAPACITY);
	}
	
	/** 
	 * Creates an empty list with the given initial capacity
	 * @param initialCapacity
	 */
	@SuppressWarnings("unchecked")
	public IUArrayList(int initialCapacity) {
		array = (T[])(new Object[initialCapacity]);
		rear = 0;
		modCount = 0;
	}
	
	/** Double the capacity of array */
	private void expandCapacity() {
		if(rear >= array.length) {
			array = Arrays.copyOf(array, array.length*2);
		}
	}

	@Override
	public void addToFront(T element) {
		expandCapacity();
		for(int i = 0; i < rear; i++) {
			array[i] = array[i+1];
		}
		array[0] = element;
		rear++;
		modCount++;
	}

	@Override
	public void addToRear(T element) {
		expandCapacity();
		array[rear] = element;
		rear++;
		modCount++;
	}

	@Override
	public void add(T element) {
		addToRear(element);
	}

	@Override
	public void addAfter(T element, T target) {
		int targetIndex = indexOf(target);
		
		if(targetIndex == -1) {
			throw new NoSuchElementException();
		}
		expandCapacity();
		
		for(int i = rear; i > targetIndex + 1; i--) {
			array[i] = array[i-1];
		}
		array[targetIndex + 1] = element;
		rear++;
		modCount++;
	}

	@Override
	public void add(int index, T element) {
		if(index > rear) {
			throw new IndexOutOfBoundsException();
		}
		expandCapacity();
		for(int i = rear; i > index + 1; i--) {
			array[i] = array[i-1];
		}
		array[index] = element;
		rear++;
		modCount++;
	}

	@Override
	public T removeFirst() {
		if(array[0] == null) {
			throw new NoSuchElementException();
		}
		T retVal = array[0];
		rear--;
		for(int i = 0; i < rear; i++) {
			array[i] = array[i + 1];
		}
		array[rear] = null;
		modCount++;
		
		return retVal;
	}

	@Override
	public T removeLast() {
		if(array[0] == null) {
			throw new NoSuchElementException();
		}
		
		return remove(rear - 1);
	}

	@Override
	public T remove(T element) {
		int index = indexOf(element);
		if (index == NOT_FOUND) {
			throw new NoSuchElementException();
		}
		
		T retVal = array[index];
		
		rear--;
		//shift elements
		for (int i = index; i < rear; i++) {
			array[i] = array[i+1];
		}
		array[rear] = null;
		modCount++;
		
		return retVal;
	}

	@Override
	public T remove(int index) {
		// TODO 
		if(index == -1 || index > rear - 1) {
			throw new IndexOutOfBoundsException();
		}
		T retVal = array[index];
		rear--;
		for(int i = index; i < rear; i++) {
			array[i] = array[i+1];
		}
		array[rear] = null;
		modCount++;
		return retVal;
	}

	@Override
	public void set(int index, T element) {
		if(index == -1 || index >= rear || array[0] == null) {
			throw new IndexOutOfBoundsException();
		}
		array[index] = element;
		modCount++;
	}

	@Override
	public T get(int index) {
		if(index < 0 || index >= rear) {
			throw new IndexOutOfBoundsException();
		}
		return array[index];
	}

	@Override
	public int indexOf(T element) {
		int index = NOT_FOUND;
		
		if (!isEmpty()) {
			int i = 0;
			while (index == NOT_FOUND && i < rear) {
				if (element.equals(array[i])) {
					index = i;
				} else {
					i++;
				}
			}
		}
		
		return index;
	}

	@Override
	public T first() {
		if(array[0] == null) {
			throw new NoSuchElementException();
		}
		return array[0];
	}

	@Override
	public T last() {
		if(rear == 0) {
			throw new NoSuchElementException();
		}
		return array[rear - 1];
	}

	@Override
	public boolean contains(T target) {
		return (indexOf(target) != NOT_FOUND);
	}

	@Override
	public boolean isEmpty() {
		return rear == 0;
	}

	@Override
	public int size() {
		return rear;
	}
	
	public String toString() {
		String arrayString = "[";
		for(int i = 0; i < rear; i++) {
			if(i == (rear - 1)) {
				arrayString += array[i];
			} else {
				arrayString += array[i] + ",";
			}
		}
		arrayString += "]";
		return arrayString;
	}

	@Override
	public Iterator<T> iterator() {
		return new ALIterator();
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int startingIndex) {
		throw new UnsupportedOperationException();
	}

	/** Iterator for IUArrayList */
	public class ALIterator implements Iterator<T> {
		private int next;
		private int iterModCount;
		private boolean canRemove;

		public ALIterator() {
			next = 0;
			iterModCount = modCount;
			canRemove = false;
		}
		
		@Override
		public boolean hasNext() {
			return (next < rear);
		}
		
		@Override
		public T next() {
			modCheck();
			if(!hasNext()) {
				throw new NoSuchElementException();
			} else {
				T object = array[next];
				next++;
				canRemove = true;
				return object;
			}
		}
		
		public void remove() {
			if(next != 0 && array[next - 1] != null) {
				array[next - 1] = null;
			} else {
				throw new IllegalStateException();
			}
		}
		
		public void modCheck() {
			if(modCount != iterModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}
}
