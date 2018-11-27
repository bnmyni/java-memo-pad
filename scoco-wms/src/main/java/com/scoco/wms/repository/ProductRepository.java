package com.scoco.wms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoco.wms.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
