package at.ac.tuwien.lod.taskmanagement;

import java.util.concurrent.Callable;

/**
 * Instances of this interface represents a task that has a return value with the type specified by
 * the given type argument &lt;T&gt;.
 *
 * @author Kevin Haller
 */
public interface ReturnValueTask<T> extends Callable<T>, AutoCloseable {

}
