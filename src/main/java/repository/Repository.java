package repository;

import domain.exception.CustomException;

import java.util.Collection;

public interface Repository<T, ID> {
    void add(T el) throws CustomException;
    void delete(ID id) throws CustomException;
    void update(T el) throws CustomException;
    Collection<T> getAll();
    T findById(ID id) throws CustomException;
}
