package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.service.exception.RDFSerializableException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.model.Model;

/**
 * Instances of this interface represents services that carry out particular actions, when
 * {@code execute()} is called.
 *
 * @author Kevin Haller
 */
@FunctionalInterface
public interface IService {

    /**
     * Executes this {@link IService} and returns the result of the service in form of a
     * {@link Model}.
     *
     * @return {@link Model} that contains the result of the service.
     * @throws ServiceException if the execution of this {@link IService} fails.
     */
    Model execute() throws RDFSerializableException;
}
