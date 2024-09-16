package org.shadow.infrastructure.client.fake;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.domain.client.model.Order;
import org.shadow.domain.client.model.OrderType;
import org.shadow.infrastructure.client.fake.exception.InsufficientBalanceException;
import org.shadow.infrastructure.client.fake.exception.OrderNotFoundException;

/** Represents a virtual account managing balance and orders. */
public class VirtualAccount {

  private static final Logger logger = LogManager.getLogger(VirtualAccount.class);
  private static final AtomicLong ORDER_ID_COUNTER = new AtomicLong(1);
  private static final BigDecimal HUNDRED = new BigDecimal("100");
  private static final int SCALE = 10;
  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  private BigDecimal balance;
  private final Map<Long, Order> openOrders;
  private final List<Order> closedOrders;

  /** Initializes the VirtualAccount with the initial balance and empty order lists. */
  public VirtualAccount(BigDecimal initialBalance) {
    this.balance = initialBalance;
    this.openOrders = new ConcurrentHashMap<>();
    this.closedOrders = Collections.synchronizedList(new ArrayList<>());
    logger.info("VirtualAccount initialized with balance: {}", this.balance);
  }

  /**
   * Retrieves the current balance.
   *
   * @return Current balance as BigDecimal.
   */
  public synchronized BigDecimal getBalance() {
    return balance;
  }

  /**
   * Opens a new order, deducts the required amount from the balance, and tracks the order.
   *
   * @param positionType Indicating the type of position.
   * @param entry Entry price of the order.
   * @param percentageFromDeposit Percentage of the deposit to use.
   * @return The newly created Order.
   */
  public synchronized Order openOrder(
      PositionType positionType, BigDecimal entry, BigDecimal percentageFromDeposit) {
    Objects.requireNonNull(entry, "Entry price cannot be null");
    Objects.requireNonNull(percentageFromDeposit, "Percentage from deposit cannot be null");
    Objects.requireNonNull(positionType, "Position type cannot be null");

    logger.debug(
        "Attempting to open {} order with entry: {}, percentageFromDeposit: {}",
        positionType,
        entry,
        percentageFromDeposit);

    var orderCost = balance.multiply(percentageFromDeposit.divide(HUNDRED, SCALE, ROUNDING_MODE));

    logger.debug("Calculated order cost: {}", orderCost);

    if (orderCost.compareTo(balance) > 0) {
      logger.error(
          "Insufficient balance to open order. Available: {}, Required: {}", balance, orderCost);
      throw new InsufficientBalanceException("Insufficient balance to open order.");
    }

    balance = balance.subtract(orderCost);
    logger.info("Deducted {} from balance. New balance: {}", orderCost, balance);

    var quantity = orderCost.divide(entry, SCALE, ROUNDING_MODE);

    var order =
        new Order(
            generateOrderId(),
            entry,
            Instant.now(),
            OrderType.MARKET,
            orderCost,
            positionType,
            quantity);
    openOrders.put(order.id(), order);

    logger.debug("Order created and added to open orders: {}", order);

    return order;
  }

  /**
   * Closes an existing order, calculates profit or loss, adjusts the balance, and moves the order
   * to closed orders.
   *
   * @param order The Order to close.
   * @param exitPrice The price at which the order is being closed.
   */
  public synchronized void closeOrder(Order order, BigDecimal exitPrice) {
    logger.info(
        "Closing order - ID: {}, Type: {}, Exit Price: {}", order.id(), order.type(), exitPrice);

    if (!openOrders.containsKey(order.id())) {
      logger.warn("Attempted to close an order that is not open: {}", order);
      throw new OrderNotFoundException("Order not found or already closed.");
    }

    var entryPrice = order.entry();
    var quantity = order.quantity();
    var positionType = order.positionType();

    var profitOrLoss =
        switch (positionType) {
          case PositionType.LONG -> exitPrice.subtract(entryPrice).multiply(quantity);
          case PositionType.SHORT -> entryPrice.subtract(exitPrice).multiply(quantity);
        };

    balance = balance.add(order.amount()).add(profitOrLoss);
    logger.info("Profit/Loss: {}. New balance: {}", profitOrLoss, balance);

    openOrders.remove(order.id());
    closedOrders.add(order);
    logger.debug("Order closed and moved to closed orders: {}", order);
  }

  /**
   * Retrieves a list of all open orders.
   *
   * @return List of open orders.
   */
  public List<Order> getOpenOrders() {
    return new ArrayList<>(openOrders.values());
  }

  /**
   * Retrieves a list of all closed orders.
   *
   * @return List of closed orders.
   */
  public List<Order> getClosedOrders() {
    return new ArrayList<>(closedOrders);
  }

  /**
   * Generates a unique order ID.
   *
   * @return Unique order ID as long.
   */
  private static long generateOrderId() {
    return ORDER_ID_COUNTER.getAndIncrement();
  }
}
