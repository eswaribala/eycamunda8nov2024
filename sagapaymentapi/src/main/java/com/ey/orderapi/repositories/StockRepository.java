package com.ey.orderapi.repositories;

import com.ey.orderapi.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock,Long> {
}
