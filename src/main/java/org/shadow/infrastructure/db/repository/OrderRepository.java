package org.shadow.infrastructure.db.repository;

import java.util.List;
import org.shadow.infrastructure.db.entity.OrderEntity;

/** Order repository interface. */
public interface OrderRepository extends GenericRepository<OrderEntity, Long> {
  /**
   * Find orders by status.
   *
   * @param status Order status.
   * @return List of orders.
   */
  List<OrderEntity> findByStatus(String status);
}
