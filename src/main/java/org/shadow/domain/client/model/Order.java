package org.shadow.domain.client.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Order(long id, BigDecimal entry, Instant fillingTime, OrderType type) {}
