package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByListing_ListingId(Integer listingId);
}
