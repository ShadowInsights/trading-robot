package org.shadow.infrastructure.db.repository.sqllite;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.shadow.infrastructure.db.entity.OrderEntity;
import org.shadow.infrastructure.db.repository.OrderRepository;

public class SQLLiteOrderRepository implements OrderRepository {

  private final EntityManager entityManager;

  public SQLLiteOrderRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public OrderEntity findById(Long id) {
    return entityManager.find(OrderEntity.class, id);
  }

  @Override
  public void save(OrderEntity order) {
    var tx = entityManager.getTransaction();
    try {
      tx.begin();
      entityManager.persist(order);
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      throw e;
    }
  }

  @Override
  public void update(OrderEntity order) {
    var tx = entityManager.getTransaction();
    try {
      tx.begin();
      entityManager.merge(order);
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      throw e;
    }
  }

  @Override
  public List<OrderEntity> findByStatus(String status) {
    var query =
        entityManager.createQuery(
            "SELECT o FROM OrderEntity o WHERE o.status = :status", OrderEntity.class);
    query.setParameter("status", status);
    return query.getResultList();
  }

  @Override
  public List<OrderEntity> findAll() {
    var query = entityManager.createQuery("SELECT o FROM OrderEntity o", OrderEntity.class);
    return query.getResultList();
  }

  @Override
  public void delete(OrderEntity entity) {
    var tx = entityManager.getTransaction();
    try {
      tx.begin();
      entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      throw e;
    }
  }
}
