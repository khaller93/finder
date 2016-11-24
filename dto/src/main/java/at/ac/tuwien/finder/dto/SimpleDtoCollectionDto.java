package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a simple implementation of {@link CollectionDto} containing simple resources.
 *
 * @author Kevin Haller
 */
public class SimpleDtoCollectionDto<T extends Dto> extends AbstractDto
    implements CollectionDto<T>, Collection<T> {

    private Collection<T> dtoCollection = new ArrayList<T>();

    @Override
    public List<Resource> asResourceList() {
        return dtoCollection.stream().map(dto -> Resource.createResource(dto.id()))
            .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return dtoCollection.size();
    }

    @Override
    public boolean isEmpty() {
        return dtoCollection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return dtoCollection.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return dtoCollection.iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return dtoCollection.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return dtoCollection.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return dtoCollection.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return dtoCollection.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return dtoCollection.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return dtoCollection.retainAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return dtoCollection.retainAll(c);
    }

    @Override
    public void clear() {
        dtoCollection.clear();
    }
}
