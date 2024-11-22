package com.ey.orderapi.repositories;

import com.ey.orderapi.dtos.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
