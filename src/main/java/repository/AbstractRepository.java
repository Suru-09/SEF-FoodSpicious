package repository;

import domain.BaseEntity;
import domain.exception.CustomException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRepository<ID, T extends BaseEntity<ID>> implements Repository<T, ID>{
    protected Map<ID,T> elems;

    public AbstractRepository() {
        elems = new HashMap<>();
    }

    /**
     * @param el element that needs to be added to the repository
     * @throws CustomException A Exception with duplicated value
     * in the repository
     */
    @Override
    public void add(T el) throws CustomException{
        if (elems.containsKey((el.getId())) ) {
            throw new CustomException("The element is already in the repo.");
        } else {
            elems.put(el.getId(), el);
        }
    }

    /**
     * @param id The id of the element that will be deleted
     * @throws CustomException Element doesn't exist exception
     */
    @Override
    public void delete(ID id) throws CustomException{
        if (elems.remove(id) == null) throw new CustomException("Element not in repo.");
    }

    /**
     * @param el the element that need an update
     * @throws CustomException Element not updated exception
     */
    @Override
    public void update(T el) throws CustomException{
        if (elems.containsKey(el.getId()))
            elems.put(el.getId(), el);
        else
            throw new CustomException("Element not updated.");
    }

    /**
     * @return The entire repository
     */
    @Override
    public Collection<T> getAll() {
        return elems.values();
    }

    /**
     * @param id the id of the element searched
     * @return the id
     * @throws CustomException element not in the repository
     * exception
     */
    @Override
    public T findById(ID id) throws CustomException{
        if (elems.containsKey(id))
            return elems.get(id);
        else
            throw new CustomException("Element not in repo.");
    }
}
