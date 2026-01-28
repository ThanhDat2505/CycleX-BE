package com.example.cyclexbe.repository;

import com.example.cyclexbe.dto.BikeListingDetail;
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
            b.locationCity,
            COALESCE(b.viewsCount, 0)
        )
        FROM BikeListings b
        JOIN b.images i
        WHERE b.status = :status
          AND i.isMain = true
        ORDER BY COALESCE(b.viewsCount, 0) DESC, b.createdAt DESC
    """;

        return entity.createQuery(jpql, BikeListingHomeDTO.class)
                .setParameter("status", "ACTIVE")
                .setMaxResults(8) // Home: top N nổi bật (không phân trang)
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
        b.locationCity,
        COALESCE(b.viewsCount, 0)
    )
    FROM BikeListings b
    LEFT JOIN b.images i WITH i.isMain = true
    WHERE b.status = :status
    ORDER BY b.createdAt DESC
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
        b.locationCity,
        COALESCE(b.viewsCount, 0)
    )
    FROM BikeListings b
    LEFT JOIN b.images i WITH i.isMain = true
    WHERE b.status = :status
      AND LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY b.createdAt DESC
""";


        return entity.createQuery(jpql, BikeListingHomeDTO.class)
                .setParameter("keyword", keyword)
                .setFirstResult(page * size)   // OFFSET
                .setMaxResults(size)           // LIMIT
                .getResultList();
    }

    @Override
    public void increaseview(int listingid) {
        String jpql = """
        UPDATE BikeListings b
        SET b.viewsCount = COALESCE(b.viewsCount, 0) + 1
        WHERE b.listingId = :id
          AND b.status = :status
    """;
        entity.createQuery(jpql)
                .setParameter("id", listingid)
                .setParameter("status", "ACTIVE")
                .executeUpdate();
    }

    @Override
    public BikeListingDetail listingdetail(int listingId) {
        // 1️⃣ Lấy detail listing (KHÔNG join image)
        String detailJpql = """
        SELECT new com.example.cyclexbe.dto.BikeListingDetail(
            b.listingId,
            b.title,
            b.description,
            b.price,
            b.locationCity,
            b.bikeType,
            b.brand,
            COALESCE(b.viewsCount, 0)
        )
        FROM BikeListings b
        WHERE b.listingId = :id
          AND b.status = :status
    """;

        BikeListingDetail dto = entity.createQuery(detailJpql, BikeListingDetail.class)
                .setParameter("id", listingId)
                .setParameter("status", "ACTIVE")
                .getSingleResult();

        // 2️⃣ Lấy danh sách image_url
        String imageJpql = """
        SELECT i.imageUrl
        FROM BikeImage i
        WHERE i.bikeListing.listingId = :id
        ORDER BY i.isMain DESC, i.uploadedAt ASC
    """;

        List<String> images = entity.createQuery(imageJpql, String.class)
                .setParameter("id", listingId)
                .getResultList();

        dto.setImages(images);

        return dto;
    }

}
