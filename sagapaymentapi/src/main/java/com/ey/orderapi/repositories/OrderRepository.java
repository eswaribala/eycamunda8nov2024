package com.ey.orderapi.repositories;

import com.ey.orderapi.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository  extends JpaRepository<Order,Long> {
}
