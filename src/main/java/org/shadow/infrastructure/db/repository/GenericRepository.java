package org.shadow.infrastructure.db.repository;

import java.io.Serializable;
import java.util.List;

/**
 * Generic repository interface.
 *
 * @param <T> Entity type.
 * @param <I> Entity id type.
 */
public interface GenericRepository<T, I extends Serializable> {
  T findById(I id);

  List<T> findAll();

  void save(T entity);

  void update(T entity);

  void delete(T entity);
}
