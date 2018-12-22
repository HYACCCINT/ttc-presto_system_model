package backend.travelcard;

import java.io.Serializable;
import java.util.Stack;

/**
 * A stack with a fixed size.
 *
 * @param <E> The type of elements
 */
public class FixedStack<E> extends Stack<E> implements Serializable {

  /** The maximum fixed size of this stack. */
  private int maxSize;

  /**
   * Creates a new stack of specified size.
   *
   * @param size The specified maximum size.
   */
  public FixedStack(int size) {
    super();
    this.maxSize = size;
  }

  @Override
  public E push(E object) {
    // If the stack is too big, remove elements until it's the right size.
    while (this.size() >= maxSize) {
      this.remove(0);
    }
    return super.push(object);
  }

  /** Removes the first item on this stack. */
  public void popLast() {
    if (size() > 0) {
      this.remove(size() - 1);
    }
  }

  /**
   * Returns the last item of the stack.
   * @return The last item.
   */
  public E getLast() {
    if (size() > 0) {
      return get(size() - 1);
    }
    return null;
  }
}
