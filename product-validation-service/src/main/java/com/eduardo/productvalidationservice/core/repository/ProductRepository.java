package com.eduardo.productvalidationservice.core.repository;

import com.eduardo.productvalidationservice.core.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Boolean existsByCode(String code);
}
