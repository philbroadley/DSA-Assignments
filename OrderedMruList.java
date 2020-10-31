package dsa_assignment1;

public class OrderedMruList<E extends Comparable<E>> implements OrderedMruListInterface<E>
{
	/**
	 * The head field is an <code>MLNode</code> object, where the
	 * <code>next1</code> and <code>prev1</code> pointers are for the circular Ordered list,
	 * and the <code>next2</code> and <code>prev2</code> are for the circular MRU list.
	 * It always contains the value <code>Null</code>.
	 * These lists are considered empty if there is no
	 * <b>other</b> <code>MLNode</code> object on the lists other
	 * than the <code>head</code> node itself
	 * 
	 */
	MLNodeInterface<E>	head	= new MLNode<E>(null);

	public OrderedMruList()
	{
	}
	
	public boolean isEmptyOrdered()
	{
		if (head.getNext1() == head) {
			return true;
		}else {
		return false;
		}
	}

	public boolean isEmptyMru()
	{
		if (head.getNext2() == head) {
			return true;
		}else {
		return false;
		}
	}

	public OrderedMruListInterface<E> touch(MLNodeInterface<E> target)
	{
		target.addAfter2(head);
		return this;
	}
	
	public MLNodeInterface<E> getFirstMru()
	{
		if (head.getNext2() != head) {
			return head.getNext2();
		}
		else {
			return null;
		}
	}
	
	public MLNodeInterface<E> getFirstOrdered()
	{
		if (head.getNext1() != head) {
			return head.getNext1();
		}
		else {
			return null;
		}
	}
	
	public MLNodeInterface<E> getNextOrdered(MLNodeInterface<E> current)
	{
		if (current.getNext1() != head) {
			return current.getNext1();
		}
		else {
			return null;
		}
	}

	public MLNodeInterface<E> getNextMru(MLNodeInterface<E> current)
	{

		if (current.getNext2() != head) {
			return current.getNext2();
		}
		else {
			return null;
		}
	}

	public OrderedMruListInterface<E> remove(MLNodeInterface<E> target)
	{
		target.remove1();
		target.remove2();
		return this;
	}
	
	public OrderedMruListInterface<E> add(E element)
	{
		MLNodeInterface<E> newItem = new MLNode<>(element);
		newItem.addAfter2(head);
		MLNodeInterface<E> pointer = head.getNext1();
		if(pointer.getElement() == null) {
			newItem.addBefore1(pointer);
			}
		while(newItem.getNext1()==newItem) {
			if (pointer.getElement().compareTo(element)>0) {
				newItem.addBefore1(pointer);	
			}
			pointer = pointer.getNext1();
		}
		return this;
	}
}
