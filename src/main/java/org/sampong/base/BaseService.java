package org.sampong.base;


import java.util.List;

public interface BaseService<T> {
    T getById(Long id);

    T getByName(String username);

    T addNew(T t);

    T updateObject(T t);

    T delete(Long id);

    List<T> getAll();

    String tableList(List<T> t);
}
