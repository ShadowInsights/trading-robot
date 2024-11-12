package org.shadow.infrastructure.db.repository.sqllite;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.shadow.infrastructure.db.entity.BalanceEntity;
import org.shadow.infrastructure.db.repository.BalanceRepository;

public class SQLLiteBalanceRepository implements BalanceRepository {

  private final EntityManager entityManager;

  public SQLLiteBalanceRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public BalanceEntity findById(Integer id) {
    return entityManager.find(BalanceEntity.class, id);
  }

  @Override
  public void save(BalanceEntity balance) {
    var tx = entityManager.getTransaction();
    try {
      tx.begin();
      entityManager.persist(balance);
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      throw e;
    }
  }

  @Override
  public void update(BalanceEntity balance) {
    var tx = entityManager.getTransaction();
    try {
      tx.begin();
      entityManager.merge(balance);
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      throw e;
    }
  }

  @Override
  public List<BalanceEntity> findAll() {
    return entityManager
        .createQuery("SELECT b FROM BalanceEntity b", BalanceEntity.class)
        .getResultList();
  }

  @Override
  public void delete(BalanceEntity entity) {
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
