package com.example.cyclexbe.repository;

import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.entity.BikeListings;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class BikeListingRepositoryImp implements BikeListingRepository {
    private EntityManager entity;
    @Autowired
    public BikeListingRepositoryImp(EntityManager entity) {
        this.entity = entity;
    }

    @Override
    public List<BikeListingHomeDTO> findAll() {
        String jpql = """
    SELECT new com.example.cyclexbe.dto.BikeListingHomeDTO(
        b.listingId,
        b.title,
        b.price,
        i.imageUrl,
        b.locationCity
    )
    FROM BikeListings b
    JOIN b.images i
    WHERE b.status = :status
      AND i.isMain = true
    """;

        return entity
                .createQuery(jpql, BikeListingHomeDTO.class)
                .setParameter("status", "ACTIVE")
                .getResultList();
    }

    @Override
    public List<BikeListingHomeDTO> filterPage(int page,int size) {
        String jpql = """
    SELECT new com.example.cyclexbe.dto.BikeListingHomeDTO(
        b.listingId,
        b.title,
        b.price,
        i.imageUrl,
        b.locationCity
    )
    FROM BikeListings b
    JOIN b.images i
    WHERE b.status = :status
      AND i.isMain = true
    """;
        return entity.createQuery(jpql, BikeListingHomeDTO.class)
                .setParameter("status", "ACTIVE")
                .setFirstResult(page * size)   // OFFSET
                .setMaxResults(size)           // LIMIT
                .getResultList();
    }

    @Override
    public List<BikeListingHomeDTO> search(String keyword, int page, int size) {
        String jpql = """
        SELECT new com.example.cyclexbe.dto.BikeListingHomeDTO(
            b.listingId,
            b.title,
            b.price,
            i.imageUrl,
            b.locationCity
        )
        FROM BikeListings b
        JOIN b.images i
        WHERE b.status = 'ACTIVE'
          AND i.isMain = true
          AND LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY b.createdAt DESC
    """;

        return entity.createQuery(jpql, BikeListingHomeDTO.class)
                .setParameter("keyword", keyword)
                .setFirstResult(page * size)   // OFFSET
                .setMaxResults(size)           // LIMIT
                .getResultList();
    }


}
