package org.sampong.base;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    T save(T t);

    Optional<T> findById(Long id);

    Optional<T> findByName(String name);

    T update(T t);

    List<T> findAll();
}
